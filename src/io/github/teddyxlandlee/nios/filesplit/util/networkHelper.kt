package io.github.teddyxlandlee.nios.filesplit.util

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.util.*

fun downloadHttpUrl(url: URL, save: File) {
    val conn: URLConnection = url.openConnection()
    conn.connectTimeout = 3000  // 3s
    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)")

    val inputStream: InputStream = conn.inputStream
    val getData: ByteArray = inputStream.readAllBytes()

    val fos = FileOutputStream(save)
    fos.write(getData)
    fos.close()
    inputStream.close()
}

fun tmpFileDownloaded(url: URL) : File {
    val file: File = File.createTempFile(UUID.randomUUID().toString(), "tmp")
    downloadHttpUrl(url = url, save = file)
    return file
}