package top.sandwwraith.kompositor

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.failure
import java.io.Reader
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Phaser

private const val BASE_API_PATH = "https://api.github.com/repos/"

internal fun makeAbsoluteSavePath(basePath: Path, gitHubPath: Path): Path {
    return basePath.resolve(gitHubPath.subpath(1, gitHubPath.nameCount))
}

class TemplateDownloader(
        val networkFileConsumer: (Reader, Path) -> Unit,
        val rootOutputPath: Path,
        templateName: String = "gradle-kotlin",
        repoPath: String = "kompositor/templates"
) {
    val rootUrl = "${BASE_API_PATH}$repoPath/contents/$templateName"

    private lateinit var phaser: Phaser

    fun loadFile(filePath: String, outPath: Path) {
        phaser.register()
        filePath.httpGet().responseObject(ResponseConsumer(outPath)) { _, _, result ->
            result.failure(::errorCollector)
            phaser.arriveAndDeregister()
        }
    }

    fun loadFolder(folderPath: String) {
        phaser.register()
        folderPath.httpGet().responseObject<List<GitHubContentElement>> { _, _, result ->
            result.fold(::traverseFolder, ::errorCollector)
            phaser.arriveAndDeregister()
        }
    }

    private fun traverseFolder(list: List<GitHubContentElement>) {
        list.forEach {
            when (it.type) {
                "file" -> loadFile(it.download_url!!, makeAbsoluteSavePath(rootOutputPath, Paths.get(it.path)))
                "dir" -> loadFolder(it.url)
                else -> throw IllegalStateException("Unknown type of element: ${it.type}")
            }
        }
    }

    fun start() {
        phaser = Phaser(1)
        loadFolder(rootUrl)
    }

    private fun errorCollector(e: FuelError) {
        System.err.println(e.exception) //todo
    }

    private inner class ResponseConsumer(val outPath: Path) : ResponseDeserializable<Unit> {
        override fun deserialize(reader: Reader): Unit? {
            return networkFileConsumer(reader, outPath)
        }
    }

    data class GitHubContentElement(val type: String, val url: String, val path: String, val download_url: String?)

    fun stop() {
        phaser.arriveAndAwaitAdvance()
        FuelManager.instance.executor.shutdown()
    }

}