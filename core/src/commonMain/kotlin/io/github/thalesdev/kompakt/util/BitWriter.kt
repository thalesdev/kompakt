package io.github.thalesdev.kompakt.util

/**
 * High-performance bit-level writer with buffering and optimized flush operations.
 *
 * Accumulates bits in a 64-bit buffer and flushes to byte array when necessary. Optimized for
 * minimal branching and efficient CPU pipeline utilization.
 *
 * @property bufferSize Size of the internal byte buffer (default 64KB)
 */
class BitWriter(private val bufferSize: Int = 65536) {
  @PublishedApi internal var bitBuffer = 0L
  @PublishedApi internal var bitCount = 0
  @PublishedApi internal val buffer = ByteArray(bufferSize)
  @PublishedApi internal var bufferIndex = 0

  /**
   * Writes bits to the buffer with minimal overhead.
   *
   * Accumulates bits in a 64-bit buffer and flushes to byte array when the buffer contains 56 or
   * more bits, ensuring space for subsequent writes.
   *
   * @param bits The bit pattern to write
   * @param length Number of bits to write from the pattern
   */
  @Suppress("NOTHING_TO_INLINE")
  inline fun writeFast(bits: Int, length: Int) {
    bitBuffer = (bitBuffer shl length) or bits.toLong()
    bitCount += length

    if (bitCount >= 56) {
      val bytesToWrite = bitCount shr 3
      val shift = bitCount and 7

      when (bytesToWrite) {
        7 -> {
          buffer[bufferIndex] = (bitBuffer shr (shift + 48)).toByte()
          buffer[bufferIndex + 1] = (bitBuffer shr (shift + 40)).toByte()
          buffer[bufferIndex + 2] = (bitBuffer shr (shift + 32)).toByte()
          buffer[bufferIndex + 3] = (bitBuffer shr (shift + 24)).toByte()
          buffer[bufferIndex + 4] = (bitBuffer shr (shift + 16)).toByte()
          buffer[bufferIndex + 5] = (bitBuffer shr (shift + 8)).toByte()
          buffer[bufferIndex + 6] = (bitBuffer shr shift).toByte()
          bufferIndex += 7
          bitCount = shift
        }
        else -> {
          var remaining = bitCount
          while (remaining >= 8) {
            remaining -= 8
            buffer[bufferIndex++] = (bitBuffer shr remaining).toByte()
          }
          bitCount = remaining
        }
      }
      bitBuffer = bitBuffer and ((1L shl bitCount) - 1)
    }
  }

  /**
   * Writes bits to the buffer (compatibility wrapper for [writeFast]).
   *
   * @param bits The bit pattern to write
   * @param length Number of bits to write from the pattern
   */
  fun write(bits: Int, length: Int) {
    writeFast(bits, length)
  }

  /**
   * Drains the buffer if it's approaching capacity.
   *
   * @return ByteArray containing buffered data if drained, null otherwise
   */
  fun drainIfFull(): ByteArray? {
    return if (bufferIndex >= bufferSize - 2048) {
      val result = buffer.copyOf(bufferIndex)
      bufferIndex = 0
      result
    } else null
  }

  /**
   * Alias for [finish] to support legacy API.
   *
   * @return Pair of final bytes and padding bits count
   */
  fun build(): Pair<ByteArray, Int> = finish()

  /**
   * Finalizes the bit stream and returns all buffered data.
   *
   * Flushes remaining bits, adding padding if necessary to complete the last byte. Resets the
   * writer state after finishing.
   *
   * @return Pair of (bytes, paddingBits) where paddingBits is the number of padding bits added
   */
  fun finish(): Pair<ByteArray, Int> {
    while (bitCount >= 8) {
      bitCount -= 8
      buffer[bufferIndex++] = (bitBuffer shr bitCount).toByte()
      bitBuffer = bitBuffer and ((1L shl bitCount) - 1)
    }

    val paddingBits =
        if (bitCount > 0) {
          val padded = (bitBuffer shl (8 - bitCount)).toByte()
          buffer[bufferIndex++] = padded
          8 - bitCount
        } else 0

    val result = buffer.copyOf(bufferIndex)
    bufferIndex = 0
    bitBuffer = 0
    bitCount = 0

    return result to paddingBits
  }
}
