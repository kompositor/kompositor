package top.sandwwraith.kompositor

import org.yaml.snakeyaml.Yaml
import java.io.Reader

typealias Layer = Map<String, List<String>>

fun parseYamlLayer(input: Reader): Layer {
    val map = Yaml().loadAs(input, HashMap::class.java)
    fun parseValue(v: Any): List<String> = when (v) {
        is List<*> -> v.map { it.toString() }
        is String -> listOf(v)
        is Int -> listOf(v.toString())
        else -> throw IllegalArgumentException("Nested yaml is not supported; value must be either String or List<String>, found ${v::class}")
    }
    return map.asSequence().map { (k, v) -> k.toString() to parseValue(v) }.toMap()
}

fun mergeLayers(layers: List<Layer>): Layer {
    val ans: MutableMap<String, List<String>> = hashMapOf()
    layers.forEach { layer ->
        layer.forEach { k, v ->
            ans.merge(k, v, { l1, l2 -> (l1 + l2.map { it.trim() }).distinct() })
        }
    }
    return ans
}