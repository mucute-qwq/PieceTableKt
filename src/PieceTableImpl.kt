package io.github.mucute.qwq.piecetable

import io.github.mucute.qwq.piecetable.util.ByteArrayUtil
import io.github.mucute.qwq.piecetable.util.CharSequenceUtil

internal class PieceTableImpl(
    private var origin: ByteArray,
    copy: Boolean
) : PieceTable {

    private var appendOnlyBuffer = ByteArrayUtil.EMPTY_BYTE_ARRAY

    private var size = 0

    private val pieces = ArrayList<Piece>()

    init {
        open(origin, copy)
    }

    override fun size() = size

    override fun pieceSize() = pieces.size

    override fun clear(): PieceTableImpl {
        origin = ByteArrayUtil.EMPTY_BYTE_ARRAY
        appendOnlyBuffer = ByteArray(PieceTable.DEFAULT_APPEND_ONLY_BUFFER_SIZE)
        size = 0
        pieces.clear()
        return this
    }

    override fun open(content: ByteArray, copy: Boolean): PieceTableImpl {
        clear()
        origin = if (copy) content.copyOf() else content
        size = origin.size
        pieces.add(
            Piece.of(
                pos = 0,
                bufferPos = 0,
                count = origin.size,
                type = PieceType.Origin
            )
        )
        return this
    }

    override fun open(content: CharSequence) = open(CharSequenceUtil.toByteArray(content))

    override fun insert(pos: Int, content: ByteArray): PieceTableImpl {
        requirePos(pos, "Pos", allowEqualsSize = true)
        if (origin.isEmpty()) return open(content)
        return this
    }

    override fun insert(pos: Int, content: CharSequence) = insert(pos, CharSequenceUtil.toByteArray(content))

    override fun append(content: ByteArray) = insert(size(), content)

    override fun append(content: CharSequence) = insert(size(), content)

    override fun deleteRange(startPos: Int, endPos: Int): PieceTableImpl {
        requirePosRange(startPos, endPos)
        return this
    }

    override fun deleteAt(pos: Int) = deleteRange(pos, pos + 1)

    override fun copyInto(destination: ByteArray, destinationOffset: Int, startPos: Int, endPos: Int): PieceTableImpl {
        requirePosRange(startPos, endPos)
        return this
    }

    override fun get(startPos: Int, endPos: Int) =
        ByteArray(maxOf(endPos - startPos, 0)).apply { this@PieceTableImpl.copyInto(this, 0, startPos, endPos) }

    override fun getString(startPos: Int, endPos: Int) = get(startPos, endPos).decodeToString()

    override operator fun get(pos: Int) = get(pos, pos + 1).first()

    override fun getContent() = if (size() > 0) get(0, size()) else ByteArray(0)

    override fun getContentString() = getContent().decodeToString()

    override fun getPiece(pieceIndex: Int) = pieces[pieceIndex]

    override fun indexOfPiece(piece: Piece) = pieces.indexOf(piece)

    override fun indexOfPiece(pos: Int, exactMatch: Boolean) =
        pieces.indexOfFirst { if (exactMatch) pos == it.pos else pos in it.pos until it.count }

    private fun requirePos(pos: Int, message: String, allowEqualsSize: Boolean = false) {
        val range = if (allowEqualsSize) 0..size() else 0 until size()
        require(pos in range) { "$message $pos out of range [0, ${size()})" }
    }

    private fun requirePosRange(startPos: Int, endPos: Int) {
        require(startPos < endPos) { "Start pos $startPos >= end pos $endPos" }
        requirePos(startPos, "Start pos")
        requirePos(endPos, "End pos", allowEqualsSize = true)
    }

}