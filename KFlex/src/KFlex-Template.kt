import java.io.File

// This is the type definition for the token function
typealias TokenFn = (Token) -> Unit

// *** This should be generated from user input *** //
enum class TokenType(val value: Int) {
    // *** AUTO GENERATED CONTENT HERE *** //
    error(0),
    decimal(1),
    hexadecimal(2),
    octal(3),
    binary(4),
    id(5)
}

// *** This should be generated from user input *** //
private val tokenInitBlocks : Array<TokenFn?> = arrayOf(
    // *** AUTO GENERATED CONTENT HERE *** //
    {
        println("error!")
    },
    null,
    null,
    null,
    null,
    null
)

private val tokenDefinitions : Array<TokenDefinition> = arrayOf(
                // *** Template Entry *** //
    /* TokenDefinition(TokenType.values()[0], Regex("")) */
    TokenDefinition(TokenType.values()[0], Regex(".*")),
    TokenDefinition(TokenType.values()[1], Regex("[0-9]+")),
    TokenDefinition(TokenType.values()[2], Regex("0x[0-9a-fA-F]+")),
    TokenDefinition(TokenType.values()[3], Regex("0o[0-7]+")),
    TokenDefinition(TokenType.values()[4], Regex("0b[01]+")),
    TokenDefinition(TokenType.values()[5], Regex("[A-Za-z_][A-Za-z0-9_]*"))
)

/**
 *     A class that defines the definition of a Token.
 *     The is what the lex file is parsed into
 **/
data class TokenDefinition(val tokenName: TokenType, val tokenRegex: Regex)

/** Token class will have the following:
 *     matchedText: String     - the string of text it matched with
 *     tokenType:   TokenType  - the type of token it was evaluated to be
 *     lineNumber:  Int        - the line number the token matched on
 *     wordIndex:   Int        - the index of the word on the line (0 for first word, 1 for second, etc.)
 *
 *    Other Initialization:
 *         Calls the block of code associated with the TokenType
 **/
data class Token(val matchedText: String, val tokenType: TokenType, val lineNumber: Int, val wordIndex: Int)
{
    init {
        /* Check if there is code associated with the type of token, if yes, invoke that function */
        tokenInitBlocks[tokenType.value]?.invoke(this)
    }
}

class Lexer(inputFilepath: String) {

    private val lines: MutableList<String> =  mutableListOf()

    // Copy all the lines from the given file into the class
    init {
        val file = File(inputFilepath)
        if (file.exists())
        {
            file.forEachLine {
                lines.add(it)
            }
        }
    }

    fun tokenize() : MutableList<Token>
    {
        val output = mutableListOf<Token>()
        lines.forEachIndexed { lineNumber: Int, line: String ->

            val chunks = line.split("""\s""".toRegex()).toMutableList()
            chunks.removeIf { chunk -> chunk.isEmpty() }

            var tokenType: TokenType?
            chunks.forEachIndexed { wordIndex: Int, inputToken: String ->
                tokenType = null
                tokenDefinitions.forEach { tokenDef ->
                    if (tokenDef.tokenRegex.matches(inputToken))
                        tokenType = tokenDef.tokenName
                }

                if (tokenType != null)
                {
                    output.add(Token(inputToken, tokenType!!, lineNumber, wordIndex))
                }
                else
                {
                    /* No valid token was found! (Did the user not supply an error type??) */

                }

            }
        }

        return output
    }

}