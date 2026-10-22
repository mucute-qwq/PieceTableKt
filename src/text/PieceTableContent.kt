package io.github.mucute.qwq.piecetable.text

import io.github.mucute.qwq.piecetable.PieceTable
import io.github.mucute.qwq.piecetable.PieceTableImpl
import io.github.mucute.qwq.piecetable.data.LinePieces
import io.github.mucute.qwq.piecetable.util.CharSequenceUtil
import kotlin.jvm.JvmStatic

/**
 * [PieceTableContent] is an extent of [PieceTable] with more document operation features.
 *
 * @see PieceTableImpl
 * @see PieceTable
 * */
class PieceTableContent private constructor(private val pieceTable: PieceTable) : CharSequence,
    PieceTable by pieceTable {

    companion object {

        @JvmStatic
        fun of(content: ByteArray, copy: Boolean = false) = PieceTableContent(PieceTableImpl(content, copy))

        @JvmStatic
        fun of(content: CharSequence = "") = of(CharSequenceUtil.toByteArray(content))

    }

    private val linePiecesList = ArrayList(LinePieces.from(pieceTable.getPieces()))

    private val indexer: Indexer = PieceTableContentIndexer(this)

    fun lineCount() = linePiecesList.size

    fun columnCount(line: Int) = linePiecesList[line].count

    override val length: Int
        get() = size()

    override fun clear(): PieceTableContent {
        pieceTable.clear()
        return this
    }

    override fun open(content: ByteArray, copy: Boolean): PieceTableContent {
        pieceTable.open(content, copy)
        return this
    }

    override fun open(content: CharSequence) = open(CharSequenceUtil.toByteArray(content))

    override fun insert(pos: Int, content: ByteArray, copy: Boolean): PieceTableContent {
        pieceTable.insert(pos, content, copy)
        return this
    }

    override fun insert(pos: Int, content: CharSequence) = insert(pos, CharSequenceUtil.toByteArray(content))

    fun insert(line: Int, column: Int, content: ByteArray, copy: Boolean = false) =
        insert(indexer.getIndex(line, column), content, copy)

    fun insert(line: Int, column: Int, content: CharSequence) =
        insert(line, column, CharSequenceUtil.toByteArray(content))

    override fun append(content: ByteArray, copy: Boolean) = insert(size(), content, copy)

    override fun append(content: CharSequence) = append(CharSequenceUtil.toByteArray(content))

    override fun deleteRange(startPos: Int, endPos: Int): PieceTableContent {
        pieceTable.deleteRange(startPos, endPos)
        return this
    }

    fun deleteRange(startLine: Int, startColumn: Int, endLine: Int, endColumn: Int) =
        deleteRange(indexer.getIndex(startLine, startColumn), indexer.getIndex(endLine, endColumn))

    override fun deleteAt(pos: Int) = deleteRange(pos, pos + 1)

    fun deleteAt(line: Int, column: Int) = deleteRange(line, column, line, column + 1)

    override fun copyInto(
        destination: ByteArray,
        destinationOffset: Int,
        startPos: Int,
        endPos: Int
    ): PieceTableContent {
        pieceTable.copyInto(destination, destinationOffset, startPos, endPos)
        return this
    }

    fun copyInto(
        destination: ByteArray,
        destinationOffset: Int,
        startLine: Int,
        startColumn: Int,
        endLine: Int,
        endColumn: Int
    ) = copyInto(
        destination,
        destinationOffset,
        indexer.getIndex(startLine, startColumn),
        indexer.getIndex(endLine, endColumn)
    )

    override fun toString() = getContentString()

    override operator fun get(index: Int) = getChar(index)

    fun getByte(line: Int, column: Int) = getByte(indexer.getIndex(line, column))

    fun getChar(line: Int, column: Int) = getByte(line, column).toInt().toChar()

    override fun subSequence(startIndex: Int, endIndex: Int): PieceTableContent {
        return of(get(startIndex, endIndex))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PieceTableContent

        return length == other.length && contentEquals(other)
    }

    override fun hashCode(): Int {
        return length
    }


}