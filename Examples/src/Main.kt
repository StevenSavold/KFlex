fun main(args: Array<String>)
{
    val lexer = Lexer(args[1])
    val tokens = lexer.tokenize()
    println(tokens)

    return
}