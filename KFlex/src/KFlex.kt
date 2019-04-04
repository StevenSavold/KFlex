import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import java.io.File

fun main(args: Array<String>)
{
    if (args.isEmpty())
    {
        printUsage()
        return
    }

    val lexFile = File(args[0])
    if (!lexFile.exists())
    {
        println("Could not open file '${args[0]}'")
        return
    }

    if (args.size > 1)
    {
        val options = getOptions(args)

    }

    val mainFun = FunSpec.builder("main")
        .addCode("""
        |var total = 0
        |for (i in 0 until 10) {
        |    total += i
        |}
        |println(total)
        |""".trimMargin())
        .build()

    //println(mainFun)

    val mainFile = FileSpec.builder("KFlex", "Output")
        .addFunction(mainFun)
        .build()

    //println(mainFile)
    mainFile.writeTo(File("output"))

    return

}

fun printUsage()
{
    println("KFlex <Lex File> [Options...]")
    println("    [Options]:")
    println("        -o <Output Directory> : Specify the location KFlex should output the generated code to.")
}

fun getOptions(args: Array<String>) : MutableMap<String, String?>
{
    val output: MutableMap<String, String?> = mutableMapOf()

    var i = 1
    while (i < args.size)
    {
        when (args[i])
        {
            "-o" -> {
                if ((i + 1) < args.size)
                    output["-o"] = args[++i]
                else
                    println("No argument pass in with '-o' option")
            }
            else -> output[args[i]] = null
        }
        ++i
    }

    return output
}

fun parseFile(filepath: String) : MutableList<TokenDefinition>?
{
    val lexFile = File(filepath)
    if (!lexFile.exists())
    {
        return null
    }

    val tokenDefinitions: MutableList<TokenDefinition> = mutableListOf()

    lexFile.forEachLine {
        println(it)
        if ((it.isNotBlank()) && (it.first() != '#'))
        {
            // Split the input line into sections based on whitespace
            val sections = it.split("""\s""".toRegex()).toMutableList()

            // Remove the empty sections to leave only the Name of the Token and the Regex that defines it
            sections.removeIf { section -> section.isEmpty() }

            // Add the Token definition to the list
            tokenDefinitions.add(TokenDefinition(TokenType.valueOf(sections[0]), Regex(sections[1])))
        }
    }

    return tokenDefinitions
}