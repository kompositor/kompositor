package top.sandwwraith.kompositor

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.StringReader
import java.io.StringWriter

class MustacheTests {
    @Test
    fun convertsNormally() {
        val converter = MustacheConverter()
        val data = mapOf("x" to "y")
        val input = StringReader("Value: {{x}}")
        val output = StringWriter()
        converter.converter(data).invoke(input, output.buffered())
        assertEquals("Value: y", output.toString())
    }
}