package io.github.thalesdev.kompakt.util

import io.github.thalesdev.kompakt.util.BytesUtil.UNSIGNED_CONVERSION_FLAG

/**
 * Utility object for byte manipulation operations.
 *
 * Provides constants and helper functions for working with bytes, including conversion masks and
 * bit manipulation utilities.
 */
object BytesUtil {
  /**
   * Mask for converting signed bytes to unsigned integer representation.
   *
   * This constant is used to extract the unsigned value from a signed byte by masking the sign
   * extension bits when converting to Int.
   */
  const val UNSIGNED_CONVERSION_FLAG = 0xFF
}

/**
 * Converts a signed byte to its unsigned integer representation.
 *
 * Extension property that returns the unsigned value (0-255) of a byte by masking the sign
 * extension bits.
 *
 * Example:
 * ```
 * val byte: Byte = -1
 * println(byte.unsigned) // Outputs: 255
 * ```
 */
val Byte.unsigned
  get() = (toInt() and UNSIGNED_CONVERSION_FLAG)

/**
 * Appends a bit to an integer value.
 *
 * Shifts the current integer left by one position and adds the new bit. Useful for building bit
 * sequences incrementally.
 *
 * @param bit The bit value to append (0 or 1)
 * @return The new integer value with the bit appended
 *
 * Example:
 * ```
 * val result = 0b1010 append 1  // Results in 0b10101
 * ```
 */
infix fun Int.append(bit: Int) = (this shl 1) or bit

/**
 * Converts a byte array to its hexadecimal string representation.
 *
 * Each byte is converted to a two-character uppercase hexadecimal string, with bytes separated by
 * spaces.
 *
 * @return String representation in format "XX XX XX ..." where X is a hex digit
 *
 * Example:
 * ```
 * byteArrayOf(0x12, 0xAB, 0xFF).toHexString()  // Returns: "12 AB FF"
 * ```
 */
fun ByteArray.toHexString(): String =
    joinToString(" ") { byte ->
      val value = byte.toInt() and 0xFF
      value.toString(16).padStart(2, '0').uppercase()
    }
