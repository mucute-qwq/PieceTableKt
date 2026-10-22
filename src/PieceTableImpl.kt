package io.github.mucute.qwq.piecetable

import io.github.mucute.qwq.piecetable.util.ByteArrayUtil
import io.github.mucute.qwq.piecetable.util.CharSequenceUtil

internal class PieceTableImpl(
    private var originalBuffer: ByteArray,
    copy: Boolean
) : PieceTable {

    private var appendOnlyBuffer = ByteArrayUtil.EMPTY_BYTE_ARRAY

    private val pieces = ArrayList<Piece>()

    private var size = 0

    private var appendOnlyBufferUsage = 0

    init {
        open(originalBuffer, copy)
    }

    override fun size() = size

    override fun pieceSize() = pieces.size

    override fun appendOnlyBufferSize() = appendOnlyBuffer.size

    override fun appendOnlyBufferUsage() = appendOnlyBufferUsage

    override fun appendOnlyBufferRemaining() = appendOnlyBufferSize() - appendOnlyBufferUsage()

    override fun clear(): PieceTableImpl {
        originalBuffer = ByteArrayUtil.EMPTY_BYTE_ARRAY
        appendOnlyBuffer = ByteArray(PieceTable.DEFAULT_APPEND_ONLY_BUFFER_SIZE)
        size = 0
        appendOnlyBufferUsage = 0
        pieces.clear()
        return this
    }

    override fun open(content: ByteArray, copy: Boolean): PieceTableImpl {
        clear()
        originalBuffer = if (copy) content.copyOf() else content
        size = originalBuffer.size
        pieces.add(
            Piece.of(
                pos = 0,
                bufferPos = 0,
                count = originalBuffer.size,
                type = PieceType.Origin
            )
        )
        return this
    }

    override fun open(content: CharSequence) = open(CharSequenceUtil.toByteArray(content))

    override fun insert(pos: Int, content: ByteArray, copy: Boolean): PieceTableImpl {
        requirePos(pos, "Pos", allowEqualsSize = true)
        if (originalBuffer.isEmpty()) return open(content, copy)
        val content = if (copy) content.copyOf() else content
        if (pos == 0 || pos == size()) {
            val newPiece = Piece.of(pos, appendOnlyBufferUsage, content.size)
            appendToAppendOnlyBuffer(content)
            val pieceIndex = if (pos == 0) 0 else pieces.size
            pieces.add(pieceIndex, newPiece)
            updatePieces(content.size, pieceIndex + 1)
            size += content.size
            return this
        }

        val leftPieceIndex = indexOfPiece(pos, reachEnd = true).takeIf { it >= 0 } ?: return this
        val leftPiece = pieces[leftPieceIndex]

        val splitLeftCount = pos - leftPiece.pos
        val splitRightCount = leftPiece.count - splitLeftCount

        leftPiece.count = splitLeftCount

        val newPiece = Piece.of(pos, appendOnlyBufferUsage, content.size)
        appendToAppendOnlyBuffer(content)
        pieces.add(leftPieceIndex + 1, newPiece)

        if (splitRightCount <= 0) {
            updatePieces(content.size, leftPieceIndex + 2)
            size += content.size
            return this
        }

        val rightPiece =
            Piece.of(pos + content.size, leftPiece.bufferPos + splitLeftCount, splitRightCount, leftPiece.type)
        pieces.add(leftPieceIndex + 2, rightPiece)
        updatePieces(content.size, leftPieceIndex + 3)
        size += content.size

        return this
    }

    override fun insert(pos: Int, content: CharSequence) = insert(pos, CharSequenceUtil.toByteArray(content))

    override fun append(content: ByteArray, copy: Boolean) = insert(size(), content, copy)

    override fun append(content: CharSequence) = insert(size(), content)

    override fun deleteRange(startPos: Int, endPos: Int): PieceTableImpl {
        requirePosRange(startPos, endPos)
        val startPieceIndex = indexOfPiece(startPos).takeIf { it >= 0 } ?: return this
        val startPiece = pieces[startPieceIndex]
        val endPieceIndex =
            indexOfPiece(endPos, fromPieceIndex = startPieceIndex, reachEnd = true).takeIf { it >= 0 } ?: return this
        val endPiece = pieces[endPieceIndex]
        val deletedCount = endPos - startPos

        if (startPieceIndex == endPieceIndex) {
            if (startPos == startPiece.pos && endPos == endPiece.pos + endPiece.count) {
                pieces.removeAt(startPieceIndex)
                updatePieces(-deletedCount, fromPieceIndex = startPieceIndex)
                size -= deletedCount
                return this
            }

            if (startPos == startPiece.pos || endPos == endPiece.pos + endPiece.count) {
                if (startPos == startPiece.pos) {
                    startPiece.bufferPos += deletedCount
                }
                startPiece.count -= deletedCount
                updatePieces(-deletedCount, fromPieceIndex = startPieceIndex + 1)
                size -= deletedCount
                return this
            }

            val splitLeftCount = startPos - startPiece.pos
            val splitRightCount = startPiece.count - splitLeftCount - deletedCount

            startPiece.count = splitLeftCount

            val rightPiece = Piece.of(
                startPiece.pos + splitLeftCount,
                startPiece.bufferPos + splitLeftCount + deletedCount,
                splitRightCount,
                startPiece.type
            )
            pieces.add(startPieceIndex + 1, rightPiece)
            updatePieces(-deletedCount, startPieceIndex + 2)
            size -= deletedCount

            return this
        }

        val startCount = startPos - startPiece.pos
        val endDeletedCount = endPos - endPiece.pos
        val endCount = endPiece.count - endDeletedCount
        val removeListSize = endPieceIndex - startPieceIndex + when {
            startCount <= 0 && endCount <= 0 -> 2
            startCount <= 0 || endCount <= 0 -> 1
            else -> 0
        }
        val removeList = ArrayList<Piece>(removeListSize)

        startPiece.count = startCount
        endPiece.pos = startPiece.pos + startCount
        endPiece.bufferPos += endDeletedCount
        endPiece.count = endCount

        if (startCount <= 0) {
            removeList.add(startPiece)
        }

        for (pieceIndex in startPieceIndex + 1 until endPieceIndex) {
            removeList.add(pieces[pieceIndex])
        }

        if (endCount <= 0) {
            removeList.add(endPiece)
        }

        updatePieces(-deletedCount, endPieceIndex + 1)
        pieces.removeAll(removeList)
        size -= deletedCount

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

    override fun getPieces() = pieces

    override fun getPiece(pieceIndex: Int) = pieces[pieceIndex].copy()

    override fun toString() =
        "PieceTable(size=${size()}, pieceSize=${pieceSize()}, appendOnlyBufferSize=${appendOnlyBufferSize()}, appendOnlyBufferUsage=$appendOnlyBufferUsage, appendOnlyBufferRemaining=${appendOnlyBufferRemaining()}, pieces=${piecesToString()})"

    override fun piecesToString() = buildString {
        append("[")
        if (pieceSize() > 0) {
            appendLine()
            for ((index, piece) in pieces.withIndex()) {
                val (pos, bufferPos, count, type) = piece
                val buffer = when (type) {
                    PieceType.Origin -> originalBuffer
                    PieceType.Append -> appendOnlyBuffer
                }
                val content = try {
                    buffer.decodeToString(bufferPos, bufferPos + count)
                } catch (e: Exception) {
                    "Error: ${e.message}"
                }
                append("\tPiece(pos=$pos, bufferPos=$bufferPos, count=$count, content=\"$content\", type=$type)")
                if (index + 1 < pieceSize()) {
                    append(", ")
                }
                appendLine()
            }
        }
        append("]")
    }

    override fun indexOfPiece(piece: Piece) = pieces.indexOf(piece)

    override fun indexOfPiece(pos: Int, fromPieceIndex: Int, reachEnd: Boolean, exactMatch: Boolean): Int {
        for ((index, piece) in pieces.listIterator(fromPieceIndex).withIndex()) {
            if (exactMatch) {
                if (pos == piece.pos) return index + fromPieceIndex
            } else {
                val range =
                    if (reachEnd) piece.pos..piece.pos + piece.count else piece.pos until piece.pos + piece.count
                if (pos in range) return index + fromPieceIndex
            }
        }
        return -1
    }

    private fun updatePieces(moddedPos: Int, fromPieceIndex: Int, toPieceIndex: Int = pieces.size) {
        for (pieceIndex in fromPieceIndex until toPieceIndex) {
            val piece = pieces[pieceIndex]
            piece.pos += moddedPos
        }
    }

    private fun appendToAppendOnlyBuffer(content: ByteArray) {
        if (appendOnlyBufferUsage + content.size <= appendOnlyBuffer.size) {
            content.copyInto(appendOnlyBuffer, appendOnlyBufferUsage)
            appendOnlyBufferUsage += content.size
            return
        }

        val newAppendOnlyBuffer =
            appendOnlyBuffer.copyOf(appendOnlyBuffer.size + content.size + PieceTable.DEFAULT_APPEND_ONLY_BUFFER_SIZE)
        content.copyInto(newAppendOnlyBuffer, appendOnlyBufferUsage)
        appendOnlyBuffer = newAppendOnlyBuffer
        appendOnlyBufferUsage += content.size
    }

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