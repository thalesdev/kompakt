package io.github.thalesdev.kompakt.algorithms.entropy.huffman

import io.github.thalesdev.kompakt.util.BitWriter

/**
 * High-performance writer for Huffman-encoded data with optimized loop unrolling.
 *
 * Combines a lookup table for O(1) code access with a bit-level writer, processing input bytes in
 * batches of 4 to maximize CPU pipeline utilization.
 *
 * @property codeTable Map of byte values (0-255) to their Huffman codes
 * @property bufferSize Size of the internal buffer in bytes
 */
class HuffmanCodeWriter(codeTable: Map<Int, HuffmanCode>, bufferSize: Int = DEFAULT_BUFFER_SIZE) {
  private val lookupTable: Array<HuffmanCode> = buildLookupTable(codeTable)
  private val writer = BitWriter(bufferSize)

  /**
   * Writes a chunk of bytes using Huffman encoding.
   *
   * Processes bytes in batches of 4 for optimal performance through loop unrolling.
   *
   * @param chunk Input bytes to encode
   */
  fun write(chunk: ByteArray) {
    var i = 0
    val size = chunk.size
    val limit = size - 3

    while (i < limit) {
      val code0 = lookupTable[chunk[i].toInt() and 0xFF]
      val code1 = lookupTable[chunk[i + 1].toInt() and 0xFF]
      val code2 = lookupTable[chunk[i + 2].toInt() and 0xFF]
      val code3 = lookupTable[chunk[i + 3].toInt() and 0xFF]

      writer.writeFast(code0.bits, code0.length)
      writer.writeFast(code1.bits, code1.length)
      writer.writeFast(code2.bits, code2.length)
      writer.writeFast(code3.bits, code3.length)

      i += 4
    }

    while (i < size) {
      val code = lookupTable[chunk[i].toInt() and 0xFF]
      writer.writeFast(code.bits, code.length)
      i++
    }
  }

  /**
   * Drains the internal buffer if it's approaching capacity.
   *
   * @return ByteArray containing buffered data if drained, null otherwise
   */
  fun drainIfNeeded(): ByteArray? = writer.drainIfFull()

  /**
   * Finalizes encoding and returns all remaining buffered data.
   *
   * @return Pair of (bytes, paddingBits) where paddingBits is the number of padding bits added
   */
  fun finish(): Pair<ByteArray, Int> = writer.finish()

  private fun buildLookupTable(table: Map<Int, HuffmanCode>): Array<HuffmanCode> {
    return Array(LOOKUP_TABLE_SIZE) { i -> table[i] ?: HuffmanCode(0, 0) }
  }

  companion object {
    private const val LOOKUP_TABLE_SIZE = 256
    private const val DEFAULT_BUFFER_SIZE = 8 * 1024 * 1024
  }
}
