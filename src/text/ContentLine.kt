package io.github.mucute.qwq.piecetable.text

import io.github.mucute.qwq.piecetable.Piece

data class ContentLine(
    val pieces: ArrayList<Piece>
) {

    val count: Int
        get() = pieces.size

    val pieceCount: Int
        get() = pieces.size

}