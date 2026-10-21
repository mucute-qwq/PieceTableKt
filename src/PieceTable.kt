package io.github.mucute.qwq.piecetable

import kotlin.jvm.JvmStatic

interface PieceTable {

    companion object {

        const val DEFAULT_APPEND_ONLY_BUFFER_SIZE: Int = 1024

        @JvmStatic
        fun of(content: ByteArray, copy: Boolean = false): PieceTable = PieceTableImpl(content, copy)


        @JvmStatic
        fun of(content: CharSequence = "") = of(ByteArray(content.length) { content[it].code.toByte() })

    }

    fun size(): Int

    fun pieceSize(): Int

    fun clear(): PieceTable

    fun open(content: ByteArray, copy: Boolean = false): PieceTable

    fun open(content: CharSequence): PieceTable

    fun insert(pos: Int, content: ByteArray): PieceTable

    fun insert(pos: Int, content: CharSequence): PieceTable

    fun append(content: ByteArray): PieceTable

    fun append(content: CharSequence): PieceTable

    fun deleteRange(startPos: Int, endPos: Int): PieceTable

    fun deleteAt(pos: Int): PieceTable

    fun copyInto(destination: ByteArray, destinationOffset: Int = 0, startPos: Int = 0, endPos: Int = size()): PieceTable

    fun get(startPos: Int, endPos: Int): ByteArray

    fun getString(startPos: Int, endPos: Int): String

    operator fun get(pos: Int): Byte

    fun getContent(): ByteArray

    fun getContentString(): String

    fun getPiece(pieceIndex: Int) : Piece

    fun indexOfPiece(piece: Piece): Int

    fun indexOfPiece(pos: Int, exactMatch: Boolean = false): Int

}