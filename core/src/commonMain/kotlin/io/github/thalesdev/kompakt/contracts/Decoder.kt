package io.github.thalesdev.kompakt.contracts

import io.github.thalesdev.kompakt.support.MetaData
import io.github.thalesdev.kompakt.support.ResultData

/**
 * Interface for synchronous data decoding (decompression).
 *
 * Defines the contract for algorithms that can decompress byte arrays in-memory. Implementations
 * should handle the entire compressed data at once, making this suitable for small to medium-sized
 * data that fits in memory.
 *
 * For large files or streaming scenarios, consider using [StreamDecoder] instead.
 *
 * @param Meta The type of metadata required for decoding
 * @see Encoder for the corresponding compression interface
 * @see StreamDecoder for streaming/chunked decoding
 * @see CompressionCodec for complete codec implementations
 */
interface Decoder<Meta : MetaData> {
  /**
   * Decodes (decompresses) the given compressed data.
   *
   * Processes the entire compressed input using the provided metadata and reconstructs the original
   * uncompressed data.
   *
   * @param compressed The compressed data with metadata
   * @return The original uncompressed byte array
   *
   * Example:
   * ```
   * val decoder: Decoder<HuffmanMeta> = HuffmanAlgorithm()
   * val decompressed = decoder.decode(compressed)
   * val text = decompressed.decodeToString()
   * ```
   */
  suspend fun decode(compressed: ResultData<Meta>): ByteArray
}
