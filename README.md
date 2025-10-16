# Kompakt

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0beta1-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Kotlin Native](https://img.shields.io/badge/Kotlin%20Native-Multiplatform-orange.svg?style=flat)](https://kotlinlang.org/docs/native-overview.html)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> A comprehensive study project exploring data compression algorithms and Kotlin Native/Multiplatform capabilities.

## 🎯 Overview

**Kompakt** is an educational project focused on implementing and understanding various data compression algorithms
using Kotlin Multiplatform. The project serves dual purposes:

1. **Study compression algorithms** - Hands-on implementation of classical and modern compression techniques
2. **Explore Kotlin Native** - Learn the capabilities, performance characteristics, and limitations of Kotlin/Native

This is a **learning-oriented project** where code quality, documentation, and understanding take precedence over
production readiness.

## ✨ Features

- **Multiple Compression Algorithms**
    - Entropy-based (Huffman, Shannon-Fano, Arithmetic)
    - Dictionary-based (LZ77, LZ78, LZW, LZMA)
    - Transform-based (BWT, MTF, Delta)
    - Hybrid approaches (DEFLATE, BZIP2)
    - And more... (see [Algorithm Catalog](docs/algorithms/))

- **Dual Operation Modes**
    - **Synchronous (in-memory)** - For small to medium files
    - **Streaming (chunked)** - For large files with constant memory usage

- **Kotlin Multiplatform**
    - Native executables (macOS, Linux, Windows)
    - JVM support
    - Shared codebase across platforms

- **Production-Quality Code**
    - Comprehensive KDoc documentation
    - Type-safe API design
    - Performance-optimized implementations
    - Extensive use of Kotlin idioms

## 📚 Documentation

### Algorithm Documentation

Each compression algorithm has dedicated documentation explaining theory, implementation details, and usage:

- [Huffman Coding](docs/algorithms/huffman.md) - Variable-length prefix coding
- [Shannon-Fano](docs/algorithms/shannon-fano.md) - Early entropy coding (WIP)
- [LZ77](docs/algorithms/lz77.md) - Sliding window compression (WIP)
- [More algorithms...](docs/algorithms/)

### Architecture Documentation

- [Project Structure](docs/architecture/structure.md)
- [Codec Design](docs/architecture/codecs.md)
- [Streaming vs Sync](docs/architecture/streaming.md)

## 🚀 Quick Start

### Prerequisites

- JDK 17 or higher
- Kotlin 2.1.0+
- Gradle 8.10+

### Build the Project

```bash
# Clone the repository
git clone https://github.com/thalesdev/kompakt.git
cd kompakt

# Build all targets
./gradlew build

# Build native executable (macOS)
./gradlew :core:linkReleaseExecutableDarwin
```

### Run Examples

```bash
# Run the native executable
./core/build/bin/darwin/releaseExecutable/core.kexe

# Run with custom file
./core/build/bin/darwin/releaseExecutable/core.kexe /path/to/file.txt
```

### Use as a Library

```kotlin
import io.github.thalesdev.kompakt.algorithms.entropy.huffman.HuffmanAlgorithm
import io.github.thalesdev.kompakt.util.asReusableFlow
import kotlinx.io.files.Path

// Synchronous compression
val codec = HuffmanAlgorithm()
val data = "Hello, World!".encodeToByteArray()
val compressed = codec.encode(data)
val decompressed = codec.decode(compressed)

// Streaming compression for large files
val inputPath = Path("large-file.txt")
val streamFactory = inputPath.asReusableFlow()
val compressedStream = codec.encode(streamFactory)

compressedStream.data.collect { chunk ->
    // Process compressed chunks
    println("Compressed chunk: ${chunk.size} bytes")
}
```

## 📁 Project Structure

```
kompakt/
├── core/                           # Core compression library
│   └── src/
│       └── commonMain/kotlin/
│           └── io/github/thalesdev/kompakt/
│               ├── algorithms/     # Algorithm implementations
│               │   ├── entropy/    # Huffman, Shannon-Fano, Arithmetic...
│               │   ├── dictionary/ # LZ77, LZ78, LZW, LZMA...
│               │   └── transform/  # BWT, MTF, Delta...
│               ├── contracts/      # Core interfaces (Codec, Encoder, Decoder)
│               ├── support/        # Data structures and type aliases
│               └── util/           # Utilities (BitWriter, StreamUtil)
├── composeApp/                     # Desktop UI (future)
├── docs/                           # Documentation
│   ├── algorithms/                 # Per-algorithm documentation
│   └── architecture/               # Architecture guides
└── README.md                       # This file
```

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :core:test
```

## 🎓 Learning Goals

This project explores:

- **Compression Theory**: Information theory, entropy, prefix codes, dictionary methods
- **Kotlin Native**: FFI, memory management, performance optimization
- **Kotlin Multiplatform**: Shared business logic, platform-specific implementations
- **Performance Engineering**: Bit manipulation, loop unrolling, buffer management
- **API Design**: Type-safe APIs, streaming patterns, extensibility

## 🤝 Contributing

This is primarily an educational project, but contributions are welcome! Whether it's:

- Implementing new compression algorithms
- Improving documentation
- Performance optimizations
- Bug fixes
- Adding tests

Please feel free to open issues or submit PRs.

### Development Setup

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-algorithm`)
3. Make your changes
4. Ensure tests pass (`./gradlew test`)
5. Commit with descriptive messages
6. Push and open a PR

## 📊 Benchmarks

Performance benchmarks are available in the [benchmarks](docs/benchmarks/) directory.

Example results (100MB text file, M1 Mac):

| Algorithm | Compression Ratio | Encode Time | Decode Time |
|-----------|-------------------|-------------|-------------|
| Huffman   | 1.8x              | 263ms       | TBD         |
| LZ77      | TBD               | TBD         | TBD         |
| DEFLATE   | TBD               | TBD         | TBD         |

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Huffman Coding - Wikipedia](https://en.wikipedia.org/wiki/Huffman_coding)
- [Data Compression Explained - Matt Mahoney](http://mattmahoney.net/dc/dce.html)
- [Kotlin Native Documentation](https://kotlinlang.org/docs/native-overview.html)

## 📬 Contact

Thales - [@thalesdev](https://github.com/thalesdev)

Project Link: [https://github.com/thalesdev/kompakt](https://github.com/thalesdev/kompakt)

---

**Note**: This is a learning project. The implementations prioritize clarity and educational value over production-grade
optimization.
