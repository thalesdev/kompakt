package io.github.thalesdev.kompakt.algorithms.entropy.huffman

/**
 * Node in a Huffman coding tree.
 *
 * A Huffman tree is a binary tree where:
 * - Leaf nodes represent individual bytes/symbols
 * - Internal nodes represent merged subtrees
 * - Left edges represent bit 0, right edges represent bit 1
 */
sealed interface HuffmanNode {
  /** Frequency count (number of occurrences) for this node. */
  val count: Int

  /**
   * Leaf node representing a single byte/symbol in the input.
   *
   * @property count Frequency of this byte in the input
   * @property byte The byte value (0-255)
   */
  data class Leaf(override val count: Int, val byte: Int) : HuffmanNode {
    override fun toString(): String {
      val char =
          when (byte.toInt()) {
            in 32..126 -> "'${byte.toInt().toChar()}'"
            10 -> "\\n"
            13 -> "\\r"
            9 -> "\\t"
            else -> "0x${byte.toString(16).uppercase().padStart(2, '0')}"
          }
      return "[$char:$count]"
    }
  }

  /**
   * Internal node with two children.
   *
   * @property count Combined frequency of all leaves in this subtree
   * @property left Left child (represents bit 0)
   * @property right Right child (represents bit 1)
   */
  data class Node(
      override val count: Int,
      val left: HuffmanNode,
      val right: HuffmanNode,
  ) : HuffmanNode {
    override fun toString(): String = buildString {
      append("($count)\n")
      append(left.toTreeString("├─ ", "│  "))
      append(right.toTreeString("└─ ", "   "))
    }
  }
}

private fun HuffmanNode.toTreeString(prefix: String, childPrefix: String): String {
  return buildString {
    append(prefix)
    when (this@toTreeString) {
      is HuffmanNode.Leaf -> {
        append(this@toTreeString.toString())
        append("\n")
      }

      is HuffmanNode.Node -> {
        append("($count)\n")
        append(left.toTreeString("$childPrefix├─ ", "$childPrefix│  "))
        append(right.toTreeString("$childPrefix└─ ", "$childPrefix   "))
      }
    }
  }
}
