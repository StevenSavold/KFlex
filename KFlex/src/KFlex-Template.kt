/*

    **** TEMPLATE FILE FOR KFLEX ****


import java.io.File

// This is the type definition for the token function
typealias TokenFn = (Token) -> Unit

// *** This should be generated from user input *** //
enum class TokenType(val value: Int) {
    // *** AUTO GENERATED CONTENT HERE *** //
}

// *** This should be generated from user input *** //
private val tokenInitBlocks : Array<TokenFn?> = arrayOf(
    // *** AUTO GENERATED CONTENT HERE *** //
)

private val tokenDefinitions : Array<TokenDefinition> = arrayOf(
                // *** Template Entry *** //
)

/**
 *     A class that defines the definition of a Token.
 *     The is what the lex file is parsed into
 **/
data class TokenDefinition(val type: TokenType, val rule: Regex)

/** Token class will have the following:
 *     matchedText: String     - the string of text it matched with
 *     tokenType:   TokenType  - the type of token it was evaluated to be
 *     lineNumber:  Int        - the line number the token matched on
 *     wordIndex:   Int        - the index of the word on the line (0 for first word, 1 for second, etc.)
 *
 *     Other Initialization:
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
                    if (tokenDef.rule.matches(inputToken))
                        tokenType = tokenDef.type
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

*/
