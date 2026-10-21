package io.github.mucute.qwq.piecetable

import kotlin.jvm.JvmStatic

/**
 * The piece data structure interface definition.
 *
 * An implementation of it is [PieceImpl].
 *
 * @see PieceTable
 * @see PieceImpl
 * */
interface Piece {

    companion object {

        /**
         * Create a new piece.
         *
         * @param pos the beginning position of content.
         * @param bufferPos the beginning position of buffer.
         * @param count the content count.
         * @param type the piece type.
         *
         * @return the new [Piece] implementation.
         *
         * @see PieceType
         * @see PieceImpl
         * */
        @JvmStatic
        fun of(
            pos: Int,
            bufferPos: Int,
            count: Int,
            type: PieceType = PieceType.Append
        ): Piece = PieceImpl(
            pos = pos,
            bufferPos = bufferPos,
            count = count,
            type = type
        )

    }

    /**
     * The beginning position of content.
     *
     * @see PieceImpl.pos
     * */
    var pos: Int

    /**
     * The beginning position of buffer.
     *
     * @see PieceImpl.bufferPos
     * */
    var bufferPos: Int

    /**
     * The content count.
     *
     * @see PieceImpl.count
     * */
    var count: Int

    /**
     * The piece type.
     *
     * @see PieceImpl.type
     * */
    val type: PieceType

    /**
     * Copy this [Piece].
     *
     * @return the copied new piece.
     *
     * @see PieceImpl.copy
     * */
    fun copy(): Piece

    operator fun component1(): Int

    operator fun component2(): Int

    operator fun component3(): Int

    operator fun component4(): PieceType

}