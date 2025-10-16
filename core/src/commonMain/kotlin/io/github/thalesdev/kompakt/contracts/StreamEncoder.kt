package io.github.thalesdev.kompakt.contracts

import io.github.thalesdev.kompakt.support.ByteStreamFactory
import io.github.thalesdev.kompakt.support.CompressedStreamData
import io.github.thalesdev.kompakt.support.MetaData

/**
 * Interface for streaming data encoding (compression).
 *
 * Defines the contract for algorithms that can compress data in chunks
 * using Kotlin Flow. This approach is memory-efficient and suitable for
 * large files or continuous data streams that cannot fit entirely in memory.
 *
 * The encoder processes data incrementally, emitting compressed chunks
 * as they become available, which allows for constant memory usage
 * regardless of input size.
 *
 * @param Meta The type of metadata produced during encoding
 *
 * @see StreamDecoder for the corresponding decompression interface
 * @see Encoder for in-memory encoding
 * @see StreamingCodec for complete streaming codec implementations
 */
interface StreamEncoder<Meta : MetaData> {
    /**
     * Encodes (compresses) a stream of data chunks.
     *
     * Accepts a factory function that produces a Flow of byte arrays,
     * allowing the same data source to be processed multiple times if needed
     * (useful for multi-pass compression algorithms).
     *
     * The implementation processes chunks incrementally and produces
     * compressed output along with metadata required for decompression.
     *
     * @param originalSource Factory function that creates a Flow emitting uncompressed data chunks
     * @return [CompressedStreamData] containing a Flow of compressed chunks and metadata
     *
     * Example:
     * ```
     * val encoder: StreamEncoder<HuffmanMeta> = HuffmanAlgorithm()
     * val flowFactory = Path("large-file.txt").asReusableFlow()
     * val compressed = encoder.encode(flowFactory)
     *
     * // Collect and write compressed chunks
     * compressed.data.collect { chunk ->
     *     outputStream.write(chunk)
     * }
     * ```
     */
    suspend fun encode(originalSource: ByteStreamFactory): CompressedStreamData<Meta>
}
