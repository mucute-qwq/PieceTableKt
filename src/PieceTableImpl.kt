package io.github.mucute.qwq.piecetable

import io.github.mucute.qwq.piecetable.util.ByteArrayUtil
import io.github.mucute.qwq.piecetable.util.CharSequenceUtil

internal class PieceTableImpl(
    originalBuffer: ByteArray,
    appendOnlyBuffer: ByteArray = ByteArray(PieceTable.DEFAULT_APPEND_ONLY_BUFFER_SIZE),
    pieces: List<Piece> = arrayListOf(Piece.of(0, 0, originalBuffer.size, PieceType.Origin)),
    count: Int = originalBuffer.size,
    appendOnlyBufferUsage: Int = 0
) : PieceTable {

    override var originalBuffer: ByteArray = originalBuffer
        private set

    override var appendOnlyBuffer: ByteArray = appendOnlyBuffer
        private set

    override val pieces = pieces as? ArrayList<Piece> ?: ArrayList(pieces)

    override var count: Int = count
        private set

    override val pieceCount: Int
        get() = pieces.size

    override val appendOnlyBufferSize: Int
        get() = appendOnlyBuffer.size

    override var appendOnlyBufferUsage: Int = appendOnlyBufferUsage
        private set

    override val appendOnlyBufferRemaining: Int
        get() = appendOnlyBufferSize - appendOnlyBufferUsage

    private val singleByteBuffer = ByteArray(1)

    override fun clear(): PieceTableImpl {
        originalBuffer = ByteArrayUtil.EMPTY_BYTE_ARRAY
        appendOnlyBuffer = ByteArray(PieceTable.DEFAULT_APPEND_ONLY_BUFFER_SIZE)
        count = 0
        appendOnlyBufferUsage = 0
        pieces.clear()
        return this
    }

    override fun open(content: ByteArray): PieceTableImpl {
        clear()
        originalBuffer = content
        count = originalBuffer.size
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

    override fun insert(pos: Int, content: ByteArray): PieceTableImpl {
        requirePos(pos, "Pos", allowEqualsSize = true)
        if (originalBuffer.isEmpty()) return open(content)
        if (pos == 0 || pos == count) {
            val newPiece = Piece.of(pos, appendOnlyBufferUsage, content.size)
            appendToAppendOnlyBuffer(content)
            val pieceIndex = if (pos == 0) 0 else pieces.size
            pieces.add(pieceIndex, newPiece)
            updatePieces(content.size, pieceIndex + 1)
            count += content.size
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
            count += content.size
            return this
        }

        val rightPiece =
            Piece.of(pos + content.size, leftPiece.bufferPos + splitLeftCount, splitRightCount, leftPiece.type)
        pieces.add(leftPieceIndex + 2, rightPiece)
        updatePieces(content.size, leftPieceIndex + 3)
        count += content.size

        return this
    }

    override fun insert(pos: Int, content: CharSequence) = insert(pos, CharSequenceUtil.toByteArray(content))

    override fun append(content: ByteArray) = insert(count, content)

    override fun append(content: CharSequence) = insert(count, content)

    @Suppress("DuplicatedCode")
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
                count -= deletedCount
                return this
            }

            if (startPos == startPiece.pos || endPos == endPiece.pos + endPiece.count) {
                if (startPos == startPiece.pos) {
                    startPiece.bufferPos += deletedCount
                }
                startPiece.count -= deletedCount
                updatePieces(-deletedCount, fromPieceIndex = startPieceIndex + 1)
                count -= deletedCount
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
            count -= deletedCount

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
        count -= deletedCount

        return this
    }

    override fun deleteAt(pos: Int) = deleteRange(pos, pos + 1)

    @Suppress("DuplicatedCode")
    override fun copyInto(destination: ByteArray, destinationOffset: Int, startPos: Int, endPos: Int): PieceTableImpl {
        requirePosRange(startPos, endPos)

        val startPieceIndex = indexOfPiece(startPos).takeIf { it >= 0 } ?: return this
        val startPiece = pieces[startPieceIndex]
        val endPieceIndex =
            indexOfPiece(endPos, fromPieceIndex = startPieceIndex, reachEnd = true).takeIf { it >= 0 } ?: return this
        val endPiece = pieces[endPieceIndex]
        var offset = destinationOffset

        if (startPieceIndex == endPieceIndex) {
            val source = when (startPiece.type) {
                PieceType.Origin -> originalBuffer
                PieceType.Append -> appendOnlyBuffer
            }
            val copyStartIndex = startPiece.bufferPos + (startPos - startPiece.pos)
            val copyEndIndex = copyStartIndex + (endPos - startPos)
            source.copyInto(destination, offset, copyStartIndex, copyEndIndex)
            return this
        }

        val startBuffer = when(startPiece.type) {
            PieceType.Origin -> originalBuffer
            PieceType.Append -> appendOnlyBuffer
        }
        val startPieceCopyStart = startPiece.bufferPos + (startPos - startPiece.pos)
        val startPieceCopyEnd = startPiece.bufferPos + startPiece.count

        startBuffer.copyInto(destination, offset, startPieceCopyStart, startPieceCopyEnd)
        offset += startPieceCopyEnd - startPieceCopyStart

        for (pieceIndex in startPieceIndex + 1 until endPieceIndex) {
            val piece = pieces[pieceIndex]
            val buffer = when(piece.type) {
                PieceType.Origin -> originalBuffer
                PieceType.Append -> appendOnlyBuffer
            }
            buffer.copyInto(destination, offset, piece.bufferPos, piece.bufferPos + piece.count)
            offset += piece.count
        }

        val endBuffer = when (endPiece.type) {
            PieceType.Origin -> originalBuffer
            PieceType.Append -> appendOnlyBuffer
        }
        val endPieceCopyStart = endPiece.bufferPos
        val endPieceCopyEnd = endPiece.bufferPos + (endPos - endPiece.pos)
        endBuffer.copyInto(destination, offset, endPieceCopyStart, endPieceCopyEnd)
        return this
    }

    override fun getBuffer(startPos: Int, endPos: Int) =
        ByteArray(maxOf(endPos - startPos, 0)).apply { this@PieceTableImpl.copyInto(this, 0, startPos, endPos) }

    override fun getString(startPos: Int, endPos: Int) = getBuffer(startPos, endPos).decodeToString()

    override fun getByte(pos: Int) = singleByteBuffer.apply { this@PieceTableImpl.copyInto(this, 0, pos, pos + 1) }.first()

    override fun getChar(pos: Int) = getByte(pos).toInt().toChar()

    override fun getContentBuffer() = if (count > 0) getBuffer(0, count) else ByteArray(0)

    override fun getContentString() = getContentBuffer().decodeToString()

    override fun toString() =
        "PieceTable(size=${count}, pieceSize=${pieceCount}, appendOnlyBufferSize=${appendOnlyBufferSize}, appendOnlyBufferUsage=$appendOnlyBufferUsage, appendOnlyBufferRemaining=${appendOnlyBufferRemaining}, pieces=${piecesToString()})"

    override fun piecesToString() = buildString {
        append("[")
        if (pieceCount > 0) {
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
                if (index + 1 < pieceCount) {
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
        val range = if (allowEqualsSize) 0..count else 0 until count
        require(pos in range) { "$message $pos out of range [0, ${count})" }
    }

    private fun requirePosRange(startPos: Int, endPos: Int) {
        require(startPos < endPos) { "Start pos $startPos >= end pos $endPos" }
        requirePos(startPos, "Start pos")
        requirePos(endPos, "End pos", allowEqualsSize = true)
    }

}