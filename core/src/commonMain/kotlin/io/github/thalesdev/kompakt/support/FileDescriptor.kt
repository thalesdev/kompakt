package io.github.thalesdev.kompakt.support

/**
 * Describes the binary structure of metadata in compressed files.
 *
 * Defines the layout of metadata fields for serialization and deserialization.
 * Fields can be fixed-size, variable-length with a length prefix, or arrays
 * with element count and size information.
 *
 * @property fields List of field descriptors defining the metadata structure
 */
data class FileDescriptor(
    val fields: List<Field>
) {
    /**
     * Base interface for metadata field descriptors.
     *
     * Each field has a name and optionally a size in bytes. Variable-length
     * fields and arrays may not have a predetermined size.
     */
    sealed interface Field {
        /**
         * Unique name identifying this field.
         */
        val name: String

        /**
         * Size of this field in bytes, or null if variable-length.
         */
        val sizeInBytes: Int?

        /**
         * Fixed-size field descriptor.
         *
         * Represents a field with a predetermined, constant size.
         *
         * @property name Field identifier
         * @property sizeInBytes Number of bytes this field occupies
         */
        data class Fixed(
            override val name: String,
            override val sizeInBytes: Int
        ) : Field

        /**
         * Variable-length field descriptor.
         *
         * Represents a field whose size is determined at runtime by a length prefix.
         * The actual data is preceded by a length value of [lengthPrefix] bytes.
         *
         * @property name Field identifier
         * @property lengthPrefix Number of bytes used to store the length value
         */
        data class Variable(
            override val name: String,
            val lengthPrefix: Int
        ) : Field {
            override val sizeInBytes: Int? = null
        }

        /**
         * Array field descriptor.
         *
         * Represents an array of fixed-size elements. The number of elements
         * is stored in [countBytes] bytes, followed by the array elements.
         *
         * @property name Field identifier
         * @property countBytes Number of bytes used to store the element count
         * @property elementBytes Size of each array element in bytes
         */
        data class Array(
            override val name: String,
            val countBytes: Int,
            val elementBytes: Int
        ) : Field {
            override val sizeInBytes: Int? = null
        }
    }

    /**
     * Calculates the total size of all fixed-size fields.
     *
     * @return Sum of sizes of all [Field.Fixed] fields in bytes
     */
    fun totalFixedSize(): Int =
        fields.filterIsInstance<Field.Fixed>().sumOf { it.sizeInBytes }
}