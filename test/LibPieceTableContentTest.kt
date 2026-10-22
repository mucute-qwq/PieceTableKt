import io.github.mucute.qwq.piecetable.text.PieceTableContent
import kotlin.test.Test

class LibPieceTableContentTest {

    @Test
    fun instantTest() {
        val content = PieceTableContent.of("Hello World")

        println(content)
    }

}