import io.github.mucute.qwq.piecetable.PieceTable
import kotlin.test.Test
import kotlin.test.assertEquals

class LibPieceTableTest {

    @Test
    fun instantTest() {
        val text = "Hello World"
        val pieceTable = PieceTable.of(text)

        println("PieceTable: ${pieceTable.getContentString()}")

        assertEquals(text, pieceTable.getContentString())
        assertEquals(text.length, pieceTable.count())
    }

    @Test
    fun insertTest() {
        val stringBuilder = StringBuilder("Hello")
            .append(" ")
            .append("World")
            .append("!")
            .insert(0, "Hi,")
            .insert(3, " ")
            .insert(4, "user!")
            .insert(9, " ")

        val pieceTable = PieceTable.of("Hello")
            .append(" ")
            .append("World")
            .append("!")
            .insert(0, "Hi,")
            .insert(3, " ")
            .insert(4, "user!")
            .insert(9, " ")

        println("StringBuilder: $stringBuilder")
        println("PieceTable: ${pieceTable.getContentString()}")

        assertEquals(stringBuilder.toString(), pieceTable.getContentString())
        assertEquals(stringBuilder.length, pieceTable.count())
    }

    @Test
    fun deleteTest() {
        val text = "Hello World"
        val stringBuilder = StringBuilder(text)
            .deleteRange(1, 4)
            .deleteAt(6)

        val pieceTable = PieceTable.of(text)
            .deleteRange(1, 4)
            .deleteAt(6)

        println("StringBuilder: $stringBuilder")
        println("PieceTable: ${pieceTable.getContentString()}")

        assertEquals(stringBuilder.toString(), pieceTable.getContentString())
        assertEquals(stringBuilder.length, pieceTable.count())
    }

    @Test
    fun getTest() {
        val text = "Hi, user! Hello World!"
        val stringBuilderSubstring = StringBuilder(text)
            .substring(4, 9)

        val pieceTableSubstring = PieceTable.of(text)
            .getString(4, 9)

        println("StringBuilderSubstring: $stringBuilderSubstring")
        println("PieceTableSubstring: $pieceTableSubstring")

        assertEquals(stringBuilderSubstring, pieceTableSubstring)
        assertEquals(stringBuilderSubstring.length, pieceTableSubstring.length)
    }

}
