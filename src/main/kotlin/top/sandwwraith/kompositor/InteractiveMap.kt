package top.sandwwraith.kompositor

import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

internal const val LAYER_PREFIX = "layer:"

class InteractiveMap(val backing: Map<String, Any>, val errorIfNotSpecified: Boolean = false) : Map<String, Any> by backing {
    private val recorded: MutableMap<String, Any> = ConcurrentHashMap()

    override fun containsKey(key: String): Boolean {
        if (recorded.containsKey(key) || backing.containsKey(key)) return true
        if (key.startsWith(LAYER_PREFIX)) return false
        recorded[key] = if (!errorIfNotSpecified) askUserForKey(key)
        else throw IllegalStateException("Key $key is missing in non-interactive mode")
        return true
    }

    override fun get(key: String): Any? {
        return recorded[key] ?: backing[key]
    }
}

fun createMapForMustache(userSettings: Map<String, String>, layers: List<Layer>, errorIfNotSpecified: Boolean = false): Map<String, Any> {
    val transformedLayers = mergeLayers(layers).mapKeys { (k, _) -> "$LAYER_PREFIX$k" }
    return InteractiveMap(transformedLayers + userSettings, errorIfNotSpecified)
}

private val readLock = Object()
fun askUserForKey(key: String): String {
    synchronized(readLock) {
        print("Enter value for $key: ")
        val s = readLine() ?: throw IOException("Unexpected EOF")
        return s.trim()
    }
}