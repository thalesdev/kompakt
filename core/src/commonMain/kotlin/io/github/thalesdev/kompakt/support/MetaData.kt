package io.github.thalesdev.kompakt.support

/**
 * Interface for algorithm-specific metadata in compressed files.
 *
 * Implementations of this interface define the metadata structure and serialization logic for
 * specific compression algorithms. Metadata typically includes information needed for
 * decompression, such as:
 * - Algorithm parameters
 * - Code tables (e.g., Huffman trees)
 * - Original data statistics
 * - Compression configuration
 *
 * Each implementation must provide a companion object implementing [MetaDataCompanion] with a
 * [FileDescriptor] that defines the binary layout of the metadata fields.
 *
 * @property magic Four-byte magic number identifying the file format
 * @property version Version number of the metadata format
 * @see MetaDataCompanion
 * @see FileDescriptor
 */
interface MetaData {

  val magic: Int

  val version: Short
}

/**
 * Type alias for the payload used during metadata restoration from binary data.
 *
 * Maps field names to their deserialized values.
 */
typealias MetaDataRestorePayload = Map<String, Any?>

/**
 * Companion interface for metadata types.
 *
 * Implementations provide the file descriptor schema and restoration logic needed for serialization
 * and deserialization.
 *
 * @param T The metadata type
 * @property descriptor File descriptor defining the binary structure
 * @see FileDescriptor
 */
interface MetaDataCompanion<T : MetaData> {
  val descriptor: FileDescriptor

  /**
   * Restores a metadata instance from deserialized field values.
   *
   * @param data Map of field names to their deserialized values
   * @return Reconstructed metadata instance
   */
  fun restore(data: MetaDataRestorePayload): T
}

/**
 * Extension function to safely retrieve and convert a value from the restore payload.
 *
 * Performs automatic type conversion for numeric types (Int, Long, Byte, Short) to handle
 * differences in binary representation sizes.
 *
 * @param T The target type
 * @param key The field name to retrieve
 * @return The converted value
 * @throws IllegalStateException if the key is missing or conversion fails
 *
 * Example:
 * ```
 * val paddingBits = data.getAs<Int>("paddingBits")
 * val frequencies = data.getAs<Map<Int, Int>>("frequencies")
 * ```
 */
inline fun <reified T> MetaDataRestorePayload.getAs(key: String): T {
  val value = this[key] ?: error("Missing key: $key")

  return when (T::class) {
    Int::class ->
        when (value) {
          is Int -> value
          is Byte -> value.toInt() and 0xFF
          is Short -> value.toInt()
          is Number -> value.toInt()
          else -> error("Cannot convert ${value::class} to Int")
        }
            as T

    Long::class ->
        when (value) {
          is Long -> value
          is Int -> value.toLong()
          is Byte -> value.toLong() and 0xFF
          is Number -> value.toLong()
          else -> error("Cannot convert ${value::class} to Long")
        }
            as T

    else -> value as T
  }
}
