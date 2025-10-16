package io.github.thalesdev.kompakt.support

import kotlinx.coroutines.flow.Flow

/**
 * Factory function that creates a reusable Flow of byte array chunks.
 *
 * This type alias represents a factory pattern for creating Flows that emit
 * byte arrays. The factory can be invoked multiple times to create new Flow
 * instances, which is useful for multi-pass algorithms that need to process
 * the same data source multiple times.
 *
 * Each invocation of the factory should create a fresh Flow that starts from
 * the beginning of the data source.
 *
 * Example:
 * ```
 * val factory: ByteStreamFactory = Path("file.txt").asReusableFlow()
 *
 * // First pass
 * factory().collect { chunk -> analyzeChunk(chunk) }
 *
 * // Second pass (starts from beginning)
 * factory().collect { chunk -> compressChunk(chunk) }
 * ```
 *
 * @see kotlinx.coroutines.flow.Flow
 */
typealias ByteStreamFactory = () -> Flow<ByteArray>
