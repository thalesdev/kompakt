package io.github.thalesdev.kompakt.support

import kotlinx.coroutines.flow.Flow

/**
 * Interface for algorithm-specific metadata in compressed files.
 *
 * Implementations of this interface define the metadata structure and
 * serialization logic for specific compression algorithms. Metadata
 * typically includes information needed for decompression, such as:
 * - Algorithm parameters
 * - Code tables (e.g., Huffman trees)
 * - Original data statistics
 * - Compression configuration
 *
 * Each implementation must provide a [FileDescriptor] that defines
 * the binary layout of the metadata fields.
 */
interface MetaData {
    /**
     * Describes the binary structure of this metadata.
     *
     * Defines the layout and types of all metadata fields for
     * serialization and deserialization operations.
     */
    val descriptor: FileDescriptor

    /**
     * Serializes metadata and combines it with compressed data.
     *
     * For synchronous operations, this method prepends or interleaves
     * metadata with the compressed byte array according to the algorithm's
     * file format specification.
     *
     * @param compressedData The compressed data bytes
     * @return Complete byte array with metadata and compressed data
     */
    fun serialize(compressedData: ByteArray): ByteArray

    /**
     * Serializes metadata and combines it with streamed compressed data.
     *
     * For asynchronous streaming operations, this method emits metadata
     * followed by compressed data chunks, or interleaves them according
     * to the algorithm's streaming protocol.
     *
     * @param compressedData Flow of compressed data chunks
     * @return Flow emitting serialized metadata and compressed data
     */
    fun serialize(compressedData: Flow<ByteArray>): Flow<ByteArray>
}
