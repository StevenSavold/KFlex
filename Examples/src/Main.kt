import kotlinx.coroutines
import KFlex.*


fun main(args: Array<String>)
{
    val lexer = Lexer(args[1])
    val tokens = lexer.tokenize()
    //val token: Token

    //GlobalScope.launch {
    //    token = lexer.tokenizeOne()
    //}

    println(token)

    return
}