package io.github.teddyxlandlee.nios.filesplit

import java.io.File
import kotlin.system.exitProcess

/**
 * @param args [0] encode/decode
 * @param args [1] filename
 * @param args [2] max one file size, string
 * @param args [3] directory
 */
fun main(args: Array<String>) {
    if (args.size < 2)
        help()
    val codecStatus: CodecStatus? = readCodec(args[0])
    if (codecStatus == null)
        help()

}

fun help() {
    println("Usage: java -jar file-spliterator-${version}.jar <encode|decode> <filename> [args]\n" +
            "encode: arg: filename\n" +
            "decoee: arg: directory name\n" +
            "more args:\n [max_one_file_size] default 99.4MB\n" +
            "[directory name] default filesplit-<origin_filename>")
    exitProcess(1)
}

fun readCodec(string: String) : CodecStatus? {
    return when {
        string.equals("encode", true) -> CodecStatus.ENCODE
        string.equals("decode", false) -> CodecStatus.DECODE
        else -> null
    }
}

fun file(string: String, codecStatus: CodecStatus) : File? {
    val file = File(string)
    return if (codecStatus == CodecStatus.ENCODE) {
        if (file.exists() && !file.isDirectory)
            file
        else
            null
    } else {
        if (file.exists() && file.isDirectory)
            file
        else
            null
    }
}