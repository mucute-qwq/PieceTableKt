package io.github.mucute.qwq.piecetable.data

import io.github.mucute.qwq.piecetable.Piece
import kotlin.jvm.JvmStatic

class LinePieces(
    pieces: List<Piece>,
) {

    companion object {

        @JvmStatic
        fun from(pieces: List<Piece>): List<LinePieces> {
            TODO()
        }

    }

    private val pieces = ArrayList<Piece>()

    val piecesCount: Int
        get() = pieces.size

    val count: Int
        get() = pieces.sumOf { it.count }

    fun getPieces() = pieces

    fun getPiece(pieceIndex: Int) = pieces[pieceIndex]

}