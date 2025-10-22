package io.github.thalesdev.kompakt.contracts

import io.github.thalesdev.kompakt.support.Algorithm
import io.github.thalesdev.kompakt.support.MetaData

/**
 * Complete compression/decompression codec for streaming operations.
 *
 * Represents a full bidirectional streaming compression algorithm that can both encode (compress)
 * and decode (decompress) data in chunks using Kotlin Flow. This interface combines [StreamEncoder]
 * and [StreamDecoder] capabilities.
 *
 * Implementations provide complete round-trip streaming compression/decompression with constant
 * memory usage, making them suitable for large files or continuous data streams that cannot fit
 * entirely in memory.
 *
 * @param Meta The type of metadata used by this codec
 * @property type The specific algorithm this codec implements
 * @see CompressionCodec for in-memory compression
 * @see StreamEncoder for compression-only streaming operations
 * @see StreamDecoder for decompression-only streaming operations
 *
 * Example implementation:
 * ```
 * class HuffmanStreamingCodec : StreamingCodec<HuffmanMeta> {
 *     override val type = Algorithm.HUFFMAN
 *
 *     override suspend fun encode(
 *         originalSource: ByteStreamFactory
 *     ): CompressedStreamData<HuffmanMeta> {
 *         // Streaming Huffman encoding implementation
 *     }
 *
 *     override suspend fun decode(
 *         compressed: CompressedStreamData<HuffmanMeta>
 *     ): Flow<ByteArray> {
 *         // Streaming Huffman decoding implementation
 *     }
 * }
 * ```
 *
 * Example usage:
 * ```
 * val codec: StreamingCodec<HuffmanMeta> = HuffmanStreamingCodec()
 * val inputPath = Path("large-file.txt")
 * val outputPath = Path("compressed.bin")
 *
 * // Compress
 * val compressed = codec.encode(inputPath.asReusableFlow())
 * compressed.data.collect { chunk -> writeToFile(outputPath, chunk) }
 *
 * // Decompress
 * val decompressed = codec.decode(compressed)
 * decompressed.collect { chunk -> processDecompressedChunk(chunk) }
 * ```
 */
interface StreamingCodec<Meta : MetaData> : StreamEncoder<Meta>, StreamDecoder<Meta> {
  /**
   * The specific compression algorithm implemented by this codec.
   *
   * This property identifies which algorithm variant this codec represents (e.g., HUFFMAN, LZ77,
   * DEFLATE, etc.).
   */
  val type: Algorithm
}
