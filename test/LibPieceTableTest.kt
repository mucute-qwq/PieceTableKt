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

}
