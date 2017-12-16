package top.sandwwraith.kompositor

import com.github.kittinunf.fuel.core.FuelManager
import joptsimple.OptionException
import java.io.BufferedWriter
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Path
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

fun create(args: CommandLineOptions) {
    val userSettings = composeConfig(args).let {
        if ("artifact" !in it) it + ("artifact" to args.projectName) else it
    }
    println("Started creating a project ${args.projectName}")

    val l = LayerDownloader(args.layers, ::parseYamlLayer).apply { start() }
    // pulled layers and user settings, getLayers() await all parallel downloads
    val layers = l.getLayers().fold({ it }, { reportAndBail("pulling requested layers", it) })
    val data = createMapForMustache(userSettings, layers.values.toList())

    val converter = MustacheConverter().converter(data)
    // download and execute main template
    val outDir = args.outdir
    val d = TemplateDownloader(toFileConsumer(converter), outDir).apply { start() }
    d.await().fold({ }, { reportAndBail("instantiating a template", it) })
    // shutdown threadpool so we don't need to wait idle time for threads to die
    FuelManager.instance.executor.shutdown()
    println("OK, content written to ${outDir.toAbsolutePath()}")
}

fun main(args: Array<String>) {
    @Suppress("NAME_SHADOWING")
    val args = try {
        CommandLineOptions.create(args)
    } catch (e: OptionException) {
        println("Error occurred during option parsing (${e.message}).${System.lineSeparator()}" +
                "If you need some help, use -h or -?")
        exitProcess(-2)
    }
    create(args)
}