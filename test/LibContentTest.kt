import io.github.mucute.qwq.piecetable.text.Content
import kotlin.test.Test

class LibContentTest {

    @Test
    fun instantTest() {
        val content = Content.of("Hello World")

        println(content)
    }

}