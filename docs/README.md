# Kompakt Documentation

Welcome to the comprehensive documentation for the Kompakt compression library.

## 📖 Documentation Structure

### [Algorithms](algorithms/)
Detailed documentation for each compression algorithm, including theory, implementation, and usage examples.

- **Entropy-based**: Huffman, Shannon-Fano, Arithmetic, Range Encoding
- **Dictionary-based**: LZ77, LZ78, LZW, LZMA
- **Transform-based**: BWT, MTF, Delta Encoding
- **Hybrid**: DEFLATE, BZIP2, LZMA2
- **Context-based**: PPM, Context Mixing
- **Neural**: N-gram, LSTM, Transformer, VAE

[→ Browse all algorithms](algorithms/)

### [Architecture](architecture/)
Architecture guides explaining the design decisions and patterns used in the library.

- **Project Structure** - Overview of the codebase organization
- **Codec Design** - Interface design and abstraction patterns
- **Streaming vs Sync** - When to use each mode
- **Type System** - Type-safe API design
- **Performance** - Optimization techniques used

[→ Explore architecture](architecture/)

### [Benchmarks](benchmarks/)
Performance benchmarks, comparisons, and analysis.

- **Algorithm Comparisons** - Head-to-head performance tests
- **Platform Benchmarks** - Native vs JVM performance
- **Memory Profiling** - Memory usage analysis
- **Methodology** - How benchmarks are conducted

[→ View benchmarks](benchmarks/)

## 🚀 Quick Links

- [Main README](../README.md) - Project overview and quick start
- [Contributing Guide](../CONTRIBUTING.md) - How to contribute
- [API Reference](api/) - KDoc API documentation

## 📚 Learning Path

For those new to data compression or Kotlin Native, we recommend this learning path:

### 1. Fundamentals
Start with understanding the basics:
- [What is Data Compression?](fundamentals/compression-basics.md)
- [Information Theory Primer](fundamentals/information-theory.md)
- [Lossless vs Lossy](fundamentals/lossless-vs-lossy.md)

### 2. Simple Algorithms
Begin with straightforward algorithms:
- [Huffman Coding](algorithms/huffman.md) - Great first algorithm to study
- [Run-Length Encoding](algorithms/rle.md) - Simplest compression technique

### 3. Advanced Algorithms
Progress to more sophisticated techniques:
- [LZ77](algorithms/lz77.md) - Dictionary-based compression
- [Arithmetic Coding](algorithms/arithmetic.md) - Advanced entropy coding
- [BWT](algorithms/bwt.md) - Block-sorting transform

### 4. Hybrid Approaches
Understand real-world implementations:
- [DEFLATE](algorithms/deflate.md) - ZIP, gzip compression
- [BZIP2](algorithms/bzip2.md) - High-ratio compression

### 5. Implementation Details
Dive into Kotlin Native specifics:
- [Bit Manipulation in Kotlin](architecture/bit-manipulation.md)
- [Memory Management](architecture/memory-management.md)
- [Performance Optimization](architecture/optimization.md)

## 🔬 Research & Theory

For academic research and theoretical foundations:

- [Entropy and Information Theory](research/entropy.md)
- [Kolmogorov Complexity](research/kolmogorov.md)
- [Universal Coding](research/universal-coding.md)
- [Rate-Distortion Theory](research/rate-distortion.md)

## 🛠️ Implementation Guides

Practical guides for implementing new algorithms:

- [Adding a New Algorithm](guides/new-algorithm.md)
- [Testing Strategy](guides/testing.md)
- [Benchmarking](guides/benchmarking.md)
- [Documentation Standards](guides/documentation.md)

## 📊 Comparison Tables

### Compression Algorithms at a Glance

| Algorithm | Type | Ratio | Speed | Complexity | Best For |
|-----------|------|-------|-------|------------|----------|
| Huffman | Entropy | 1.5-2x | Fast | Low | Text |
| LZ77 | Dictionary | 2-3x | Medium | Medium | General |
| DEFLATE | Hybrid | 2-4x | Medium | Medium | Archives |
| BZIP2 | Hybrid | 3-5x | Slow | High | Max compression |

[→ Full comparison table](comparisons/algorithms.md)

### When to Use Each Mode

| Scenario | Recommended Mode | Why |
|----------|-----------------|-----|
| File < 10MB | Synchronous | Simple, no streaming overhead |
| File > 100MB | Streaming | Constant memory usage |
| Multiple passes needed | Streaming | ReusableFlow support |
| Embedded system | Streaming | Memory constraints |
| Maximum performance | Synchronous | No chunking overhead |

[→ Detailed mode comparison](architecture/streaming.md)

## 🎯 Goals & Philosophy

This documentation aims to be:

- **Educational**: Teach compression theory and practice
- **Practical**: Provide working, optimized code
- **Comprehensive**: Cover theory, implementation, and usage
- **Accessible**: Clear explanations for all skill levels
- **Maintainable**: Well-organized and easy to update

## 🤝 Contributing to Documentation

We welcome documentation improvements! See our [Documentation Guide](guides/documentation.md) for:

- Documentation standards
- Writing style guide
- Example templates
- Review process

## 📬 Feedback

Found an error? Have a suggestion? Please:

1. [Open an issue](https://github.com/thalesdev/kompakt/issues)
2. [Submit a PR](https://github.com/thalesdev/kompakt/pulls)
3. [Start a discussion](https://github.com/thalesdev/kompakt/discussions)

---

[← Back to Project README](../README.md)
