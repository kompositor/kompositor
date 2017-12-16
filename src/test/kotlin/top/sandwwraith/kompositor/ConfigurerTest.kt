package top.sandwwraith.kompositor

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.nio.file.Paths

/**
 * Utikeev Stanislav
 * utikeev@gmail.com
 * 15.12.2017
 */

class ConfigurerTest {
    private val configurer = Configurer()

    @Test
    fun parseEasy() {
        val str = arrayOf("create", "kotlin-gradle", "with", "JSON", "--outdir=tmp/project/", "called", "Test Project")
        val opts = configurer.parseOptions(str)
        assertTrue(opts.has("create"))
        assertTrue(opts.has("with"))
        assertTrue(opts.has("outdir"))
        assertTrue(opts.has("called"))
        assertEquals("kotlin-gradle", opts.valueOf("create"))
        assertEquals("JSON", opts.valueOf("with"))
        assertEquals("tmp/project/", opts.valueOf("outdir"))
        assertEquals("Test Project", opts.valueOf("called"))
    }

    @Test
    fun parseVariables() {
        val str = arrayOf("create", "kotlin-gradle", "-Vkotlin.version=1.2.0", "-Vauthor==Utikeev Stanislav", "called", "Test")
        val opts = configurer.parseOptions(str)
        val variables = configurer.getVariables(opts)
        assertTrue(opts.has("V"))
        assertEquals(listOf("kotlin.version=1.2.0", "author==Utikeev Stanislav"), opts.valuesOf("V"))
        assertEquals(mapOf("kotlin.version" to "1.2.0", "author" to "=Utikeev Stanislav"), variables)
    }

    @Test
    fun parseSeveralLayers() {
        val str = arrayOf("create", "kotlin-gradle", "with", "JSON,junit,your-mom-package", "called", "Test")
        val opts = configurer.parseOptions(str)
        assertTrue(opts.has("with"))
        assertEquals(listOf("JSON", "junit", "your-mom-package"), opts.valuesOf("with"))
    }

    @Test
    fun commandLineOptionsEasyTest() {
        val str = arrayOf("create", "kotlin-gradle", "with", "JSON,junit", "--outdir=tmp", "-Vkotlin.version=1.2.10", "called", "Test")
        val clOptions = ParsedOption.create(str) as CommandLineOptions
        assertEquals("kotlin-gradle", clOptions.template)
        assertEquals(listOf("JSON", "junit"), clOptions.layers)
        assertEquals("Test", clOptions.projectName)
        assertEquals(null, clOptions.config)
        assertEquals(Paths.get("tmp"), clOptions.outdir)
        assertEquals(mapOf("kotlin.version" to "1.2.10"), clOptions.variables)
    }

    @Test
    fun commandLineOptionsDefaultFolderTest() {
        val str = arrayOf("create", "kotlin-gradle", "called", "Test Project for IFMO")
        val clOptions = ParsedOption.create(str) as CommandLineOptions
        assertEquals(Paths.get("Test Project for IFMO"), clOptions.outdir)
    }
}