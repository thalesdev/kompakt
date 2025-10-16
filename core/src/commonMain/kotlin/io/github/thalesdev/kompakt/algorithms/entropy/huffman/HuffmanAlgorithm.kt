package io.github.thalesdev.kompakt.algorithms.entropy.huffman

import io.github.thalesdev.kompakt.algorithms.entropy.huffman.FrequencyAnalyzer.countFrequencies
import io.github.thalesdev.kompakt.algorithms.entropy.huffman.FrequencyAnalyzer.nonZeroEntries
import io.github.thalesdev.kompakt.algorithms.entropy.huffman.FrequencyAnalyzer.totalBits
import io.github.thalesdev.kompakt.contracts.CompressionCodec
import io.github.thalesdev.kompakt.contracts.StreamingCodec
import io.github.thalesdev.kompakt.support.Algorithm
import io.github.thalesdev.kompakt.support.ByteStreamFactory
import io.github.thalesdev.kompakt.support.CompressedData
import io.github.thalesdev.kompakt.support.CompressedStreamData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Huffman coding algorithm for lossless data compression.
 *
 * Developed by David A. Huffman in 1952 at MIT, this algorithm builds an optimal prefix-free
 * binary code based on symbol frequencies in the input data.
 *
 * ## Algorithm Definition
 * Given:
 * - A set of symbols **S**
 * - For each symbol **x ∈ S**, a frequency **fₓ** representing the fraction of occurrences
 *   of **x** in the input data
 *
 * The algorithm constructs a binary tree where:
 * - More frequent symbols have shorter codes
 * - Less frequent symbols have longer codes
 * - No code is a prefix of another (prefix-free property)
 *
 * ## Complexity
 * - Time: O(n log n) where n is the number of unique symbols
 * - Space: O(n) for the tree structure
 *
 * ## Example
 * ```
 * Input: "AAABBC"
 * Frequencies: A=3, B=2, C=1
 * Possible codes: A=0, B=10, C=11
 * Compressed: 000101011 (9 bits vs 18 bits original)
 * ```
 * @since 0.0.1
 * @see <a href="https://en.wikipedia.org/wiki/Huffman_coding">Huffman Coding - Wikipedia</a>
 */
class HuffmanAlgorithm : CompressionCodec<HuffmanMeta>, StreamingCodec<HuffmanMeta> {
    override val type: Algorithm = Algorithm.HUFFMAN

    override suspend fun encode(originalSource: ByteArray): CompressedData<HuffmanMeta> {
        val frequencies = originalSource.countFrequencies()
        val tree = HuffmanPriorityQueue(frequencies).build()
            ?: throw IllegalStateException("No Huffman tree found")
        val codeTable = HuffmanCodeTable(tree).build()

        val (encoded, paddingBits) = encodeBytes(originalSource, codeTable)

        return CompressedData(
            encoded,
            meta = HuffmanMeta(
                paddingBits = paddingBits,
                frequencies = frequencies.nonZeroEntries().associate { it.index to it.value },
            )
        )
    }

    override suspend fun encode(originalSource: ByteStreamFactory): CompressedStreamData<HuffmanMeta> {
        val frequencies = originalSource().countFrequencies()
        val tree = HuffmanPriorityQueue(frequencies).build()
            ?: throw IllegalStateException("No Huffman tree found")
        val codeTable = HuffmanCodeTable(tree).build()

        val totalBits = frequencies.totalBits(codeTable)
        val paddingBits = if (totalBits % 8 == 0L) 0 else (8 - (totalBits % 8)).toInt()

        return CompressedStreamData(
            encodeStreamBytes(originalSource, codeTable),
            meta = HuffmanMeta(
                paddingBits = paddingBits,
                frequencies = frequencies.nonZeroEntries().associate { it.index to it.value },
                compressedSize = ((totalBits + paddingBits) / 8).toInt(),
            )
        )
    }

    override suspend fun decode(compressed: CompressedData<HuffmanMeta>): ByteArray {
        TODO("Not yet implemented")
    }

    override suspend fun decode(compressed: CompressedStreamData<HuffmanMeta>): Flow<ByteArray> {
        TODO("Not yet implemented")
    }


    private fun encodeBytes(
        uncompressed: ByteArray,
        table: Map<Int, HuffmanCode>,
    ): Pair<ByteArray, Int> {
        val writer = HuffmanCodeWriter(table, bufferSize = uncompressed.size)
        writer.write(uncompressed)
        return writer.finish()
    }

    private fun encodeStreamBytes(
        source: ByteStreamFactory,
        table: Map<Int, HuffmanCode>
    ) = flow {
        val writer = HuffmanCodeWriter(table)

        source().collect { chunk ->
            writer.write(chunk)
            writer.drainIfNeeded()?.let { emit(it) }
        }

        val (final, _) = writer.finish()
        if (final.isNotEmpty()) {
            emit(final)
        }
    }
}