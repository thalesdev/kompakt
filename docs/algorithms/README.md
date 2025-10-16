# Compression Algorithms

This directory contains detailed documentation for each compression algorithm implemented in the project.

## Entropy-Based Algorithms

Entropy-based algorithms use statistical analysis and information theory to encode data more efficiently based on symbol frequencies.

- **[Huffman Coding](huffman.md)** ✅ - Variable-length prefix coding based on symbol frequencies
- **Shannon-Fano** 🚧 - Early entropy coding, predecessor to Huffman
- **Arithmetic Coding** 📋 - Encodes entire message as a single fractional number
- **Range Encoding** 📋 - Simplified variant of arithmetic coding

## Dictionary-Based Algorithms

Dictionary-based algorithms replace repeated sequences with references to a dictionary of previously seen patterns.

- **LZ77** 📋 - Sliding window dictionary compression
- **LZ78** 📋 - Dynamic dictionary building
- **LZW** 📋 - Lempel-Ziv-Welch, used in GIF and TIFF
- **LZMA** 📋 - High compression ratio dictionary algorithm

## Transform-Based Algorithms

Transform-based algorithms apply reversible transformations to make data more compressible for subsequent encoding.

- **BWT (Burrows-Wheeler Transform)** 📋 - Reversible block-sorting transform
- **MTF (Move-to-Front)** 📋 - Improves locality of reference
- **Delta Encoding** 📋 - Stores differences between consecutive values

## Hybrid Algorithms

Hybrid algorithms combine multiple compression techniques for optimal results.

- **DEFLATE** 📋 - Combines LZ77 and Huffman (used in ZIP, gzip)
- **BZIP2** 📋 - Combines BWT, RLE, and Huffman
- **LZMA2** 📋 - Enhanced LZMA with multithreading support

## Context-Based Algorithms

Context-based algorithms use surrounding data to predict and encode information more efficiently.

- **PPM (Prediction by Partial Matching)** 📋 - Context modeling for text
- **Context Mixing** 📋 - Combines multiple context models

## Neural Network-Based Algorithms

Experimental algorithms using machine learning for compression.

- **N-gram Arithmetic** 📋 - Statistical language models with arithmetic coding
- **LSTM Arithmetic** 📋 - LSTM networks for prediction
- **Transformer Compression** 📋 - Transformer-based compression
- **VAE (Variational Autoencoder)** 📋 - Neural network compression

---

## Legend

- ✅ Fully documented
- 🚧 Work in progress
- 📋 Planned

## Contributing Documentation

When documenting a new algorithm, please include:

1. **Overview** - Brief description and use cases
2. **Theory** - Mathematical/theoretical foundation
3. **Algorithm** - Step-by-step explanation
4. **Implementation** - Kotlin code walkthrough
5. **Performance** - Time/space complexity
6. **Examples** - Usage examples with sample data
7. **References** - Academic papers, books, articles

See [huffman.md](huffman.md) as a template.
