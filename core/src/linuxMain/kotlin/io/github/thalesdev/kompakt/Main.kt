package io.github.thalesdev.kompakt

import io.github.thalesdev.kompakt.algorithms.entropy.huffman.HuffmanAlgorithm
import io.github.thalesdev.kompakt.algorithms.entropy.huffman.HuffmanMeta
import io.github.thalesdev.kompakt.support.BinaryDeserializer
import io.github.thalesdev.kompakt.support.BinarySerializer
import io.github.thalesdev.kompakt.util.toHexString
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer

fun main() {
  val huffmanAlgorithm = HuffmanAlgorithm()
  val factory = { flow { emit("aaab".encodeToByteArray()) } }

  runBlocking {
    huffmanAlgorithm.encode(factory).also { streamData ->
      val binarySerializer = BinarySerializer(streamData.meta, HuffmanMeta.descriptor)
      val binaryDeserializer = BinaryDeserializer(HuffmanMeta)

      val compressed = binarySerializer.serialize(streamData.data).reduce { a, b -> a + b }

      println(compressed.toHexString())
      println("#".repeat(50))

      val (meta, compressedStream) =
          binaryDeserializer.deserialize(Buffer().also { it.write(compressed) })
      println(meta)
      println(compressedStream)
    }
  }
}
