package io.github.teddyxlandlee.nios.filesplit

import io.github.teddyxlandlee.nios.filesplit.util.Emaps.fileSizeMap
import java.io.File
import kotlin.system.exitProcess

/**
 * @param args [0] encode/decode
 * @param args [1] filename
 * @param args --size max one file size, string
 * @param args --output directory
 */
fun main(args: Array<String>) {
    if (args.size < 2)
        help()
    val codecStatus: CodecStatus? = readCodec(args[0])
    if (codecStatus == null)
        help()
    else {
        val file: File? = file(args[1], codecStatus)
        if (file == null) {
            println("ERROR: ${args[1]} is not a correct ${codecStatus.preFileType}!")
            exitProcess(1)
        }
        var maxOneFileSize: Int = fileSizeDefault
        var outputDirectory = "filesplit-${file.name}"
        for (i in 0..args.size - 2) {
            if (args[i].startsWith("--")) {
                if (args[i].equals("--size", true))
                    maxOneFileSize = maxOneFileSize(args[i + 1])
                else if (args[i].equals("--output", true))
                    outputDirectory = args[i + 1]
            }
        }
        Core.run(codecStatus, file, maxOneFileSize, outputDirectory)
    }

}

fun help() {
    println("Usage: java -jar file-spliterator-${version}.jar <encode|decode> <filename> [args]\n" +
            "encode: arg: filename\n" +
            "decoee: arg: directory name\n" +
            "more args:\n [max_one_file_size] default 99.4MB\n" +
            "[directory name] default filesplit-<origin_filename>")
    exitProcess(0)
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

fun maxOneFileSize(string: String) : Int {
    val size: Int? = string.toIntOrNull()
    if (size != null)
        return size
    else {
        val size2: Char = string[string.lastIndex]
        if (!fileSizeMap.containsKey(size2)) {
            println("$size2 is not a correct file size suffix")
            return fileSizeDefault
        }
        return maxOneFileSize(string.substring(0..string.length - 2)) * fileSizeMap.getOrDefault(size2, 1)
    }
}

const val fileSizeDefault = 104228454