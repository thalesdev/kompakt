package io.github.thalesdev.kompakt

import io.github.thalesdev.kompakt.algorithms.entropy.huffman.HuffmanAlgorithm
import io.github.thalesdev.kompakt.util.toHexString
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

fun main() {
    val huffmanAlgorithm = HuffmanAlgorithm()
    val factory = { flow { emit("aaab".encodeToByteArray()) } }
    runBlocking {
        huffmanAlgorithm.encode(factory).also {
            println(
                it.meta.serialize(it.data).toList()
                    .reduce { a, b -> a + b }
                    .toHexString()
            )
        }
    }
}