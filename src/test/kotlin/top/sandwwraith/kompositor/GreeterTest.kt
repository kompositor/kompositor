package top.sandwwraith.kompositor

import org.junit.Assert.assertEquals
import org.junit.Test

class GreeterTest {
    @Test
    fun world() {
        val greeter = Greeter()
        assertEquals("Hello, world!", greeter.greet("world"))
    }
}