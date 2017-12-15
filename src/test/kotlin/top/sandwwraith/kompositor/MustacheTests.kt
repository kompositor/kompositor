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

    @Test
    fun complexProcessingTest() {
        val layers = mergeLayers(listOf(
                parseYamlLayer(StringReader(junitLayer)),
                parseYamlLayer(StringReader(jacksonLayer))
        ))
        val userSettings = mapOf("kotlin.version" to "1.2.10")
        val output = StringWriter()
        MustacheConverter().converter(layers + userSettings).invoke(StringReader(gradleKotlinTemplate), output.buffered())
        output.close()
        assertEquals(expectedInComplex, output.toString())
    }

    val expectedInComplex = """buildscript {
    ext.kotlin_version = '1.2.10'
    ext.jackson-version = 2.9.3

    repositories {
        jcenter()
    }

    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:${"$"}kotlin_version"
    }
}

apply plugin: 'kotlin'

dependencies {
        compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${"$"}kotlin_version"

        testCompile junit:junit:4.12
        compile group: 'com.fasterxml.jackson.core', name: 'jackson-core', version: jacksonVersion
        compile group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: jacksonVersion
        compile group: 'com.fasterxml.jackson.module', name: 'jackson-module-kotlin', version: jacksonVersion
}
"""
}