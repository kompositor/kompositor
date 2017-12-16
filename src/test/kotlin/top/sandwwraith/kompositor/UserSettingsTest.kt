package top.sandwwraith.kompositor

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.StringReader
import java.nio.file.Files
import java.nio.file.Paths

class UserSettingsTest {
    val str = """group: top.sandwwraith
        |version: 0.1-SNAPSHOT
        |""".trimMargin()

    @Test
    fun testParsesSimpleConfig() {
        val reader = StringReader(str)
        val settings = LoadSettings.fromYaml(reader)
        assertEquals(mapOf("group" to "top.sandwwraith", "version" to "0.1-SNAPSHOT"), settings)
    }

    @Test
    fun composeCorrectly() {
        val tmp = Files.createTempFile(Paths.get("."), ".komp", null)
        Files.newBufferedWriter(tmp).use {
            it.write(str)
            it.newLine()
        }
        val overrides = mapOf("version" to "1.1")
        val settings = composeConfig(tmp, overrides, useHome = false)
        assertEquals(mapOf("version" to "1.1", "group" to "top.sandwwraith"), settings)
    }
}