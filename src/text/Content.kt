package io.github.mucute.qwq.piecetable.text

import io.github.mucute.qwq.piecetable.PieceTable
import io.github.mucute.qwq.piecetable.PieceTableImpl
import io.github.mucute.qwq.piecetable.util.CharSequenceUtil
import io.github.mucute.qwq.piecetable.util.ContentLineUtil
import kotlin.jvm.JvmStatic

/**
 * [Content] is an extent of [PieceTable] with more document operation features.
 *
 * @see PieceTableImpl
 * @see PieceTable
 * */
class Content private constructor(private val pieceTable: PieceTable) : CharSequence,
    PieceTable by pieceTable {

    companion object {

        @JvmStatic
        fun of(content: ByteArray) = Content(PieceTableImpl(content))

        @JvmStatic
        fun of(content: CharSequence = "") = of(CharSequenceUtil.toByteArray(content))

    }

    val contentLines = ArrayList(ContentLineUtil.createFrom(this))

    val contentIndexer = ContentIndexer(this)

    val contentLineCount: Int
        get() = contentLines.size

    override val length: Int
        get() = count

    override operator fun get(index: Int) = getChar(index)

    override fun subSequence(startIndex: Int, endIndex: Int) = of(getBuffer(startIndex, endIndex))

    override fun toString() = getContentString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Content

        return length == other.length && contentEquals(other)
    }

    override fun hashCode(): Int {
        return length
    }

}