package top.sandwwraith.kompositor

import org.yaml.snakeyaml.Yaml
import top.sandwwraith.kompositor.LoadSettings.fromFile
import top.sandwwraith.kompositor.LoadSettings.fromHomeDir
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

typealias UserSettings = Map<String, String>

internal object LoadSettings {
    fun fromYaml(input: Reader): UserSettings {
        val map = Yaml().loadAs(input, HashMap::class.java)
        return map.asSequence().map { (k, v) -> k.toString() to v.toString() }.toMap()
    }

    fun fromFile(file: Path) = fromYaml(Files.newBufferedReader(file))

    fun fromHomeDir(): UserSettings? {
        val homeDir = Paths.get(System.getProperty("user.home"))
        val configFile = homeDir.resolve(".kompositor.yml")
        return if (Files.notExists(configFile)) null
        else fromFile(configFile)
    }
}

fun composeConfig(configPath: Path?, userVars: UserSettings, useHome: Boolean = true): UserSettings {
    val fromHome = if (useHome) fromHomeDir() ?: emptyMap() else emptyMap()
    val fromConfigFile = configPath?.let { fromFile(it) } ?: emptyMap()
    // map added last have higher priority
    return fromHome + fromConfigFile + userVars
}

fun composeConfig(cliArgs: CommandLineOptions) = composeConfig(cliArgs.config, cliArgs.variables)