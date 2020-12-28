package io.github.teddyxlandlee.nios.filesplit.util

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

fun downloadHttpUrl(url: URL, save: File) {
    val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
    conn.connectTimeout = 3000  // 3s
    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)")

    val inputStream: InputStream = conn.inputStream
    val getData: ByteArray = inputStream.readAllBytes()

    val fos = FileOutputStream(save)
    fos.write(getData)
    fos.close()
    inputStream.close()
}