package top.sandwwraith.kompositor

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

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
        val str = arrayOf("create", "kotlin-gradle", "-Vkotlin.version=1.2.0", "-Vauthor==Utikeev Stanislav", "called", "Template")
        val opts = configurer.parseOptions(str)
        val variables = configurer.getVariables(opts)
        assertTrue(opts.has("V"))
        assertEquals(listOf("kotlin.version=1.2.0", "author==Utikeev Stanislav"), opts.valuesOf("V"))
        assertEquals(mapOf("kotlin.version" to "1.2.0", "author" to "=Utikeev Stanislav"), variables)
    }

    @Test
    fun parseSeveralLayers() {
        val str = arrayOf("create", "kotlin-gradle", "with", "JSON,junit,your-mom-package")
        val opts = configurer.parseOptions(str)
        assertTrue(opts.has("with"))
        assertEquals(listOf("JSON", "junit", "your-mom-package"), opts.valuesOf("with"))
    }
}