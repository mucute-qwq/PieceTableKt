import io.github.mucute.qwq.piecetable.PieceTable
import kotlin.test.Test
import kotlin.test.assertTrue

class LibPieceTableTest {

    @Test
    fun instantTest() {
        val text = "Hello World"
        val pieceTable = PieceTable.of(text)
        println("Piece table size: ${pieceTable.size()}, text size: ${text.length}")
        assertTrue { pieceTable.size() == text.length }
    }

    @Test
    fun insertTest() {
        val pieceTable = PieceTable.of("Hello")
        pieceTable.append(" ")
        pieceTable.append("World")
        pieceTable.append("UUU")
        pieceTable.append("CCC")
        pieceTable.deleteRange(5, 12)
        println(pieceTable.toString())
    }

}
