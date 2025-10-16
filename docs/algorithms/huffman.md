# Huffman Coding

> **Type**: Entropy-based
> **Inventor**: David A. Huffman (1952)
> **Status**: ✅ Fully Implemented

## Overview

Huffman coding is a lossless data compression algorithm that assigns variable-length codes to input characters based on their frequencies. More frequent characters receive shorter codes, while less frequent characters receive longer codes, resulting in an optimal prefix-free binary code.

### Key Characteristics

- **Lossless**: Perfect reconstruction of original data
- **Optimal**: Among prefix-free codes for given symbol frequencies
- **Adaptive**: Code length adapts to symbol frequency
- **Greedy**: Builds optimal tree using greedy algorithm
- **Prefix-free**: No code is a prefix of another

### Use Cases

- **Text compression**: Effective for natural language text
- **Image compression**: Component of JPEG (after quantization)
- **File archiving**: Used in ZIP, gzip (part of DEFLATE)
- **Data transmission**: Efficient encoding for transmission

## Theory

### Information Theory Background

The theoretical foundation comes from **Claude Shannon's Information Theory** (1948):

**Entropy** (H) measures the average information content:

```
H(X) = -Σ p(x) × log₂(p(x))
```

Where:
- `p(x)` is the probability of symbol `x`
- `log₂(p(x))` is the information content in bits

Huffman coding achieves compression close to the entropy limit for symbol-by-symbol encoding.

### Algorithm Intuition

Given a message "AAABBC":
- Frequencies: A=3, B=2, C=1
- Total symbols: 6

**Without compression** (fixed-length encoding):
- Need 2 bits per symbol (can represent 4 symbols)
- Total: 6 symbols × 2 bits = 12 bits

**With Huffman coding**:
- A (most frequent): 1 bit → "0"
- B: 2 bits → "10"
- C (least frequent): 2 bits → "11"
- Encoded: "000 10 10 11" = 9 bits
- **Compression ratio**: 12/9 = 1.33x

## Algorithm

### Building the Huffman Tree

The algorithm constructs a binary tree bottom-up using a priority queue:

1. **Initialize**: Create a leaf node for each symbol with its frequency
2. **Build Tree**:
   - Extract two nodes with minimum frequency from queue
   - Create parent node with frequency = sum of children
   - Add parent back to queue
   - Repeat until one node remains (the root)
3. **Generate Codes**:
   - Traverse tree from root to leaves
   - Assign '0' for left branches, '1' for right branches
   - Path from root to leaf = code for that symbol

### Pseudocode

```
function buildHuffmanTree(frequencies):
    queue = PriorityQueue()

    // Create leaf nodes
    for each symbol, freq in frequencies:
        queue.insert(Node(symbol, freq))

    // Build tree
    while queue.size > 1:
        left = queue.extractMin()
        right = queue.extractMin()
        parent = Node(null, left.freq + right.freq)
        parent.left = left
        parent.right = right
        queue.insert(parent)

    return queue.extractMin()  // root

function generateCodes(node, code, table):
    if node.isLeaf():
        table[node.symbol] = code
        return

    generateCodes(node.left, code + "0", table)
    generateCodes(node.right, code + "1", table)
```

### Visual Example

For input "AAABBC":

```
Step 1: Initial frequencies
[A:3] [B:2] [C:1]

Step 2: Combine C and B (smallest)
[A:3] [*:3]
       / \
      B:2 C:1

Step 3: Combine A and *
      [*:6]
      /   \
    A:3   *:3
          / \
        B:2 C:1

Step 4: Generate codes (left=0, right=1)
A → 0    (left from root)
B → 10   (right, then left)
C → 11   (right, then right)
```

## Implementation

### Core Components

#### 1. Frequency Analysis

```kotlin
object FrequencyAnalyzer {
    fun ByteArray.countFrequencies(): IntArray {
        val frequencies = IntArray(256)
        forEach { byte ->
            frequencies[byte.toInt() and 0xFF]++
        }
        return frequencies
    }
}
```

#### 2. Priority Queue

```kotlin
class HuffmanPriorityQueue(private val frequencies: IntArray) {
    fun build(): HuffmanNode? {
        val queue = PriorityQueue<HuffmanNode> { a, b ->
            a.frequency.compareTo(b.frequency)
        }

        // Add leaf nodes
        frequencies.forEachIndexed { index, freq ->
            if (freq > 0) {
                queue.add(HuffmanNode.Leaf(index.toByte(), freq))
            }
        }

        // Build tree
        while (queue.size > 1) {
            val left = queue.poll()
            val right = queue.poll()
            val parent = HuffmanNode.Internal(
                frequency = left.frequency + right.frequency,
                left = left,
                right = right
            )
            queue.add(parent)
        }

        return queue.poll()
    }
}
```

#### 3. Code Table Generation

```kotlin
class HuffmanCodeTable(private val root: HuffmanNode) {
    fun build(): Map<Int, HuffmanCode> {
        val table = mutableMapOf<Int, HuffmanCode>()
        generateCodes(root, 0, 0, table)
        return table
    }

    private fun generateCodes(
        node: HuffmanNode,
        code: Int,
        length: Int,
        table: MutableMap<Int, HuffmanCode>
    ) {
        when (node) {
            is HuffmanNode.Leaf -> {
                table[node.value.toInt() and 0xFF] = HuffmanCode(code, length)
            }
            is HuffmanNode.Internal -> {
                generateCodes(node.left, code shl 1, length + 1, table)
                generateCodes(node.right, (code shl 1) or 1, length + 1, table)
            }
        }
    }
}
```

#### 4. Encoding

```kotlin
class HuffmanAlgorithm : CompressionCodec<HuffmanMeta> {
    override suspend fun encode(originalSource: ByteArray): CompressedData<HuffmanMeta> {
        // 1. Analyze frequencies
        val frequencies = originalSource.countFrequencies()

        // 2. Build Huffman tree
        val tree = HuffmanPriorityQueue(frequencies).build()
            ?: throw IllegalStateException("Empty input")

        // 3. Generate code table
        val codeTable = HuffmanCodeTable(tree).build()

        // 4. Encode data
        val writer = HuffmanCodeWriter(codeTable, bufferSize = originalSource.size)
        writer.write(originalSource)
        val (encoded, paddingBits) = writer.finish()

        // 5. Return compressed data with metadata
        return CompressedData(
            data = encoded,
            meta = HuffmanMeta(
                paddingBits = paddingBits,
                frequencies = frequencies.nonZeroEntries().associate { it.index to it.value }
            )
        )
    }
}
```

### Metadata Structure

The compressed file format includes:

```
[Metadata] [Compressed Data]
```

**Metadata** (HuffmanMeta):
- `paddingBits: Int` - Number of padding bits in last byte (0-7)
- `frequencies: Map<Int, Int>` - Symbol frequencies (to rebuild tree)
- `compressedSize: Int` - Size of compressed data (streaming only)

## Performance

### Complexity Analysis

| Operation          | Time Complexity | Space Complexity |
|--------------------|-----------------|------------------|
| Frequency analysis | O(n)            | O(1)             |
| Tree building      | O(k log k)      | O(k)             |
| Code generation    | O(k)            | O(k)             |
| Encoding          | O(n)            | O(n)             |
| Decoding          | O(n)            | O(k)             |

Where:
- `n` = input size (bytes)
- `k` = alphabet size (256 for byte streams)

### Compression Ratio

Compression effectiveness depends on data characteristics:

| Data Type           | Typical Ratio | Notes                          |
|---------------------|---------------|--------------------------------|
| English text        | 1.5x - 2.0x   | Good due to skewed frequencies |
| Source code         | 1.5x - 1.8x   | Similar to natural text        |
| Random data         | ~1.0x         | No compression (uniform dist)  |
| Already compressed  | <1.0x         | Expansion (add metadata)       |
| Highly repetitive   | 2.0x - 3.0x   | Excellent compression          |

### Benchmark Results

Test file: 100MB text file (Project Gutenberg corpus)

| Mode      | Time    | Throughput | Memory  |
|-----------|---------|------------|---------|
| Sync      | 450ms   | 222 MB/s   | ~100MB  |
| Streaming | 263ms   | 380 MB/s   | ~8MB    |

*Tested on M1 MacBook Pro, Kotlin/Native*

## Usage Examples

### Basic Synchronous Compression

```kotlin
import io.github.thalesdev.kompakt.algorithms.entropy.huffman.HuffmanAlgorithm

fun main() {
    val codec = HuffmanAlgorithm()

    // Compress
    val original = "Hello, World!".encodeToByteArray()
    val compressed = codec.encode(original)

    println("Original: ${original.size} bytes")
    println("Compressed: ${compressed.data.size} bytes")
    println("Ratio: ${"%.2f".format(original.size.toFloat() / compressed.data.size)}x")

    // Decompress
    val decompressed = codec.decode(compressed)
    println("Match: ${original.contentEquals(decompressed)}")
}
```

### Streaming Large Files

```kotlin
import io.github.thalesdev.kompakt.algorithms.entropy.huffman.HuffmanAlgorithm
import io.github.thalesdev.kompakt.util.asReusableFlow
import kotlinx.io.files.Path

suspend fun compressLargeFile() {
    val codec = HuffmanAlgorithm()
    val inputPath = Path("large-file.txt")

    // Create reusable flow factory
    val flowFactory = inputPath.asReusableFlow(chunkSize = 64 * 1024)

    // Compress in streaming mode
    val compressed = codec.encode(flowFactory)

    // Write compressed chunks to output
    val outputPath = Path("large-file.huffman")
    SystemFileSystem.sink(outputPath).buffered().use { sink ->
        compressed.data.collect { chunk ->
            sink.write(chunk)
        }
    }

    println("Compression complete!")
    println("Metadata: ${compressed.meta}")
}
```

### Custom Chunk Sizes

```kotlin
import io.github.thalesdev.kompakt.util.ChunkSize

// Small chunks for memory-constrained environments
val smallChunks = inputPath.asReusableFlow(ChunkSize.SMALL)

// Large chunks for maximum throughput
val largeChunks = inputPath.asReusableFlow(ChunkSize.XLARGE)
```

## Limitations

1. **Not optimal for**:
   - Already compressed data (adds overhead)
   - Random/encrypted data (uniform distribution)
   - Very small files (metadata overhead)

2. **Metadata overhead**:
   - Must store frequency table (or tree structure)
   - Padding bits for incomplete bytes
   - Can be significant for small inputs

3. **Bounded by entropy**:
   - Cannot compress below entropy limit
   - Single-pass symbol-by-symbol encoding

4. **No adaptation**:
   - Static codes based on initial frequency analysis
   - Adaptive Huffman variants exist but not implemented

## Variations & Extensions

### Canonical Huffman Coding
- Generates codes in canonical order
- More efficient metadata representation
- Easier reconstruction from code lengths

### Adaptive Huffman Coding
- Updates tree dynamically during encoding
- No need to transmit frequency table
- Two-pass algorithm vs one-pass

### Package-Merge Algorithm
- Optimal for length-limited codes
- Useful when maximum code length is constrained

## References

### Academic Papers
1. Huffman, D. A. (1952). "A Method for the Construction of Minimum-Redundancy Codes"
2. Shannon, C. E. (1948). "A Mathematical Theory of Communication"

### Books
- "Introduction to Data Compression" by Khalid Sayood
- "The Data Compression Book" by Mark Nelson & Jean-Loup Gailly

### Online Resources
- [Huffman Coding - Wikipedia](https://en.wikipedia.org/wiki/Huffman_coding)
- [Huffman Coding Visualization](https://people.ok.ubc.ca/ylucet/DS/Huffman.html)

## See Also

- [Shannon-Fano Coding](shannon-fano.md) - Similar entropy-based algorithm
- [Arithmetic Coding](arithmetic.md) - More efficient entropy coder
- [Algorithm Comparison](../architecture/algorithm-comparison.md)

---

[← Back to Algorithm Index](README.md)
