package io.github.mucute.qwq.piecetable.text

import io.github.mucute.qwq.piecetable.data.Position

interface Indexer {

    fun getIndex(line: Int, column: Int): Int

    fun getLine(index: Int): Int

    fun getColumn(index: Int): Int

    fun getPosition(index: Int): Position

    fun getPosition(line: Int, column: Int): Position

}