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

}