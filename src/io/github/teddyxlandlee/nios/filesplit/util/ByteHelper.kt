package io.github.teddyxlandlee.nios.filesplit.util


fun fromInt(i: Int): ByteArray {
    return byteArrayOf(
        (i shr 24 and 255).toByte(),
        (i shr 16 and 255).toByte(),
        (i shr 8 and 255).toByte(),
        (i and 255).toByte()
    )
}

fun toInt(bs: ByteArray): Int {
    if (bs.size < 4)
        return -1
    return  bs[0].toInt().shl(24) +
            bs[1].toInt().shl(16) +
            bs[2].toInt().shl(8) +
            bs[3].toInt()
}

fun concat(vararg byteArrays: ByteArray): ByteArray {
    return concat0(byteArrays)
}

private fun concat0(byteArrays: Array<out ByteArray>) : ByteArray {
    var length = 0
    for (bs in byteArrays) {
        length += bs.size
    }
    val rt = ByteArray(length)
    var allI = 0
    for (byteArray in byteArrays) {
        var j = 0
        while (j < byteArray.size) {
            rt[allI] = byteArray[j]
            ++j
            ++allI
        }
    }
    return rt
}

fun concat(vararg ints: Int) : ByteArray {
    val arr: Array<ByteArray> = Array(ints.size) { ByteArray(0) }
    for (i: Int in ints.indices) {
        arr[i] = fromInt(ints[i])
    }
    return concat0(arr)
}