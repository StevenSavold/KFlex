import java.io.File
import kotlin.text.Regex

fun main(args: Array<String>)
{

    val lexer = Lexer(args[1])
    val tokens = lexer.tokenize()
    println(tokens)

    return
}

/*
fun immediateMode(tokenDefinitions: MutableList<TokenDefinition>) : Unit
{
    var shouldQuit = false
    while (!shouldQuit)
    {
        print("> ")
        val userInput = readLine().also { if (it.isNullOrBlank()) shouldQuit = true }?.split(" ")?.toMutableList()
        userInput?.removeIf { it.isEmpty() }

        var tokenType: TokenType?
        userInput?.forEach { inputToken ->
            tokenType = null
            tokenDefinitions.forEach { tokenDef ->
                if (tokenDef.tokenRegex.matches(inputToken))
                    tokenType = tokenDef.tokenName
            }

            if (tokenType != null)
                println("Token Matched pattern for '$tokenType'")
            else
                println("Token did not match a known pattern...")
        }
    }

    return
}
*/

