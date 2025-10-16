package io.github.thalesdev.kompakt.support

/**
 * Categories of compression algorithm types based on their underlying approach.
 *
 * This enum classifies compression algorithms into distinct categories based on
 * their fundamental compression strategy and methodology.
 */
enum class AlgorithmType {
    /**
     * Entropy-based compression algorithms.
     *
     * Use statistical analysis and information theory to encode data more efficiently.
     * Examples: Huffman coding, Arithmetic coding, Shannon-Fano.
     */
    ENTROPY,

    /**
     * Dictionary-based compression algorithms.
     *
     * Replace repeated sequences with references to a dictionary of previously seen data.
     * Examples: LZ77, LZ78, LZW, LZMA.
     */
    DICTIONARY,

    /**
     * Run-Length Encoding algorithms.
     *
     * Compress sequences of repeated values by storing the value and count.
     * Examples: RLE and its variants.
     */
    RUN_LENGTH,

    /**
     * Transform-based compression algorithms.
     *
     * Apply reversible transformations to make data more compressible.
     * Examples: Burrows-Wheeler Transform (BWT), Move-to-Front (MTF), Delta encoding.
     */
    TRANSFORM,

    /**
     * Hybrid compression algorithms.
     *
     * Combine multiple compression techniques for better results.
     * Examples: DEFLATE (LZ77 + Huffman), BZIP2 (BWT + RLE + Huffman), LZMA2.
     */
    HYBRID,

    /**
     * Context-based compression algorithms.
     *
     * Use context modeling to predict and encode data based on surrounding information.
     * Examples: PPM (Prediction by Partial Matching), Context Mixing.
     */
    CONTEXTUAL,

    /**
     * Neural network-based compression algorithms.
     *
     * Use machine learning and neural networks for data compression.
     * Examples: LSTM-based arithmetic coding, Transformer-based compression, VAE.
     */
    NEURAL
}