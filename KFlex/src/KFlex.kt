import java.io.File

enum class TokenType(val value: Int)
data class TokenDefinition(val type: Pair<String, Int>, val rule: Regex)
data class Token(val matchedText: String, val tokenType: TokenType, val lineNumber: Int, val wordIndex: Int)

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

    val definitions = parseFile(lexFile)
    val outFile = File("Examples/src")

    if (definitions != null)
        createFileFromTemplate(definitions).writeTo(outFile)
    else
        println("Invalid input file given!")

    return
}

fun printUsage()
{
    println("KFlex <Lex File> [Options...]")
    println("    [Options]:")
    println("        -o <Output Directory> : Specify the location KFlex should output the generated code to.")
}

//fun getOptions(args: Array<String>) : MutableMap<String, String?>
//{
//    val output: MutableMap<String, String?> = mutableMapOf()
//
//    var i = 1
//    while (i < args.size)
//    {
//        when (args[i])
//        {
//            "-o" -> {
//                if ((i + 1) < args.size)
//                    output["-o"] = args[++i]
//                else
//                    println("No argument pass in with '-o' option")
//            }
//            else -> output[args[i]] = null
//        }
//        ++i
//    }
//
//    return output
//}

fun parseFile(lexFile: File) : MutableList<TokenDefinition>?
{
    if (!lexFile.exists())
        return null

    val tokenDefinitions: MutableList<TokenDefinition> = mutableListOf()

    var i = 0
    lexFile.forEachLine {
        //println(it)
        if ((it.isNotBlank()) && (it.first() != '#'))
        {
            // Split the input line into sections based on whitespace
            val sections = it.split("""\s""".toRegex()).toMutableList()

            // Remove the empty sections to leave only the Name of the Token and the Regex that defines it
            sections.removeIf { section -> section.isEmpty() }

            // Add the Token definition to the list
            tokenDefinitions.add(TokenDefinition(Pair(sections[0], i++), Regex(sections[1])))
        }
    }

    return tokenDefinitions
}