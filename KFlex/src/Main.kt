import com.squareup.kotlinpoet.*
import java.io.File
import kotlin.text.Regex

fun main(args: Array<String>)
{
    if (args.isEmpty())
    {
        printUsage()
        return
    }
    val lexFilePath = args[0]
    val lexFile = File(lexFilePath)
    var userDefinedRegExs: MutableList<Regex> = mutableListOf()
    var sectionsCopy: MutableList<String> = mutableListOf()


    lexFile.forEachLine {
        var sections = it.split(" ").toMutableList()
            sections.removeIf { it.isEmpty() }

        userDefinedRegExs.add(Regex(sections[1]))

        sectionsCopy.add(sections[0])

    }

//    sectionsCopy.forEachIndexed{
//            idx, expr ->
//
//        println(idx.toString() + " " + expr)
//    }

    while (true)
    {
        var userInput = readLine()
        var strProper = userInput.orEmpty()

        userDefinedRegExs.forEachIndexed {
            idx, expr ->

            //println(idx.toString() + " " + expr)
            if (expr.matches(strProper))
            {
                println("input matches ${sectionsCopy[idx]}")
            }

        }
    }


    return

}

fun printUsage()
{
    println("KFlex <Lex File> [Options...]")
}

fun CreateExampleFile()
{
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

    var mainFile = FileSpec.builder("KFlex", "Output")
        .addFunction(mainFun)
        .build()

    //println(mainFile)
    mainFile.writeTo(File("output"))
}

