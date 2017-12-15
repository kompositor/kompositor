package top.sandwwraith.kompositor

import com.github.kittinunf.fuel.core.FuelManager
import java.io.BufferedWriter
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun toFileConsumer(converter: (Reader, BufferedWriter) -> Unit): (Reader, Path) -> Unit = { reader, outPath ->
    outPath.parent.toFile().mkdirs()
    val writer = Files.newBufferedWriter(outPath) // todo: options
    converter(reader, writer).also { reader.close(); writer.close(); }
}

fun main(args: Array<String>) {
    // download layers
    val l = LayerDownloader(listOf("jackson-kotlin", "junit"), ::parseYamlLayer).apply { start() }
    // pulled layers and user settings, getLayers() await all parallel downloads
    val dependencies = mergeLayers(l.getLayers().values.toList())
    val userSettings = mapOf("kotlin.version" to ("1.2.10"))
    // Mustache converter with data
    val converter = MustacheConverter().converter(dependencies + userSettings)
    // download and execute main template to 'tmp' folder in current dir
    val d = TemplateDownloader(toFileConsumer(converter), Paths.get("tmp")).apply { start() }
    //await all async operations
    d.await()
    // shutdown threadpool so we don't need to wait idle time for threads to die
    FuelManager.instance.executor.shutdown()
    println("OK")
}