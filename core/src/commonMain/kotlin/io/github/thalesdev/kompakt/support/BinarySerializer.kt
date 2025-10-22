package io.github.thalesdev.kompakt.support

import io.github.thalesdev.kompakt.support.FileDescriptor.Field.CompressedData
import kotlin.reflect.KProperty1
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.readByteArray

/**
 * Binary serializer for metadata and compressed data.
 *
 * Writes metadata and compressed data to binary format according to a [FileDescriptor] schema.
 * Supports both synchronous (Sink-based) and asynchronous (Flow-based) serialization.
 *
 * This class handles serialization of fixed-size fields, arrays, maps, and streaming compressed
 * data in big-endian byte order.
 *
 * @property metadata The metadata object to serialize
 * @property descriptor The file descriptor defining the binary structure
 * @see BinaryDeserializer
 * @see FileDescriptor
 */
@Suppress("UNCHECKED_CAST")
class BinarySerializer(private val metadata: MetaData, private val descriptor: FileDescriptor) {

  /**
   * Serializes metadata and compressed data to a binary sink.
   *
   * Writes all metadata fields followed by the compressed data according to the descriptor schema.
   * Used for synchronous, in-memory serialization.
   *
   * @param data The compressed byte array to write
   * @param output The sink to write to
   */
  fun serialize(data: ByteArray, output: Sink) {
    descriptor.fields.forEach { field ->
      when (field) {
        is CompressedData -> writeCompressedData(data, field.lengthPrefix, output)

        else -> writeField(field, output)
      }
    }
  }

  /**
   * Serializes metadata and compressed data stream to a Flow of byte chunks.
   *
   * Processes metadata fields and compressed data chunks incrementally, emitting serialized bytes
   * as they become available. Used for streaming compression where data doesn't fit in memory.
   *
   * @param data Flow of compressed byte chunks to serialize
   * @return Flow emitting serialized byte chunks
   * @throws IllegalStateException if compressed size property is missing
   */
  fun serialize(data: Flow<ByteArray>): Flow<ByteArray> = flow {
    val buffer = Buffer()
    descriptor.fields.forEach { field ->
      when (field) {
        is CompressedData -> {
          val compressedSize =
              (field.compressedSizeProperty as KProperty1<MetaData, Int?>?)?.get(metadata)
                  ?: error("Missing compressed size property")

          emitCompressedData(data, field.lengthPrefix, compressedSize)
        }

        else ->
            writeField(field, buffer).also { emit(buffer.readByteArray()).also { buffer.clear() } }
      }
    }
  }

  /**
   * Writes a single field to the output sink.
   *
   * Extracts the field value from metadata and dispatches to the appropriate write method based on
   * field type.
   *
   * @param field The field descriptor
   * @param output The sink to write to
   * @throws IllegalStateException if CompressedData field is encountered (should be handled
   *   separately)
   */
  private fun writeField(field: FileDescriptor.Field, output: Sink) {
    val property = field.property as KProperty1<MetaData, Any?>?
    val value = property?.get(metadata)

    return when (field) {
      is FileDescriptor.Field.MagicNumber -> {
        writeInt(value as Int, 4, output)
      }

      is FileDescriptor.Field.Fixed<*> -> {
        writeValue(value, field.sizeInBytes, output)
      }

      is CompressedData -> error("Compressed data should be handled by the serialize method")

      is FileDescriptor.Field.Array<*> -> {
        writeArray(value as Iterable<*>, field.countBytes, field.elementBytes, output)
      }

      is FileDescriptor.Field.Map<*, *> -> {
        writeMap(value as kotlin.collections.Map<*, *>, field, output)
      }
    }
  }

  /**
   * Writes a value to the output sink based on its type and size.
   *
   * Handles various numeric types (Int, Long, Short, Byte) and byte arrays, writing them in
   * big-endian format.
   *
   * @param value The value to write
   * @param size Number of bytes to write
   * @param output The sink to write to
   * @throws IllegalStateException if value type is unsupported
   */
  private fun writeValue(value: Any?, size: Int, output: Sink) {
    when (value) {
      is Int -> writeInt(value, size, output)
      is Long -> writeLong(value, size, output)
      is Short -> writeInt(value.toInt(), size, output)
      is UShort -> writeInt(value.toInt(), size, output)
      is Byte -> output.writeByte(value)
      is UByte -> output.writeByte(value.toByte())
      is ByteArray -> output.write(value)
      else -> error("Unsupported type for fixed field: ${value?.let { it::class }}")
    }
  }

  /**
   * Writes an integer in big-endian format.
   *
   * @param value The integer value to write
   * @param size Number of bytes to write (1-4)
   * @param output The sink to write to
   */
  private fun writeInt(value: Int, size: Int, output: Sink) {
    for (i in size - 1 downTo 0) {
      output.writeByte(((value shr (i * 8)) and 0xFF).toByte())
    }
  }

  /**
   * Writes a long in big-endian format.
   *
   * @param value The long value to write
   * @param size Number of bytes to write (1-8)
   * @param output The sink to write to
   */
  private fun writeLong(value: Long, size: Int, output: Sink) {
    for (i in size - 1 downTo 0) {
      output.writeByte(((value shr (i * 8)) and 0xFF).toByte())
    }
  }

  /**
   * Writes compressed data with a length prefix.
   *
   * First writes the data length in the specified number of bytes, then writes the actual data.
   *
   * @param data The compressed data to write
   * @param lengthPrefix Number of bytes for the length prefix
   * @param output The sink to write to
   */
  private fun writeCompressedData(data: ByteArray, lengthPrefix: Int, output: Sink) {
    writeInt(data.size, lengthPrefix, output)
    output.write(data)
  }

  /**
   * Emits compressed data stream with a length prefix.
   *
   * First emits the compressed size as a length prefix, then emits all chunks from the data Flow.
   *
   * @param data Flow of compressed data chunks
   * @param lengthPrefix Number of bytes for the length prefix
   * @param compressedSize Total size of compressed data
   * @receiver FlowCollector to emit byte chunks to
   */
  private suspend fun FlowCollector<ByteArray>.emitCompressedData(
      data: Flow<ByteArray>,
      lengthPrefix: Int,
      compressedSize: Int,
  ) {
    emit(Buffer().also { writeInt(compressedSize, lengthPrefix, it) }.readByteArray())
    emitAll(data)
  }

  /**
   * Writes an array with element count prefix.
   *
   * First writes the number of elements, then writes each element sequentially.
   *
   * @param items The iterable of items to write
   * @param countBytes Number of bytes for the count prefix
   * @param elementBytes Size of each element in bytes
   * @param output The sink to write to
   */
  private fun writeArray(items: Iterable<*>, countBytes: Int, elementBytes: Int, output: Sink) {
    val list = items.toList()
    writeInt(list.size, countBytes, output)

    list.forEach { element -> writeValue(element, elementBytes, output) }
  }

  /**
   * Writes a map with entry count prefix.
   *
   * First writes the number of entries, then writes each key-value pair sequentially.
   *
   * @param map The map to write
   * @param field The map field descriptor containing size information
   * @param output The sink to write to
   */
  private fun writeMap(
      map: kotlin.collections.Map<*, *>,
      field: FileDescriptor.Field.Map<*, *>,
      output: Sink,
  ) {
    writeInt(map.size, field.countBytes, output)

    map.forEach { (key, value) ->
      writeValue(key, field.keyBytes, output)
      writeValue(value, field.valueBytes, output)
    }
  }
}
