package io.github.thalesdev.kompakt.algorithms.entropy.huffman

import io.github.thalesdev.kompakt.algorithms.entropy.huffman.HuffmanNode.Leaf
import io.github.thalesdev.kompakt.algorithms.entropy.huffman.HuffmanNode.Node

/**
 * Priority queue for building optimal Huffman trees.
 *
 * Maintains two sorted lists (leaves and internal nodes) to efficiently
 * construct the Huffman tree in O(n) time without requiring a heap.
 *
 * The algorithm leverages the fact that merged nodes are always added
 * with increasing frequencies, maintaining sort order in the nodes list.
 *
 * @property frequencies Array of byte frequencies (index 0-255)
 */
class HuffmanPriorityQueue(
    frequencies: IntArray
) {
    val leaves = frequencies
        .withIndex()
        .filter { (_, v) -> v > 0 }
        .map { (byte, count) ->
            Leaf(byte = byte, count = count)
        }
        .sortedBy { it.count }
        .toMutableList()

    private val nodes = mutableListOf<Node>()

    /**
     * Total number of nodes remaining in the queue.
     */
    val size: Int
        get() = leaves.size + nodes.size

    /**
     * Removes and returns the node with the smallest frequency.
     *
     * Chooses from leaves or internal nodes based on their frequencies.
     *
     * @return The node with minimum frequency
     */
    fun poll(): HuffmanNode =
        when {
            leaves.isEmpty() -> nodes.removeAt(0)
            nodes.isEmpty() -> leaves.removeAt(0)
            leaves[0].count <= nodes[0].count -> leaves.removeAt(0)
            else -> nodes.removeAt(0)
        }

    /**
     * Builds the complete Huffman tree from the frequency data.
     *
     * Repeatedly combines the two nodes with smallest frequencies until
     * only the root remains.
     *
     * @return Root of the Huffman tree, or null if no symbols present
     */
    fun build(): HuffmanNode? = when (size) {
        0 -> null
        1 -> leaves.firstOrNull() ?: nodes.firstOrNull()
        else -> {
            while (size > 1) {
                val left = poll()
                val right = poll()

                val parent = Node(
                    count = left.count + right.count,
                    left = left,
                    right = right
                )

                nodes.add(parent)
            }

            leaves.firstOrNull() ?: nodes.first()
        }
    }
}
