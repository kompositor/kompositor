package top.sandwwraith.kompositor

import joptsimple.OptionException
import joptsimple.OptionParser
import joptsimple.OptionSet
import joptsimple.OptionSpec
import java.nio.file.Path
import java.nio.file.Paths

class Configurer {

    private val parser = OptionParser()
    val createSpec: OptionSpec<String>
    val withSpec: OptionSpec<String>
    val nameSpec: OptionSpec<String>
    val outdirSpec: OptionSpec<String>
    val configSpec: OptionSpec<String>
    val varSpec: OptionSpec<String>
    val layersSpec: OptionSpec<Void>
    val templatesSpec: OptionSpec<Void>
    val helpSpec: OptionSpec<Void>

    init {
        parser.recognizeAlternativeLongOptions(true)
        parser.allowsUnrecognizedOptions()
        createSpec = parser.accepts("create", "Template name. Can be used without double hyphen.")
                .withRequiredArg()

        withSpec = parser.accepts("with", "Layers separated with comma and no whitespaces. Can be used without double hyphen.")
                .withRequiredArg()
                .describedAs("layer1,layer2,layer3")
                .withValuesSeparatedBy(',')

        nameSpec = parser.accepts("called", "Name of the project. Can be used without double hyphen.")
                .withRequiredArg()

        outdirSpec = parser.accepts("outdir", "Output directory")
                .withRequiredArg()
                .defaultsTo("Project name")

        configSpec = parser.accepts("c", "Path to config")
                .withRequiredArg()

        varSpec = parser.accepts("V", "Optional variables starting with capital V. For example: -Vkotlin.version=1.2.10")
                .withRequiredArg()

        layersSpec = parser.accepts("layers", "List of available layers")

        templatesSpec = parser.accepts("templates", "List of available templates")

        helpSpec = parser.acceptsAll(listOf("h", "?", "help"), "Show help")
                .forHelp()
    }

    fun parseOptions(options: Array<String>): OptionSet {
        val mOptions = options.toMutableList()

        val createInd = mOptions.indexOf("create")
        if (createInd != -1) mOptions.add(createInd, "-W")
        val withInd = mOptions.indexOf("with")
        if (withInd != -1) mOptions.add(withInd, "-W")
        val calledInd = mOptions.indexOf("called")
        if (calledInd != -1) mOptions.add(calledInd, "-W")

        return parser.parse(*mOptions.toTypedArray())
    }

    fun getVariables(optionSet: OptionSet): Map<String, String> {
        return varSpec.values(optionSet).map {
            val lst = it.split("=")
            lst[0] to lst.drop(1).joinToString("=")
        }.toMap()
    }

    fun printHelp() {
        parser.printHelpOn(System.out)
    }
}

class MissedProjectOrTemplateNameException : OptionException(listOf(""))

sealed class ParsedOption {
    companion object {
        fun create(args: Array<String>) : ParsedOption {
            val configurer = Configurer()
            val opts = configurer.parseOptions(args)

            if (opts.has(configurer.helpSpec))
                return HelpOptions()

            if (opts.has(configurer.layersSpec))
                return LayersOptions()

            if (opts.has(configurer.templatesSpec))
                return TemplatesOptions()

            if (!(opts.has(configurer.nameSpec) && opts.has(configurer.createSpec)))
                throw MissedProjectOrTemplateNameException()

            val projectName = configurer.nameSpec.value(opts)
            val template = configurer.createSpec.value(opts)
            val layers = if (opts.has(configurer.withSpec)) configurer.withSpec.values(opts) else emptyList()
            val outdir =
                    Paths.get(
                            if (opts.has(configurer.outdirSpec))
                                configurer.outdirSpec.value(opts)
                            else
                                projectName)
            val variables = if (opts.has(configurer.varSpec)) configurer.getVariables(opts) else emptyMap()
            val config = if (opts.has(configurer.configSpec)) Paths.get(configurer.configSpec.value(opts)) else null

            return CommandLineOptions(projectName, template, layers, outdir, variables, config)
        }
    }
}

class HelpOptions : ParsedOption() {
    fun printHelp() {
        val configurer = Configurer()
        configurer.printHelp()
    }
}

class LayersOptions : ParsedOption()

class TemplatesOptions : ParsedOption()

data class CommandLineOptions(
        val projectName: String,
        val template: String,
        val layers: List<String>,
        val outdir: Path,
        val variables: Map<String, String>,
        val config: Path?) : ParsedOption()