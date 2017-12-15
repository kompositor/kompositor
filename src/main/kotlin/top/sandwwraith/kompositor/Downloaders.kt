package top.sandwwraith.kompositor

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.jacksonDeserializerOf
import java.io.Reader
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Phaser

private const val BASE_API_PATH = "https://api.github.com/repos/"

open class AbstractGitHubContentDownloader(repoPath: String) {
    val contentUrl = "$BASE_API_PATH$repoPath/contents"

    protected lateinit var phaser: Phaser

    protected fun <T : Any> load(url: String, deserializer: ResponseDeserializable<T>, handler: (T) -> Unit) {
        phaser.register()
        url.httpGet().responseObject(deserializer) { _, _, result ->
            result.fold(handler, ::errorCollector)
            phaser.arriveAndDeregister()
        }
    }

    open fun start() {
        phaser = Phaser(1)
    }

    open fun await() {
        phaser.arriveAndAwaitAdvance()
    }

    protected fun errorCollector(e: FuelError) {
        System.err.println(e.exception) //todo
    }

}

internal fun makeAbsoluteSavePath(basePath: Path, gitHubPath: Path): Path {
    return basePath.resolve(gitHubPath.subpath(1, gitHubPath.nameCount))
}

class TemplateDownloader(
        val networkFileConsumer: (Reader, Path) -> Unit,
        val rootOutputPath: Path,
        val templateName: String = "gradle-kotlin",
        repoPath: String = "kompositor/templates"
) : AbstractGitHubContentDownloader(repoPath) {

    fun loadFile(filePath: String, outPath: Path) = load(filePath, ResponseConsumer(outPath)) { /* OK */ }

    fun loadFolder(folderPath: String) = load(folderPath, jacksonDeserializerOf(), ::traverseFolder)

    private fun traverseFolder(list: List<GitHubContentElement>) {
        list.forEach {
            when (it.type) {
                "file" -> loadFile(it.download_url!!, makeAbsoluteSavePath(rootOutputPath, Paths.get(it.path)))
                "dir" -> loadFolder(it.url)
                else -> throw IllegalStateException("Unknown type of element: ${it.type}")
            }
        }
    }

    override fun start() {
        super.start()
        loadFolder("$contentUrl/$templateName")
    }

    private inner class ResponseConsumer(val outPath: Path) : ResponseDeserializable<Unit> {
        override fun deserialize(reader: Reader): Unit? {
            return networkFileConsumer(reader, outPath)
        }
    }

    data class GitHubContentElement(val type: String, val url: String, val path: String, val download_url: String?)
}

class LayerDownloader(
        val layerNames: List<String>,
        val layerLoader: (Reader) -> Layer,
        repoPath: String = "kompositor/layers"
) : AbstractGitHubContentDownloader(repoPath) {
    private val resultMap: MutableMap<String, Layer> = ConcurrentHashMap()

    private val rawUrl = "https://raw.githubusercontent.com/$repoPath/master"

    private fun loadLayer(layerName: String) = load("$rawUrl/$layerName", LayerReader(layerName), {})

    override fun start() {
        super.start()
        layerNames.forEach(::loadLayer)
    }

    fun getLayers(): Map<String, Layer> = await().run { resultMap }

    private inner class LayerReader(val layerName: String) : ResponseDeserializable<Unit> {
        override fun deserialize(reader: Reader): Unit? {
            resultMap[layerName] = layerLoader(reader)
            return Unit
        }
    }
}