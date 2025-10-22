package io.github.thalesdev.kompakt.support

import kotlinx.coroutines.flow.Flow

/**
 * Container for streaming compressed data with associated metadata.
 *
 * Holds a Flow of compressed byte chunks along with algorithm-specific metadata. This is used for
 * asynchronous streaming compression operations where data is processed in chunks without loading
 * everything into memory.
 *
 * The Flow can be collected multiple times if the underlying source supports it, but this behavior
 * depends on the specific Flow implementation.
 *
 * @param Meta The type of metadata, must extend [MetaData]
 * @property data Flow that emits compressed byte chunks
 * @property meta Algorithm-specific metadata for decompression
 * @see ResultData for synchronous compression operations
 */
data class ResultStreamData<Meta : MetaData>(val data: Flow<ByteArray>, val meta: Meta)
