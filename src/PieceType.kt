package io.github.mucute.qwq.piecetable

/**
 * The type of [Piece] buffer.
 *
 * [Origin] represents the originalBuffer.
 *
 * [Append] represents the appendOnlyBuffer.
 *
 * @see PieceTableImpl.originalBuffer
 * @see PieceTableImpl.appendOnlyBuffer
 * @see Piece.type
 * */
enum class PieceType {

    Origin, Append

}