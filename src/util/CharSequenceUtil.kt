package io.github.mucute.qwq.piecetable.util

internal object CharSequenceUtil {

    fun toByteArray(content: CharSequence): ByteArray {
        if (content is String) return content.encodeToByteArray()
        return ByteArray(content.length) { index -> content[index].code.toByte() }
    }

}