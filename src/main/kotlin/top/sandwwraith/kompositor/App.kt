package top.sandwwraith.kompositor

import com.github.kittinunf.fuel.core.FuelManager
import java.io.BufferedWriter
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

fun toFileConsumer(converter: (Reader, BufferedWriter) -> Unit): (Reader, Path) -> Unit = { reader, outPath ->
    outPath.parent.toFile().mkdirs()
    val writer = Files.newBufferedWriter(outPath) // todo: options
    converter(reader, writer).also { reader.close(); writer.close(); }
}

private fun Exception.prettyPrint() = buildString {
    var i = 0
    var e: Throwable? = this@prettyPrint
    while (e != null) {
        append("  ".repeat(i))
        append(e.message)
        append(System.lineSeparator())
        e = e.cause
        i++
    }
}

private fun reportAndBail(where: String, err: CompositeException): Nothing {
    println("Following errors occurred when $where:")
    err.errors.forEach {
        println(it.prettyPrint())
    }
    exitProcess(-1)
}

fun main(args: Array<String>) {
    // download layers
    val l = LayerDownloader(listOf("jackson-kotlin", "junit"), ::parseYamlLayer).apply { start() }
    // pulled layers and user settings, getLayers() await all parallel downloads
    val userSettings = mapOf("kotlin.version" to ("1.2.10"))
    val layers = l.getLayers().fold({ it }, { reportAndBail("pulling requested layers", it) })
    val data = createMapForMustache(userSettings, layers.values.toList(), true)
    // Mustache converter with data
    val converter = MustacheConverter().converter(data)
    // download and execute main template to 'tmp' folder in current dir
    val d = TemplateDownloader(toFileConsumer(converter), Paths.get("tmp")).apply { start() }
    //await all async operations
    d.await().fold({ }, { reportAndBail("instantiating a template", it) })
    // shutdown threadpool so we don't need to wait idle time for threads to die
    FuelManager.instance.executor.shutdown()
    println("OK")
}