package io.github.thalesdev.kompakt.algorithms.entropy.huffman

import io.github.thalesdev.kompakt.support.FileDescriptor
import io.github.thalesdev.kompakt.support.FileDescriptor.Field.Array
import io.github.thalesdev.kompakt.support.FileDescriptor.Field.Fixed
import io.github.thalesdev.kompakt.support.FileDescriptor.Field.Variable
import io.github.thalesdev.kompakt.support.MetaData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.io.Buffer
import kotlinx.io.readByteArray

/**
 * Metadata required to decode Huffman-compressed data.
 *
 * Contains the frequency table needed to reconstruct the Huffman tree
 * and decode the compressed data.
 *
 * ## File Format
 * ```
 * | paddingBits (1 byte) | freq count (1 byte) | frequencies (3 bytes each) | size (4 bytes) | data |
 * ```
 *
 * @property frequencies Map of byte value to frequency count
 * @property paddingBits Number of padding bits added to the last byte (0-7)
 * @property compressedSize Total size of compressed data in bytes (for streaming)
 */
data class HuffmanMeta(
    val frequencies: Map<Int, Int>,
    val paddingBits: Int = 0,
    val compressedSize: Int? = null,
) : MetaData {

    override val descriptor = FileDescriptor(
        listOf(
            Fixed("paddingBits", sizeInBytes = 1),
            Array(
                name = "frequencies",
                elementBytes = 3,
                countBytes = 1
            ),
            Variable(
                name = "content",
                lengthPrefix = 4
            )
        )
    )

    /**
     * Serializes metadata and compressed data into a single byte array.
     *
     * @param compressedData The Huffman-encoded bytes
     * @return Complete serialized format ready for storage or transmission
     */
    override fun serialize(compressedData: ByteArray): ByteArray = Buffer().apply {
        writeByte(paddingBits.toByte())
        writeByte(frequencies.size.toByte())
        frequencies.forEach { (byte, count) ->
            writeByte(byte.toByte())
            writeShort(count.toShort())
        }
        writeInt(compressedData.size)
        write(compressedData)
    }.readByteArray()

    /**
     * Serializes metadata and streams compressed data.
     *
     * @param compressedData Flow of Huffman-encoded byte chunks
     * @return Flow of serialized chunks (metadata header followed by data)
     * @throws IllegalStateException if compressedSize is not set
     */
    override fun serialize(compressedData: Flow<ByteArray>): Flow<ByteArray> = flow {
        val buffer = Buffer()

        buffer.writeByte(paddingBits.toByte())
        buffer.writeByte(frequencies.size.toByte())
        frequencies.forEach { (byte, count) ->
            buffer.writeByte(byte.toByte())
            buffer.writeShort(count.toShort())
        }
        buffer.writeInt(compressedSize ?: throw IllegalStateException("Compressed byte length not set"))

        emit(buffer.readByteArray())
        emitAll(compressedData)
    }
}

