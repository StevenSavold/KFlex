package KFlex

import java.io.File
import kotlin.Array
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.collections.MutableList
import kotlin.text.Regex

typealias TokenFn = (Token) -> Unit

enum class TokenType(value: Int) {
    error(0),

    decimal(1),

    hexadecimal(2),

    octal(3),

    binary(4),

    id(5)
}

private val tokenInitBlocks: Array<((Token) -> Unit)?> = arrayOf(
        null,
        null,
        null,
        null,
        null,
        {
            /*
             * This will be the token that comes into the lambda,
             * optionally you could omit this lime and use 'it'
             * just as in a regular kotlin lambda.
             */
            tokenInst: Token ->
            println("A token of type '${tokenInst.tokenType}' was created from the string")
            println("\"${tokenInst.matchedText}\"")
            println(tokenInst)
            println()
        }
        )

private val tokenDefinitions: Array<TokenDefinition> =
        arrayOf(TokenDefinition(TokenType.values()[0], Regex(".*")),
        TokenDefinition(TokenType.values()[1], Regex("[0-9]+")),
        TokenDefinition(TokenType.values()[2], Regex("0x[0-9a-fA-F]+")),
        TokenDefinition(TokenType.values()[3], Regex("0o[0-7]+")),
        TokenDefinition(TokenType.values()[4], Regex("0b[01]+")),
        TokenDefinition(TokenType.values()[5], Regex("[A-Za-z_][A-Za-z0-9_]*")))

/**
 *     A class that defines the definition of a Token.
 *     The is what the lex file is parsed into
 */
data class TokenDefinition(val type: TokenType, val rule: Regex)

/**
 * Token class will have the following:
 *     matchedText: String     - the string of text it matched with
 *     tokenType:   TokenType  - the type of token it was evaluated to be
 *     lineNumber:  Int        - the line number the token matched on
 *     wordIndex:   Int        - the index of the word on the line (0 for first word, 1 for second,
        etc.)
 *
 *     Other Initialization:
 *         Calls the block of code associated with the TokenType
 */
data class Token(
    val matchedText: String,
    val tokenType: TokenType,
    val lineNumber: Int,
    val wordIndex: Int
) {
    init {
        tokenInitBlocks[tokenType.ordinal]?.invoke(this)
    }
}

class Lexer(inputFilepath: String) {
    private val lines: MutableList<String> = mutableListOf()

    init {
        val file = File(inputFilepath)
        if (file.exists())
            file.forEachLine { lines.add(it) }
    }

    fun tokenize(): MutableList<Token> {
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
                    output.add(Token(inputToken, tokenType!!, lineNumber, wordIndex))
                else {
                    /* No valid token was found! (Did the user not supply an error type??) */
                }
            }
        }

        return output
    }
}
