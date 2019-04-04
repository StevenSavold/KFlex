import com.squareup.kotlinpoet.*
import java.io.File

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

    val mainFile = FileSpec.builder("KFlex", "Output")
        .addFunction(mainFun)
        .build()

    //println(mainFile)
    mainFile.writeTo(File("output"))
}