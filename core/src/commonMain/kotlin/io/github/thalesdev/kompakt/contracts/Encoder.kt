package io.github.thalesdev.kompakt.contracts

import io.github.thalesdev.kompakt.support.MetaData
import io.github.thalesdev.kompakt.support.ResultData

/**
 * Interface for synchronous data encoding (compression).
 *
 * Defines the contract for algorithms that can compress byte arrays in-memory. Implementations
 * should handle the entire data at once, making this suitable for small to medium-sized data that
 * fits in memory.
 *
 * For large files or streaming scenarios, consider using [StreamEncoder] instead.
 *
 * @param Meta The type of metadata produced during encoding
 * @see Decoder for the corresponding decompression interface
 * @see StreamEncoder for streaming/chunked encoding
 * @see CompressionCodec for complete codec implementations
 */
interface Encoder<Meta : MetaData> {
  /**
   * Encodes (compresses) the given byte array.
   *
   * Processes the entire input data and produces compressed output along with algorithm-specific
   * metadata required for decompression.
   *
   * @param originalSource The uncompressed data to encode
   * @return [ResultData] containing compressed bytes and metadata
   *
   * Example:
   * ```
   * val encoder: Encoder<HuffmanMeta> = HuffmanAlgorithm()
   * val original = "Hello World".encodeToByteArray()
   * val compressed = encoder.encode(original)
   * ```
   */
  suspend fun encode(originalSource: ByteArray): ResultData<Meta>
}
