package io.github.thalesdev.kompakt.algorithms.entropy.huffman

/**
 * Represents a Huffman code for a single byte/symbol.
 *
 * Stores the variable-length prefix-free binary code generated
 * by traversing the Huffman tree.
 *
 * @property bits The binary code stored as an integer
 * @property length Number of significant bits in the code (1-32)
 */
data class HuffmanCode(
    val bits: Int,
    val length: Int
) {
    /**
     * Returns the binary representation of the code as a string.
     *
     * Example: HuffmanCode(bits=5, length=3) returns "101"
     */
    override fun toString(): String =
        bits.toString(2).padStart(length, '0')
}
