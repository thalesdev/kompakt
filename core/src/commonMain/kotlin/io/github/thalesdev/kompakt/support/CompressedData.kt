package io.github.thalesdev.kompakt.support

/**
 * Container for compressed data with associated metadata.
 *
 * Holds the compressed byte array along with algorithm-specific metadata
 * required for decompression. This is used for synchronous compression
 * operations where all data fits in memory.
 *
 * @param Meta The type of metadata, must extend [MetaData]
 * @property data The compressed byte array
 * @property meta Algorithm-specific metadata for decompression
 *
 * @see CompressedStreamData for streaming compression operations
 */
data class CompressedData<Meta : MetaData>(
    val data: ByteArray,
    val meta: Meta
) {
    /**
     * Compares this compressed data with another object for equality.
     *
     * Two CompressedData instances are equal if their byte arrays have
     * identical content and their metadata are equal.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CompressedData<*>

        if (!data.contentEquals(other.data)) return false
        if (meta != other.meta) return false

        return true
    }

    /**
     * Computes hash code based on data content and metadata.
     */
    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + meta.hashCode()
        return result
    }
}