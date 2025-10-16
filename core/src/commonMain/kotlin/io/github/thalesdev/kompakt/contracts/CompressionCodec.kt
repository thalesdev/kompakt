package io.github.thalesdev.kompakt.contracts

import io.github.thalesdev.kompakt.support.Algorithm
import io.github.thalesdev.kompakt.support.MetaData

/**
 * Complete compression/decompression codec for in-memory operations.
 *
 * Represents a full bidirectional compression algorithm that can both
 * encode (compress) and decode (decompress) data. This interface combines
 * [Encoder] and [Decoder] capabilities into a single codec.
 *
 * Implementations provide complete round-trip compression/decompression
 * for data that fits in memory. For large files or streaming scenarios,
 * use [StreamingCodec] instead.
 *
 * @param Meta The type of metadata used by this codec
 * @property type The specific algorithm this codec implements
 *
 * @see StreamingCodec for streaming/chunked compression
 * @see Encoder for compression-only operations
 * @see Decoder for decompression-only operations
 *
 * Example implementation:
 * ```
 * class HuffmanCodec : CompressionCodec<HuffmanMeta> {
 *     override val type = Algorithm.HUFFMAN
 *
 *     override suspend fun encode(originalSource: ByteArray): CompressedData<HuffmanMeta> {
 *         // Huffman encoding implementation
 *     }
 *
 *     override suspend fun decode(compressed: CompressedData<HuffmanMeta>): ByteArray {
 *         // Huffman decoding implementation
 *     }
 * }
 * ```
 */
interface CompressionCodec<Meta : MetaData> : Encoder<Meta>, Decoder<Meta> {
    /**
     * The specific compression algorithm implemented by this codec.
     *
     * This property identifies which algorithm variant this codec represents
     * (e.g., HUFFMAN, LZ77, DEFLATE, etc.).
     */
    val type: Algorithm
}
