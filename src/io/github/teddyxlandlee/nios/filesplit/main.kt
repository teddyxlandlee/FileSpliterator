package io.github.teddyxlandlee.nios.filesplit

import io.github.teddyxlandlee.nios.filesplit.util.Emaps.fileSizeMap
import java.io.File
import kotlin.system.exitProcess

/**
 * @param args [0] encode/decode
 * @param args [1] filename
 * @param args (encode) --size max one file size, string
 * @param args (encode) --output directory
 * @param args (decode) --github|--gitee <repo>:<branch default master>:<path/to/directory>
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
        var common: Byte = 0
        var component = GitRepoComponent("", "", "", "")
        var maxOneFileSize: Int = fileSizeDefault
        var outputDirectory = "filesplit-${file.name}"
        for (i in 0..args.size - 2) {
            if (args[i].startsWith("--")) {
                if (args[i].equals("--size", true))
                    maxOneFileSize = maxOneFileSize(args[i + 1])
                else if (args[i].equals("--output", true))
                    outputDirectory = args[i + 1]
                else if (args[i].equals("--github", true)) {
                    common = 1
                    component = gitRepo("--github", args[i + 1])
                } else if (args[i].equals("--gitee", true)) {
                    common = 2
                    component = gitRepo("--gitee", args[i + 1])
                }
            }
        }
        if (common == (0).toByte())
            Core.run(codecStatus, file, maxOneFileSize, outputDirectory)
        else if (common == (1).toByte() || common == (2).toByte()) {
            Core.decodeGit(component.server, component.repo, component.branch, component.path)
        }
    }

}

fun help() {
    println("Usage: java -jar FileSpliterator.jar <encode|decode> <filename> [args]\n" +
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

fun gitRepo(server: String, con: String) : GitRepoComponent {
    val server0 = when (server) {
        "--github" -> "https://github.com"
        "--gitee" -> "https://gitee.com"
        else -> throw RuntimeException()
    }

    val arr: MutableList<String> = con.split(':').toMutableList()
    if (arr.size != 3)
        throw RuntimeException()
    if (arr[1] == "")
        arr[1] = "master"
    return GitRepoComponent(server0, arr[0], arr[1], arr[2])
}

const val fileSizeDefault = 98566144

data class GitRepoComponent(val server: String, val repo: String, val branch: String, val path: String)