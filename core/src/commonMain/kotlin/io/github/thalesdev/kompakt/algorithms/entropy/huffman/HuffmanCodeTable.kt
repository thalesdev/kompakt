package io.github.thalesdev.kompakt.algorithms.entropy.huffman

import io.github.thalesdev.kompakt.algorithms.entropy.huffman.HuffmanNode.Leaf
import io.github.thalesdev.kompakt.algorithms.entropy.huffman.HuffmanNode.Node
import io.github.thalesdev.kompakt.util.append

/**
 * Builds a lookup table mapping bytes to their Huffman codes.
 *
 * Traverses the Huffman tree to generate prefix-free binary codes for each symbol:
 * - Traversing left adds a 0 bit
 * - Traversing right adds a 1 bit
 * - Leaf nodes store the final code for that byte
 *
 * @property root Root of the Huffman tree
 */
class HuffmanCodeTable(
    private val root: HuffmanNode,
) {
    /**
     * Generates the complete code table by traversing the tree.
     *
     * @return Map from byte value (0-255) to its Huffman code
     */
    fun build(): Map<Int, HuffmanCode> = buildMap {
        fun traverse(node: HuffmanNode, code: Int, length: Int) {
            when (node) {
                is Leaf -> put(node.byte, HuffmanCode(code, length))
                is Node -> {
                    traverse(node.left, code append 0, length + 1)
                    traverse(node.right, code append 1, length + 1)
                }
            }
        }
        traverse(root, 0, 0)
    }
}
