package io.github.thalesdev.kompakt.util

import io.github.thalesdev.kompakt.support.ByteStreamFactory
import io.github.thalesdev.kompakt.util.ChunkSize.DEFAULT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

/**
 * Predefined chunk sizes for streaming operations.
 *
 * Provides common buffer sizes optimized for different use cases:
 * - SMALL: For memory-constrained environments or many concurrent streams
 * - DEFAULT: Balanced performance for most scenarios
 * - LARGE: Better throughput for larger files
 * - XLARGE: Maximum throughput for very large files with ample memory
 */
object ChunkSize {
    /**
     * Small chunk size (8 KB) - suitable for low memory scenarios.
     */
    const val SMALL = 8 * 1024

    /**
     * Default chunk size (64 KB) - balanced performance.
     */
    const val DEFAULT = 64 * 1024

    /**
     * Large chunk size (256 KB) - higher throughput.
     */
    const val LARGE = 256 * 1024

    /**
     * Extra large chunk size (1 MB) - maximum throughput.
     */
    const val XLARGE = 1024 * 1024
}

/**
 * Creates a reusable Flow factory for reading file contents in chunks.
 *
 * Returns a lambda that creates a new Flow each time it's invoked, allowing
 * the file to be read multiple times. The Flow emits byte arrays of the specified
 * chunk size, with the last chunk potentially being smaller.
 *
 * This is useful for scenarios where you need to process the same file multiple times
 * (e.g., multi-pass compression algorithms) without loading the entire file into memory.
 *
 * @param chunkSize Size of chunks to read (default: 64 KB)
 * @return A factory function that creates a new Flow<ByteArray> on each invocation
 *
 * Example:
 * ```
 * val flowFactory = Path("large-file.bin").asReusableFlow()
 *
 * // First pass
 * flowFactory().collect { chunk -> processFirstPass(chunk) }
 *
 * // Second pass on the same file
 * flowFactory().collect { chunk -> processSecondPass(chunk) }
 * ```
 */
fun Path.asReusableFlow(chunkSize: Int = DEFAULT): ByteStreamFactory = {
    flow {
        SystemFileSystem.source(this@asReusableFlow).buffered().use { source ->
            while (true) {
                val buffer = ByteArray(chunkSize)
                val read = source.readAtMostTo(buffer)
                if (read == -1) break
                if (read < chunkSize) {
                    emit(buffer.copyOf(read))
                } else {
                    emit(buffer)
                }
            }
        }
    }
}