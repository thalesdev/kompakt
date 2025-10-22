package io.github.thalesdev.kompakt.algorithms.entropy.huffman

import io.github.thalesdev.kompakt.util.unsigned
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce

/**
 * Analyzes byte frequency distributions for entropy coding algorithms.
 *
 * Provides utilities to count byte occurrences in data streams, which is essential for building
 * optimal Huffman trees and other statistical encoders.
 */
internal object FrequencyAnalyzer {
  private const val BYTE_RANGE = 256

  /**
   * Counts frequency of each byte value in the array.
   *
   * @return IntArray of size 256 where index represents byte value
   * @receiver ByteArray to analyze
   */
  fun ByteArray.countFrequencies(): IntArray =
      IntArray(BYTE_RANGE).apply { this@countFrequencies.forEach { this[it.unsigned]++ } }

  /**
   * Counts frequency of each byte value across a flow of byte arrays.
   *
   * @return IntArray of size 256 with accumulated frequencies
   * @receiver Flow of ByteArray chunks
   */
  suspend fun Flow<ByteArray>.countFrequencies(): IntArray =
      map { chunk -> chunk.countFrequencies() }
          .reduce { a, b -> a.zip(b) { x, y -> x + y }.toIntArray() }

  /**
   * Filters frequency array to only non-zero entries with their indices.
   *
   * @return List of IndexedValue for bytes that appear in the data
   * @receiver IntArray of frequencies
   */
  fun IntArray.nonZeroEntries(): List<IndexedValue<Int>> = withIndex().filter { it.value > 0 }

  /**
   * Calculates total bits needed to encode data with given Huffman codes.
   *
   * @param codeTable Map from byte value to its Huffman code
   * @return Total number of bits required
   * @receiver IntArray of frequencies
   */
  fun IntArray.totalBits(codeTable: Map<Int, HuffmanCode>): Long =
      nonZeroEntries().sumOf { (byte, count) -> codeTable[byte]!!.length.toLong() * count }
}
