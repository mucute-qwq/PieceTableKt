package io.github.mucute.qwq.piecetable.util

import io.github.mucute.qwq.piecetable.text.Content
import io.github.mucute.qwq.piecetable.text.ContentLine

object ContentLineUtil {

    fun createFrom(content: Content): List<ContentLine> = buildList {
        val length = content.length
        var index = 0
        while (index < length) {
            val char = content[index]

            when (char) {
                '\n' -> {

                }
                '\r' -> {}
            }

            index++
        }
    }

}