package io.github.mucute.qwq.piecetable

import io.github.mucute.qwq.piecetable.util.CharSequenceUtil
import kotlin.jvm.JvmStatic

/**
 * The Piece Table text data structure interface definition.
 *
 * An implementation of it is [PieceTableImpl].
 *
 * @see Piece
 * @see PieceTableImpl
 * */
interface PieceTable {

    companion object {

        /**
         * The default size of appendOnlyBuffer.
         *
         * @see PieceTableImpl.appendOnlyBuffer
         * */
        const val DEFAULT_APPEND_ONLY_BUFFER_SIZE: Int = 1024

        /**
         * Create a new [PieceTable] instance with given original [content].
         *
         * @param content the given original content.
         *
         * @return the new [PieceTable] instance.
         *
         * @see PieceTableImpl
         * */
        @JvmStatic
        fun of(content: ByteArray): PieceTable = PieceTableImpl(content)

        /**
         * Create a new [PieceTable] instance with given original [content].
         *
         * @param content the given original content.
         *
         * @return the new [PieceTable] instance.
         *
         * @see PieceTableImpl
         * */
        @JvmStatic
        fun of(content: CharSequence = "") = of(CharSequenceUtil.toByteArray(content))

    }

    /**
     * The originalBuffer.
     *
     * @see PieceTableImpl.originalBuffer
     * */
    val originalBuffer: ByteArray

    /**
     * The appendOnlyBuffer.
     *
     * @see PieceTableImpl.appendOnlyBuffer
     * */
    val appendOnlyBuffer: ByteArray

    /**
     * The [List] of [Piece].
     *
     * @see PieceTableImpl.pieces
     * */
    val pieces: List<Piece>

    /**
     * The count of PieceTable content.
     *
     * @see PieceTableImpl.count
     * */
    val count: Int

    /**
     * The size of pieces.
     *
     * @see PieceTableImpl.pieceCount
     * */
    val pieceCount: Int

    /**
     * The size of appendOnlyBuffer.
     *
     * @see PieceTableImpl.appendOnlyBufferSize
     * */
    val appendOnlyBufferSize: Int

    /**
     * The usage size of appendOnlyBuffer.
     *
     * @see PieceTableImpl.appendOnlyBufferUsage
     * */
    val appendOnlyBufferUsage: Int

    /**
     * The remaining size of appendOnlyBuffer.
     *
     * @see PieceTableImpl.appendOnlyBufferRemaining
     * */
    val appendOnlyBufferRemaining: Int

    /**
     * Clear the content of [PieceTable], releasing the buffers, and reset the size.
     *
     * @see PieceTableImpl.clear
     * */
    fun clear(): PieceTable

    /**
     * Clear the [PieceTable] and insert the given original content into it.
     *
     * @param content the given original content.
     *
     * @see PieceTableImpl.open
     * */
    fun open(content: ByteArray): PieceTable

    /**
     * Clear the [PieceTable] and insert the given original content into it.
     *
     * @param content the given original content.
     *
     * @see PieceTableImpl.open
     * */
    fun open(content: CharSequence): PieceTable

    /**
     * Insert the given content into the [PieceTable].
     *
     * @param pos the position that content will be inserted to.
     * @param content the inserting content.
     *
     * @see PieceTableImpl.insert
     * */
    fun insert(pos: Int, content: ByteArray): PieceTable

    /**
     * Insert the given content into the [PieceTable].
     *
     * @param pos the position that content will be inserted to.
     * @param content the inserting content.
     *
     * @see PieceTableImpl.insert
     * */
    fun insert(pos: Int, content: CharSequence): PieceTable

    /**
     * Insert the given content into the end of [PieceTable].
     *
     * @param content the inserting content.
     *
     * @see PieceTableImpl.append
     * */
    fun append(content: ByteArray): PieceTable

    /**
     * Insert the given content into the end of [PieceTable].
     *
     * @param content the inserting content.
     *
     * @see PieceTableImpl.append
     * */
    fun append(content: CharSequence): PieceTable

    /**
     * Delete the content of given range.
     *
     * @param startPos the start position of deletion.
     * @param endPos the end position of deletion.
     *
     * @see PieceTableImpl.deleteRange
     * */
    fun deleteRange(startPos: Int, endPos: Int): PieceTable

    /**
     * Delete a char of given position.
     *
     * @param pos the position of deletion.
     *
     * @see PieceTableImpl.deleteAt
     * */
    fun deleteAt(pos: Int): PieceTable

    /**
     * Copy the content of [PieceTable] into another [ByteArray].
     *
     * @param destination the [ByteArray] copied to.
     * @param destinationOffset the beginning offset copied to of [ByteArray].
     * @param startPos the start position of [PieceTable] content.
     * @param endPos the end position of [PieceTable] content.
     *
     * @see PieceTableImpl.copyInto
     * */
    fun copyInto(destination: ByteArray, destinationOffset: Int = 0, startPos: Int = 0, endPos: Int = count): PieceTable

    /**
     * Create a new [ByteArray] from given range of [PieceTable] content.
     *
     * @param startPos the start position of [PieceTable] content.
     * @param endPos the end position of [PieceTable] content.
     *
     * @return the new [ByteArray] with given content of range.
     *
     * @see PieceTableImpl.getBuffer
     * */
    fun getBuffer(startPos: Int, endPos: Int): ByteArray

    /**
     * Create a new [String] from given range of [PieceTable] content.
     *
     * @param startPos the start position of [PieceTable] content.
     * @param endPos the end position of [PieceTable] content.
     *
     * @return the new [String] with given range.
     *
     * @see PieceTableImpl.getString
     * */
    fun getString(startPos: Int, endPos: Int): String

    /**
     * Get a [Byte] from specified position.
     *
     * @param pos the specified position.
     *
     * @return the specified [Byte].
     *
     * @see PieceTableImpl.getByte
     * */
    fun getByte(pos: Int): Byte

    /**
     * Get a [Char] from specified position.
     *
     * @param pos the specified position.
     *
     * @return the specified [Char].
     *
     * @see PieceTableImpl.getChar
     * */
    fun getChar(pos: Int): Char

    /**
     * Get the full content of [PieceTable].
     *
     * @return the [ByteArray] with full content.
     *
     * @see PieceTableImpl.getContentBuffer
     * */
    fun getContentBuffer(): ByteArray

    /**
     * Get the full content [String] of [PieceTable].
     *
     * @return the full content [String].
     *
     * @see PieceTableImpl.getContentString
     * */
    fun getContentString(): String

    /**
     * Convert to the [String] expression.
     *
     * @see PieceTableImpl.toString
     * */
    override fun toString(): String

    /**
     * Convert pieces to the [String] expression.
     *
     * @see PieceTableImpl.piecesToString
     * */
    fun piecesToString(): String

    /**
     * Search for the index of given [piece] in [Piece] list.
     *
     * @param piece the piece.
     *
     * @return the index of [piece].
     * If not found, it returns -1.
     *
     * @see PieceTableImpl.indexOfPiece
     * */
    fun indexOfPiece(piece: Piece): Int

    /**
     * Search for the index of given piece [pos] in [Piece] list.
     *
     * @param pos the position in [PieceTable].
     * @param fromPieceIndex the start piece index to search.
     * @param reachEnd can the position reach the piece end position.
     * @param exactMatch [pos] must equals the [Piece] beginning position.
     *
     * @see PieceTableImpl.indexOfPiece
     * */
    fun indexOfPiece(pos: Int, fromPieceIndex: Int = 0, reachEnd: Boolean = false, exactMatch: Boolean = false): Int

}