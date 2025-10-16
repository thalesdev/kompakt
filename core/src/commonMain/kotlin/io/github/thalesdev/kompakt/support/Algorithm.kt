package io.github.thalesdev.kompakt.support
import io.github.thalesdev.kompakt.support.AlgorithmType.CONTEXTUAL
import io.github.thalesdev.kompakt.support.AlgorithmType.DICTIONARY
import io.github.thalesdev.kompakt.support.AlgorithmType.ENTROPY
import io.github.thalesdev.kompakt.support.AlgorithmType.HYBRID
import io.github.thalesdev.kompakt.support.AlgorithmType.NEURAL
import io.github.thalesdev.kompakt.support.AlgorithmType.RUN_LENGTH
import io.github.thalesdev.kompakt.support.AlgorithmType.TRANSFORM

/**
 * Enumeration of supported compression algorithms.
 *
 * Each algorithm is categorized by its [AlgorithmType], which indicates
 * the fundamental compression approach used.
 *
 * @property type The category this algorithm belongs to
 */
enum class Algorithm (val type: AlgorithmType) {
    // Entropy-based algorithms

    /**
     * Huffman coding - Variable-length prefix coding based on symbol frequencies.
     */
    HUFFMAN(ENTROPY),

    /**
     * Shannon-Fano coding - Early entropy coding predecessor to Huffman.
     */
    SHANNON_FANO(ENTROPY),

    /**
     * Arithmetic coding - Encodes entire message as a single number.
     */
    ARITHMETIC(ENTROPY),

    /**
     * Range encoding - Variant of arithmetic coding with simpler implementation.
     */
    RANGE_ENCODING(ENTROPY),

    // Dictionary-based algorithms

    /**
     * LZ77 - Sliding window dictionary compression (used in DEFLATE).
     */
    LZ77(DICTIONARY),

    /**
     * LZ78 - Dictionary-based compression with dynamic dictionary building.
     */
    LZ78(DICTIONARY),

    /**
     * LZW (Lempel-Ziv-Welch) - Dictionary compression used in GIF and TIFF.
     */
    LZW(DICTIONARY),

    /**
     * LZMA (Lempel-Ziv-Markov chain Algorithm) - High compression ratio dictionary algorithm.
     */
    LZMA(DICTIONARY),

    // Run-length encoding

    /**
     * RLE (Run-Length Encoding) - Compresses sequences of repeated values.
     */
    RLE(RUN_LENGTH),

    // Transform-based algorithms

    /**
     * BWT (Burrows-Wheeler Transform) - Reversible permutation for improved compressibility.
     */
    BWT(TRANSFORM),

    /**
     * MTF (Move-to-Front) - Transform that improves locality of reference.
     */
    MTF(TRANSFORM),

    /**
     * Delta encoding - Stores differences between consecutive values.
     */
    DELTA(TRANSFORM),

    // Hybrid algorithms

    /**
     * DEFLATE - Combines LZ77 and Huffman coding (used in ZIP, gzip).
     */
    DEFLATE(HYBRID),

    /**
     * BZIP2 - Combines BWT, RLE, and Huffman coding.
     */
    BZIP2(HYBRID),

    /**
     * LZMA2 - Enhanced version of LZMA with improved multithreading support.
     */
    LZMA2(HYBRID),

    // Context-based algorithms

    /**
     * PPM (Prediction by Partial Matching) - Context modeling for text compression.
     */
    PPM(CONTEXTUAL),

    /**
     * Context mixing - Combines multiple context models for prediction.
     */
    CONTEXT_MIXING(CONTEXTUAL),

    // Neural network-based algorithms

    /**
     * N-gram arithmetic coding - Uses n-gram models with arithmetic coding.
     */
    NGRAM_ARITHMETIC(NEURAL),

    /**
     * LSTM arithmetic coding - Uses LSTM networks for prediction with arithmetic coding.
     */
    LSTM_ARITHMETIC(NEURAL),

    /**
     * Transformer-based compression - Uses transformer neural networks.
     */
    TRANSFORMER(NEURAL),

    /**
     * VAE (Variational Autoencoder) - Neural network compression using autoencoders.
     */
    VAE(NEURAL)
}