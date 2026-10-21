package io.github.mucute.qwq.piecetable

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
         * @param copy copy the content if needed.
         *
         * @return the new [PieceTable] instance.
         *
         * @see PieceTableImpl
         * */
        @JvmStatic
        fun of(content: ByteArray, copy: Boolean = false): PieceTable = PieceTableImpl(content, copy)

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
        fun of(content: CharSequence = "") = of(ByteArray(content.length) { content[it].code.toByte() })

    }

    /**
     * Return the size of PieceTable content.
     *
     * @see PieceTableImpl.size
     * */
    fun size(): Int

    /**
     * Return the size of pieces.
     *
     * @see PieceTableImpl.pieceSize
     * */
    fun pieceSize(): Int

    /**
     * Return the size of appendOnlyBuffer.
     *
     * @see PieceTableImpl.appendOnlyBufferSize
     * */
    fun appendOnlyBufferSize(): Int

    /**
     * Return the usage size of appendOnlyBuffer.
     *
     * @see PieceTableImpl.appendOnlyBufferUsage
     * */
    fun appendOnlyBufferUsage(): Int

    /**
     * Return the remaining size of appendOnlyBuffer.
     *
     * @see PieceTableImpl.appendOnlyBufferRemaining
     * */
    fun appendOnlyBufferRemaining(): Int

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
     * @param copy copy the content if needed.
     *
     * @see clear
     * */
    fun open(content: ByteArray, copy: Boolean = false): PieceTable

    /**
     * Clear the [PieceTable] and insert the given original content into it.
     *
     * @param content the given original content.
     *
     * @see clear
     * */
    fun open(content: CharSequence): PieceTable

    /**
     * Insert the given content into the [PieceTable].
     *
     * @param pos the position that content will be inserted to.
     * @param content the inserting content.
     * @param copy copy the content if needed.
     *
     * @see PieceTableImpl.insert
     * */
    fun insert(pos: Int, content: ByteArray, copy: Boolean = false): PieceTable

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
     * @param copy copy the content if needed.
     *
     * @see PieceTableImpl.append
     * */
    fun append(content: ByteArray, copy: Boolean = false): PieceTable

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
    fun copyInto(destination: ByteArray, destinationOffset: Int = 0, startPos: Int = 0, endPos: Int = size()): PieceTable

    /**
     * Create a new [ByteArray] from given range of [PieceTable] content.
     *
     * @param startPos the start position of [PieceTable] content.
     * @param endPos the end position of [PieceTable] content.
     *
     * @return the new [ByteArray] with given content of range.
     *
     * @see PieceTableImpl.get
     * */
    fun get(startPos: Int, endPos: Int): ByteArray

    /**
     * Create a new [String] from given range of [PieceTable] content.
     *
     * @param startPos the start position of [PieceTable] content.
     * @param endPos the end position of [PieceTable] content.
     *
     * @return the new [String] with given range.
     *
     * @see PieceTableImpl.get
     * */
    fun getString(startPos: Int, endPos: Int): String

    /**
     * Get a [Byte] from specified position.
     *
     * @param pos the specified position.
     *
     * @return the specified [Byte].
     *
     * @see PieceTableImpl.get
     * */
    operator fun get(pos: Int): Byte

    /**
     * Get the full content of [PieceTable].
     *
     * @return the [ByteArray] with full content.
     *
     * @see PieceTableImpl.getContent
     * */
    fun getContent(): ByteArray

    /**
     * Get the full content [String] of [PieceTable].
     *
     * @return the full content [String].
     *
     * @see PieceTableImpl.getContentString
     * */
    fun getContentString(): String

    /**
     * Get the [List] of [Piece].
     *
     * @return the [List] of [Piece].
     *
     * @see Piece
     * @see PieceTableImpl.getPieces
     * */
    fun getPieces(): List<Piece>

    /**
     * Get the [Piece] by specified [pieceIndex].
     *
     * @param pieceIndex the specified index of pieces.
     *
     * @return the [Piece].
     *
     * @see PieceTableImpl.getPiece
     * */
    fun getPiece(pieceIndex: Int) : Piece

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
     * @param exactMatch [pos] must equals the [Piece] beginning position.
     *
     * @see PieceTableImpl.indexOfPiece
     * */
    fun indexOfPiece(pos: Int, exactMatch: Boolean = false): Int

}