package io.github.thalesdev.kompakt.contracts

import io.github.thalesdev.kompakt.support.MetaData
import io.github.thalesdev.kompakt.support.ResultStreamData
import kotlinx.coroutines.flow.Flow

/**
 * Interface for streaming data decoding (decompression).
 *
 * Defines the contract for algorithms that can decompress data in chunks using Kotlin Flow. This
 * approach is memory-efficient and suitable for large compressed files or continuous data streams
 * that cannot fit entirely in memory.
 *
 * The decoder processes compressed chunks incrementally, emitting decompressed data as it becomes
 * available, which allows for constant memory usage regardless of compressed file size.
 *
 * @param Meta The type of metadata required for decoding
 * @see StreamEncoder for the corresponding compression interface
 * @see Decoder for in-memory decoding
 * @see StreamingCodec for complete streaming codec implementations
 */
interface StreamDecoder<Meta : MetaData> {
  /**
   * Decodes (decompresses) a stream of compressed data chunks.
   *
   * Processes compressed data incrementally using the provided metadata and emits decompressed
   * chunks as they are produced.
   *
   * The implementation should handle chunk boundaries appropriately, maintaining any necessary
   * state between chunks to ensure correct decompression.
   *
   * @param compressed The compressed stream data with metadata
   * @return Flow emitting decompressed byte array chunks
   *
   * Example:
   * ```
   * val decoder: StreamDecoder<HuffmanMeta> = HuffmanAlgorithm()
   * val decompressedFlow = decoder.decode(compressedStream)
   *
   * // Collect and process decompressed chunks
   * decompressedFlow.collect { chunk ->
   *     outputStream.write(chunk)
   * }
   * ```
   */
  suspend fun decode(compressed: ResultStreamData<Meta>): Flow<ByteArray>
}
