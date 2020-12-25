package io.github.teddyxlandlee.nios.filesplit

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size < 2) {
        help()
    }
}

fun help() {
    println("Usage: java -jar file-spliterator-${version}.jar <encode|decode> <filename> [args]\n" +
            "encode: arg: filename\n" +
            "decoee: arg: directory name\n" +
            "more args:\n [max_one_file_size] default 99.4MB\n" +
            "[directory name] default filesplit-<origin_filename>");
    exitProcess(1);
}