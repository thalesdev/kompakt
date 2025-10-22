package io.github.thalesdev.kompakt.support

import io.github.thalesdev.kompakt.support.Bits.U08
import io.github.thalesdev.kompakt.support.Bits.U16
import io.github.thalesdev.kompakt.support.Bits.U24
import io.github.thalesdev.kompakt.support.Bits.U32

/**
 * Type-safe representation of unsigned integer values with explicit bit widths.
 *
 * Provides value classes for unsigned integers of various sizes, used primarily in file descriptor
 * definitions to specify exact byte sizes for serialization.
 *
 * All types store their value as a signed Int but represent unsigned semantics within their
 * respective ranges.
 *
 * @property value The underlying integer value
 */
sealed interface Bits {
  val value: Int

  /** Unsigned 8-bit integer (0-255), occupies 1 byte. */
  value class U08(override val value: Int) : Bits

  /** Unsigned 16-bit integer (0-65535), occupies 2 bytes. */
  value class U16(override val value: Int) : Bits

  /** Unsigned 24-bit integer (0-16777215), occupies 3 bytes. */
  value class U24(override val value: Int) : Bits

  /** Unsigned 32-bit integer (0-4294967295), occupies 4 bytes. */
  value class U32(override val value: Int) : Bits
}

/**
 * Returns the size in bytes of a given type.
 *
 * Supports [Bits] types (U08, U16, U24, U32) and standard Kotlin numeric types (Byte, Short, Int,
 * Long, Float, Double) including their unsigned variants.
 *
 * @param T The type to query
 * @return Size of the type in bytes
 * @throws IllegalStateException if the type is not supported
 *
 * Example:
 * ```
 * sizeOf<U08>() // Returns 1
 * sizeOf<U16>() // Returns 2
 * sizeOf<Int>() // Returns 4
 * ```
 */
inline fun <reified T> sizeOf(): Int {
  return when (T::class) {
    U08::class,
    Byte::class,
    UByte::class -> 1
    U16::class,
    Short::class,
    UShort::class -> 2
    U24::class -> 3
    U32::class,
    Int::class,
    UInt::class,
    Float::class -> 4
    Long::class,
    ULong::class,
    Double::class -> 8
    else -> error("Cannot determine size for type ${T::class}")
  }
}
