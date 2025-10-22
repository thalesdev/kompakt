package io.github.thalesdev.kompakt.support

import io.github.thalesdev.kompakt.support.FileDescriptor.Field.CompressedData
import kotlin.reflect.KProperty1
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.Source
import kotlinx.io.readTo

/**
 * Binary deserializer for metadata and compressed data.
 *
 * Reads binary data from a [Source] according to a [FileDescriptor] schema and reconstructs
 * metadata objects along with the compressed data stream.
 *
 * This class handles the reverse operation of [BinarySerializer], parsing fixed-size fields,
 * arrays, maps, and streaming compressed data.
 *
 * @param T The type of metadata to deserialize
 * @property companion Companion object providing the descriptor and restoration logic
 * @see BinarySerializer
 * @see FileDescriptor
 */
@Suppress("UNCHECKED_CAST")
class BinaryDeserializer<T : MetaData>(
    private val companion: MetaDataCompanion<T>,
) {

  /**
   * Deserializes metadata and compressed data from a binary source.
   *
   * Reads the binary stream according to the descriptor schema, extracting all metadata fields and
   * creating a Flow for streaming compressed data.
   *
   * @param input The binary source to read from
   * @return Pair of restored metadata and a Flow emitting compressed data chunks
   * @throws IllegalStateException if compressed data field is missing or invalid
   */
  fun deserialize(input: Source): Pair<T, Flow<ByteArray>> {
    val data = mutableMapOf<String, Any?>()
    var compressedDataFlow: Flow<ByteArray>? = null

    companion.descriptor.fields.forEach { field ->
      when (field) {
        is CompressedData -> {

          val compressedSize = readInt(input, field.lengthPrefix)

          (field.compressedSizeProperty as? KProperty1<MetaData, Int?>?)?.name?.let {
            data[it] = compressedSize
          }

          compressedDataFlow = createCompressedDataFlow(input, compressedSize)
        }

        else -> {
          val property = field.property as? KProperty1<MetaData, Any?>
          data[property?.name ?: field.name] = readField(field, input)
        }
      }
    }

    val meta = companion.restore(data)
    return meta to (compressedDataFlow ?: error("Missing compressed data field"))
  }

  /**
   * Creates a Flow that emits compressed data in chunks.
   *
   * Reads the specified number of bytes from the source in fixed-size chunks, emitting each chunk
   * through the Flow. The final chunk may be smaller than the specified chunk size.
   *
   * @param source The binary source to read from
   * @param totalSize Total number of bytes to read
   * @param chunkSize Maximum size of each emitted chunk (default: 8192)
   * @return Flow emitting byte array chunks
   */
  private fun createCompressedDataFlow(
      source: Source,
      totalSize: Int,
      chunkSize: Int = 8192,
  ): Flow<ByteArray> = flow {
    var remaining = totalSize

    while (remaining > 0) {
      val toRead = minOf(remaining, chunkSize)
      val buffer = ByteArray(toRead)
      source.readAtMostTo(buffer, 0, toRead)
      emit(buffer)
      remaining -= toRead
    }
  }

  /**
   * Reads a field value from the binary source based on its descriptor.
   *
   * Dispatches to the appropriate read method based on the field type.
   *
   * @param field The field descriptor
   * @param input The binary source to read from
   * @return The deserialized field value
   * @throws IllegalStateException if CompressedData field is encountered
   */
  private fun readField(field: FileDescriptor.Field, input: Source): Any? {
    return when (field) {
      is FileDescriptor.Field.MagicNumber -> readInt(input, 4)
      is FileDescriptor.Field.Fixed<*> -> readValue(input, field.sizeInBytes)
      is FileDescriptor.Field.Array<*> -> readArray(input, field.countBytes, field.elementBytes)
      is FileDescriptor.Field.Map<*, *> -> readMap(input, field)
      is CompressedData -> error("Should be handled separately")
    }
  }

  /**
   * Reads a big-endian integer from the source.
   *
   * @param input The binary source to read from
   * @param size Number of bytes to read (1-4)
   * @return The decoded integer value
   */
  private fun readInt(input: Source, size: Int): Int {
    var result = 0
    repeat(size) { result = (result shl 8) or (input.readByte().toInt() and 0xFF) }
    return result
  }

  /**
   * Reads a value from the source based on its size.
   *
   * Automatically determines the appropriate type (Byte, Short, Int, Long, or ByteArray) based on
   * the number of bytes to read.
   *
   * @param input The binary source to read from
   * @param size Number of bytes to read
   * @return The decoded value (Byte, Short, Int, Long, or ByteArray)
   */
  private fun readValue(input: Source, size: Int): Any {
    return when (size) {
      1 -> input.readByte()
      2 -> readInt(input, 2).toShort()
      4 -> readInt(input, 4)
      8 -> readLong(input, 8)
      else -> {
        val buffer = ByteArray(size)
        input.readTo(buffer)
        buffer
      }
    }
  }

  /**
   * Reads a big-endian long from the source.
   *
   * @param input The binary source to read from
   * @param size Number of bytes to read (1-8)
   * @return The decoded long value
   */
  private fun readLong(input: Source, size: Int): Long {
    var result = 0L
    repeat(size) { result = (result shl 8) or (input.readByte().toLong() and 0xFF) }
    return result
  }

  /**
   * Reads an array of fixed-size elements from the source.
   *
   * First reads the element count, then reads each element sequentially.
   *
   * @param input The binary source to read from
   * @param countBytes Number of bytes encoding the element count
   * @param elementBytes Size of each element in bytes
   * @return List of decoded elements
   */
  private fun readArray(input: Source, countBytes: Int, elementBytes: Int): List<Any> {
    val count = readInt(input, countBytes)
    return List(count) { readValue(input, elementBytes) }
  }

  /**
   * Reads a map from the source.
   *
   * First reads the entry count, then reads each key-value pair sequentially.
   *
   * @param input The binary source to read from
   * @param field The map field descriptor containing size information
   * @return Map of decoded key-value pairs
   */
  private fun readMap(input: Source, field: FileDescriptor.Field.Map<*, *>): Map<Any, Any> {
    val count = readInt(input, field.countBytes)
    val map = mutableMapOf<Any, Any>()

    repeat(count) {
      val key = readValue(input, field.keyBytes)
      val value = readValue(input, field.valueBytes)
      map[key] = value
    }

    return map
  }
}
