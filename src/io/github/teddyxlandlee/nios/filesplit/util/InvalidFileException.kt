package io.github.teddyxlandlee.nios.filesplit.util

import java.io.File
import java.io.IOException

class InvalidFileException(filename: String, err: Int) : IOException("$filename, error $err: ${Emaps.errMap.getOrDefault(err, "???")}") {
    constructor(file: File, err: Int) : this(file.name, err)
}