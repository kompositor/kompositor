package top.sandwwraith.kompositor

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.StringReader

class LayersTest {

    private val simple = "gradle-dependencies: junit:junit:4.12"

    @Test
    fun parseSimpleLayer() {

        val parsed = parseYamlLayer(StringReader(simple))
        assertEquals(mapOf("gradle-dependencies" to listOf("junit:junit:4.12")), parsed)
    }

    private val complex = """
        |gradle-ext-block: "ext.jackson-version = 2.9.3"
        |
        |gradle-dependencies:
        | - 'testCompile group: ''com.fasterxml.jackson.core'', name: ''jackson-core'', version: jacksonVersion'
        | - 'testCompile group: ''com.fasterxml.jackson.core'', name: ''jackson-databind'', version: jacksonVersion'
        | - 'testCompile group: ''com.fasterxml.jackson.module'', name: ''jackson-module-kotlin'', version: jacksonVersion'
        """.trimMargin()

    @Test
    fun parseComplexLayer() {

        val parsed = parseYamlLayer(StringReader(complex))
        assertEquals(
                mapOf(
                        "gradle-ext-block" to listOf("ext.jackson-version = 2.9.3"),
                        "gradle-dependencies" to listOf(
                                "testCompile group: 'com.fasterxml.jackson.core', name: 'jackson-core', version: jacksonVersion",
                                "testCompile group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: jacksonVersion",
                                "testCompile group: 'com.fasterxml.jackson.module', name: 'jackson-module-kotlin', version: jacksonVersion"
                        )
                ), parsed)
    }

    @Test
    fun mergeLayers() {
        val l1 = parseYamlLayer(StringReader(simple))
        val l2 = parseYamlLayer(StringReader(complex))
        val merged = mergeLayers(listOf(l1, l2))
        assertEquals(
                mapOf(
                        "gradle-ext-block" to listOf("ext.jackson-version = 2.9.3"),
                        "gradle-dependencies" to listOf(
                                "junit:junit:4.12",
                                "testCompile group: 'com.fasterxml.jackson.core', name: 'jackson-core', version: jacksonVersion",
                                "testCompile group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: jacksonVersion",
                                "testCompile group: 'com.fasterxml.jackson.module', name: 'jackson-module-kotlin', version: jacksonVersion"
                        )
                ), merged)
    }
}