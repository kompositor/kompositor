package top.sandwwraith.kompositor

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.StringReader

class LayersTest {
    @Test
    fun parseSimpleLayer() {
        val parsed = parseYamlLayer(StringReader(junitLayer))
        assertEquals(mapOf("gradle-dependencies" to listOf("testCompile junit:junit:4.12")), parsed)
    }

    @Test
    fun parseComplexLayer() {
        val parsed = parseYamlLayer(StringReader(jacksonLayer))
        assertEquals(
                mapOf(
                        "gradle-ext-block" to listOf("ext.jackson-version = 2.9.3"),
                        "gradle-dependencies" to listOf(
                                "compile group: 'com.fasterxml.jackson.core', name: 'jackson-core', version: jacksonVersion",
                                "compile group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: jacksonVersion",
                                "compile group: 'com.fasterxml.jackson.module', name: 'jackson-module-kotlin', version: jacksonVersion"
                        )
                ), parsed)
    }

    @Test
    fun mergeLayers() {
        val l1 = parseYamlLayer(StringReader(junitLayer))
        val l2 = parseYamlLayer(StringReader(jacksonLayer))
        val merged = mergeLayers(listOf(l1, l2))
        assertEquals(
                mapOf(
                        "gradle-ext-block" to listOf("ext.jackson-version = 2.9.3"),
                        "gradle-dependencies" to listOf(
                                "testCompile junit:junit:4.12",
                                "compile group: 'com.fasterxml.jackson.core', name: 'jackson-core', version: jacksonVersion",
                                "compile group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: jacksonVersion",
                                "compile group: 'com.fasterxml.jackson.module', name: 'jackson-module-kotlin', version: jacksonVersion"
                        )
                ), merged)
    }
}