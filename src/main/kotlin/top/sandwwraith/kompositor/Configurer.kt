package top.sandwwraith.kompositor

import joptsimple.OptionParser
import joptsimple.OptionSet
import joptsimple.OptionSpec

/**
 * Utikeev Stanislav
 * utikeev@gmail.com
 * 15.12.2017
 */
class Configurer {

    private val parser = OptionParser()
    private val createSpec: OptionSpec<String>
    private val withSpec: OptionSpec<String>
    private val nameSpec: OptionSpec<String>
    private val outdirSpec: OptionSpec<String>
    private val configSpec: OptionSpec<String>
    private val varSpec: OptionSpec<String>

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
        val outdir: String,
        val variables: Map<String, String>,
        val config: String?)