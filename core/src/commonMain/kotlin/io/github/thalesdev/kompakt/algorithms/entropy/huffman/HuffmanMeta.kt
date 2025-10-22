package io.github.thalesdev.kompakt.algorithms.entropy.huffman

import io.github.thalesdev.kompakt.support.*
import io.github.thalesdev.kompakt.support.Bits.U08
import io.github.thalesdev.kompakt.support.Bits.U16

/**
 * Metadata required to decode Huffman-compressed data.
 *
 * Contains the frequency table needed to reconstruct the Huffman tree and decode the compressed
 * data.
 *
 * ## File Format
 *
 * ```
 * | paddingBits (1 byte) | freq count (1 byte) | frequencies (3 bytes each) | size (4 bytes) | data |
 * ```
 *
 * @property frequencies Map of byte value to frequency count
 * @property paddingBits Number of padding bits added to the last byte (0-7)
 * @property compressedSize Total size of compressed data in bytes (for streaming)
 */
@Suppress("UNCHECKED_CAST")
data class HuffmanMeta(
    val frequencies: Map<Int, Int>,
    val paddingBits: Int = 0,
    val compressedSize: Int? = null,
) : MetaData {

  override val magic: Int = 0x48554646 // HUFF
  override val version: Short = 1

  companion object : MetaDataCompanion<HuffmanMeta> {
    override val descriptor =
        FileDescriptor.descriptor {
          magicNumber(HuffmanMeta::magic)
          fixed(HuffmanMeta::version, sizeInBytes = 2)
          fixed(HuffmanMeta::paddingBits, sizeInBytes = 1)
          map<U08, U16>(HuffmanMeta::frequencies, countBytes = 1)
          compressedData<ByteArray>(HuffmanMeta::compressedSize, lengthPrefix = 4)
        }

    override fun restore(data: MetaDataRestorePayload): HuffmanMeta =
        HuffmanMeta(
            paddingBits = data.getAs<Int>("paddingBits"),
            frequencies = data.getAs("frequencies"),
            compressedSize = data["compressedSize"] as? Int,
        )
  }
}
