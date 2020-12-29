package io.github.teddyxlandlee.nios.filesplit.util

import io.github.teddyxlandlee.nios.filesplit.fsplitinfoHeader
import java.io.InputStream
import java.nio.charset.StandardCharsets

fun infoFromStream(inputStream: InputStream) : Fsplitinfo {
    var iCache: Int
    val maxFilenameCount: Int
    var cache = ByteArray(4)

    iCache = inputStream.read(cache, 0, 4)
    if (iCache != 4 || toInt(cache) != fsplitinfoHeader)
        throw InvalidFileException("INFO.fsplitinfo", 0x00000002)

    iCache = inputStream.read()
    if (iCache < 1)
        throw InvalidFileException("INFO.fsplitinfo", 0x00000003)

    iCache = inputStream.read(cache, 0, 4)
    if (iCache != 4)
        throw InvalidFileException("INFO.fsplitinfo", 0x00000006)
    maxFilenameCount = toInt(cache)

    iCache = inputStream.read(cache, 0, 4)
    if (iCache != 4)
        throw InvalidFileException("INFO.fsplitinfo", 0x00000004)
    iCache = toInt(cache)
    cache = ByteArray(4)
    iCache = inputStream.read(cache, 0, iCache)
    if (iCache < 0)
        throw InvalidFileException("INFO.fsplitinfo", 0x00000005)
    inputStream.close()
    return Fsplitinfo(maxFilenameCount, String(cache, StandardCharsets.UTF_8))
}