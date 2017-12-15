package top.sandwwraith.kompositor

import joptsimple.OptionParser
import joptsimple.OptionSet
import joptsimple.OptionSpec
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Utikeev Stanislav
 * utikeev@gmail.com
 * 15.12.2017
 */
class Configurer {

    private val parser = OptionParser()
    val createSpec: OptionSpec<String>
    val withSpec: OptionSpec<String>
    val nameSpec: OptionSpec<String>
    val outdirSpec: OptionSpec<String>
    val configSpec: OptionSpec<String>
    val varSpec: OptionSpec<String>

    init {
        parser.recognizeAlternativeLongOptions(true)
        parser.allowsUnrecognizedOptions()
        createSpec = parser.accepts("create").withRequiredArg().required()
        withSpec = parser.accepts("with").withRequiredArg().withValuesSeparatedBy(',')
        nameSpec = parser.accepts("called").withRequiredArg().required()
        outdirSpec = parser.accepts("outdir").withRequiredArg()
        configSpec = parser.accepts("c").withRequiredArg()
        varSpec = parser.accepts("V").withRequiredArg()
    }

    fun parseOptions(options: Array<String>): OptionSet {
        val mOptions = options.toMutableList()

        mOptions.add(mOptions.indexOf("create"), "-W")
        val withInd = mOptions.indexOf("with")
        if (withInd != -1) mOptions.add(withInd, "-W")
        val calledInd = mOptions.indexOf("called")
        if (calledInd != -1) mOptions.add(calledInd, "-W")

        return parser.parse("-W", *mOptions.toTypedArray())
    }

    fun getVariables(optionSet: OptionSet): Map<String, String> {
        return varSpec.values(optionSet).map {
            val lst = it.split("=")
            lst[0] to lst.drop(1).joinToString("=")
        }.toMap()
    }
}

data class CommandLineOptions(
        val projectName: String,
        val template: String,
        val layers: List<String>,
        val outdir: Path,
        val variables: Map<String, String>,
        val config: Path?) {

    companion object {
        fun create(args: Array<String>) : CommandLineOptions {
            val configurer = Configurer()
            val opts = configurer.parseOptions(args)
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