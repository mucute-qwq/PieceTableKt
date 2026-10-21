package io.github.mucute.qwq.piecetable

import kotlin.jvm.JvmStatic

interface Piece {

    companion object {

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

    var pos: Int

    var bufferPos: Int

    var count: Int

    val type: PieceType

    fun copy(): Piece

    operator fun component1(): Int

    operator fun component2(): Int

    operator fun component3(): Int

    operator fun component4(): PieceType

}