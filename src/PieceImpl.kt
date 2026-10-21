package io.github.mucute.qwq.piecetable

internal data class PieceImpl(
    override var pos: Int,
    override var bufferPos: Int,
    override var count: Int,
    override val type: PieceType
) : Piece