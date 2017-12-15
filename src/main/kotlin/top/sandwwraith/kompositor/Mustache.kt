package top.sandwwraith.kompositor

import com.samskivert.mustache.Mustache
import java.io.BufferedWriter
import java.io.Reader

class MustacheConverter(private val compiler: Mustache.Compiler = Mustache.compiler().escapeHTML(false)) {
    fun converter(data: Map<String, Any>): (Reader, BufferedWriter) -> Unit = { reader, writer ->
        compiler.compile(reader).execute(data, writer).also { writer.flush() }
    }
}