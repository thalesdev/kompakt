package io.github.thalesdev.kompakt.support

import io.github.thalesdev.kompakt.support.FileDescriptor.Field
import kotlin.reflect.KProperty1

/**
 * Builder for constructing [FileDescriptor] instances using a DSL.
 *
 * Provides a fluent API for defining the binary structure of metadata, including fixed-size fields,
 * arrays, maps, and compressed data sections.
 *
 * @property fields Mutable list of field descriptors being built
 * @see FileDescriptor
 */
class FileDescriptorBuilder {
  val fields = mutableListOf<Field>()

  /**
   * Adds a fixed-size field to the descriptor.
   *
   * @param property The metadata property to serialize
   * @param sizeInBytes Size of the field in bytes
   */
  fun <T : Any> fixed(property: KProperty1<out MetaData, T>, sizeInBytes: Int) {
    fields.add(Field.Fixed(property, sizeInBytes))
  }

  /**
   * Adds a compressed data field to the descriptor.
   *
   * This field contains the actual compressed data with a length prefix. There should typically be
   * only one compressed data field per descriptor.
   *
   * @param compressedSizeProperty Property containing the compressed data size
   * @param lengthPrefix Number of bytes for the length prefix (default: 4)
   */
  fun <T> compressedData(
      compressedSizeProperty: KProperty1<out MetaData, Int?>,
      lengthPrefix: Int = 4,
  ) {
    fields.add(Field.CompressedData(compressedSizeProperty, lengthPrefix))
  }

  /**
   * Adds an array field to the descriptor.
   *
   * Arrays are stored with an element count prefix followed by the elements.
   *
   * @param property The metadata property containing the array
   * @param countBytes Number of bytes for the element count
   * @param elementBytes Size of each element in bytes
   */
  fun <T> array(
      property: KProperty1<out MetaData, Iterable<T>>,
      countBytes: Int,
      elementBytes: Int,
  ) {
    fields.add(Field.Array(property, countBytes, elementBytes))
  }

  /**
   * Adds a map field to the descriptor with explicit key and value sizes.
   *
   * Maps are stored with an entry count prefix followed by key-value pairs.
   *
   * @param property The metadata property containing the map
   * @param countBytes Number of bytes for the entry count
   * @param keyBytes Size of each key in bytes
   * @param valueBytes Size of each value in bytes
   */
  fun <K, V> map(
      property: KProperty1<out MetaData, kotlin.collections.Map<K, V>>,
      countBytes: Int,
      keyBytes: Int,
      valueBytes: Int,
  ) {
    fields.add(Field.Map<K, V>(property, countBytes, keyBytes, valueBytes))
  }

  /**
   * Adds a map field with automatic size inference using reified types.
   *
   * Key and value sizes are automatically determined from the types K and V using the [sizeOf]
   * function. Supports [Bits] types and standard numeric types.
   *
   * @param K The key type (must be a supported type)
   * @param V The value type (must be a supported type)
   * @param property The metadata property containing the map
   * @param countBytes Number of bytes for the entry count
   *
   * Example:
   * ```
   * map<U08, U16>(MyMeta::frequencies, countBytes = 1)
   * ```
   */
  inline fun <reified K, reified V> map(
      property: KProperty1<out MetaData, Map<*, *>>,
      countBytes: Int,
  ) {
    fields.add(
        Field.Map<K, V>(
            property = property,
            countBytes = countBytes,
            keyBytes = sizeOf<K>(),
            valueBytes = sizeOf<V>(),
        )
    )
  }

  /**
   * Adds a magic number field to the descriptor.
   *
   * Magic numbers are typically placed at the start of files to identify the file format. Always
   * occupies 4 bytes.
   *
   * @param property The metadata property containing the magic number
   */
  fun magicNumber(property: KProperty1<out MetaData, Int>) {
    fields.add(Field.MagicNumber(property))
  }

  /**
   * Builds the [FileDescriptor] from the accumulated field definitions.
   *
   * @return The constructed FileDescriptor
   */
  fun build(): FileDescriptor = FileDescriptor(fields)
}

/**
 * Describes the binary structure of metadata in compressed files.
 *
 * Defines the layout of metadata fields for serialization and deserialization. Fields can be
 * fixed-size, variable-length with a length prefix, or arrays with element count and size
 * information.
 *
 * @property fields List of field descriptors defining the metadata structure
 */
data class FileDescriptor(val fields: List<Field>) {
  /**
   * Base interface for metadata field descriptors.
   *
   * Each field has a name and optionally a size in bytes. Variable-length fields and arrays may not
   * have a predetermined size.
   */
  sealed interface Field {
    /** Unique name identifying this field. */
    val name: String

    /** Size of this field in bytes, or null if variable-length. */
    val sizeInBytes: Int?

    /** The metadata property this field corresponds to. */
    val property: KProperty1<out MetaData, *>?

    /**
     * Magic number field descriptor.
     *
     * Identifies the file format with a 4-byte constant value. Typically placed at the beginning of
     * the file.
     *
     * @property property Kotlin property containing the magic number
     */
    data class MagicNumber(override val property: KProperty1<out MetaData, Int>) : Field {
      override val name: String = "MAGIC_NUMBER"
      override val sizeInBytes: Int = 4
    }

    /**
     * Fixed-size field descriptor.
     *
     * Represents a field with a predetermined, constant size.
     *
     * @property property Kotlin Field descriptor for this field
     * @property sizeInBytes Number of bytes this field occupies
     */
    data class Fixed<T : Any>(
        override val property: KProperty1<out MetaData, T>,
        override val sizeInBytes: Int,
    ) : Field {
      override val name: String = property.name
    }

    /**
     * CompressedData field descriptor.
     *
     * Represents a field that contains compressed data before or after computation. The actual data
     * is preceded by a length value of [lengthPrefix] bytes.
     *
     * @property lengthPrefix Number of bytes used to store the length value
     */
    data class CompressedData(
        val compressedSizeProperty: KProperty1<out MetaData, Int?>,
        val lengthPrefix: Int = 4,
    ) : Field {
      override val sizeInBytes: Int? = null
      override val property: KProperty1<out MetaData, ByteArray>? = null
      override val name: String = "COMPRESSED_DATA"
    }

    /**
     * Array field descriptor.
     *
     * Represents an array of fixed-size elements. The number of elements is stored in [countBytes]
     * bytes, followed by the array elements.
     *
     * @property property Kotlin property containing the iterable
     * @property countBytes Number of bytes used to store the element count
     * @property elementBytes Size of each array element in bytes
     */
    data class Array<T>(
        override val property: KProperty1<out MetaData, Iterable<T>>,
        val countBytes: Int,
        val elementBytes: Int,
    ) : Field {
      override val sizeInBytes: Int? = null
      override val name: String = property.name
    }

    /**
     * Map field descriptor.
     *
     * Represents a map with fixed-size keys and values. The number of entries is stored in
     * [countBytes] bytes, followed by key-value pairs.
     *
     * @property property Kotlin property containing the map
     * @property countBytes Number of bytes for the entry count
     * @property keyBytes Size of each key in bytes
     * @property valueBytes Size of each value in bytes
     */
    data class Map<K, V>(
        override val property: KProperty1<out MetaData, kotlin.collections.Map<*, *>>,
        val countBytes: Int,
        val keyBytes: Int,
        val valueBytes: Int,
    ) : Field {
      override val sizeInBytes: Int = keyBytes + valueBytes
      override val name: String = property.name
    }
  }

  /**
   * Calculates the total size of all fixed-size fields.
   *
   * @return Sum of sizes of all [Field.Fixed] fields in bytes
   */
  fun totalFixedSize(): Int = fields.filterIsInstance<Field.Fixed<*>>().sumOf { it.sizeInBytes }

  companion object {
    /**
     * Creates a FileDescriptor from a vararg of fields.
     *
     * @param fields Field descriptors to include
     * @return A new FileDescriptor
     */
    fun of(vararg fields: Field): FileDescriptor = FileDescriptor(fields.toList())

    /**
     * Creates a FileDescriptor using a DSL builder.
     *
     * @param block Builder configuration block
     * @return A new FileDescriptor
     *
     * Example:
     * ```
     * val descriptor = FileDescriptor.descriptor {
     *     magicNumber(MyMeta::magic)
     *     fixed(MyMeta::version, sizeInBytes = 2)
     *     map<U08, U16>(MyMeta::frequencies, countBytes = 1)
     *     compressedData(MyMeta::compressedSize, lengthPrefix = 4)
     * }
     * ```
     */
    fun descriptor(block: FileDescriptorBuilder.() -> Unit): FileDescriptor {
      return FileDescriptorBuilder().apply(block).build()
    }
  }
}
