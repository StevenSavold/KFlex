# KFlex - Kotlin's Fast Lexical Analyzer
A flex implementation in Kotlin

### What is flex?
flex is a tool used to create scanners, which are programs that recognizes lexical patterns in text. 
Scanners are generally useful when attempting to abstract a stream of characters into some more meaningful 
representation.

flex is an open source project that can be found right [here on GitHub](https://github.com/westes/flex).

### What is KFlex?
KFLex is an implementation of flex in the Kotlin programming language.

### Why port flex to Kotlin?
flex is written in C and will generate C code to analyze the inputted text. 
This makes it great if you are writing a C or C++ program that you want to analyze text with.
However, it you want to then write a Kotlin program that analyzes text using flex, you will have to
interface the generated C code with Kotlin.

KFlex, on the other hand, will generate Kotlin code that will analyze the text making it easier to 
embed in other Kotlin programs.

### How do I use KFlex?
KFlex uses input files just like flex does. You simply write your input file with the syntax that is
described below and give that to KFlex at the command line. KFlex will then use your input file to 
generate a lexer file for you to use in your project.

### KFlex Input File Syntax
A KFlex file traditionally uses the extension ```.lex``` however KFlex will not directly check for this.
It will attempt to parse any file given to it and if the syntax is invalid it will abort.

##### Comments and Whitespace
You can write single line comments in KFlex using the '#' as the very
first character in the line. All characters after that will be ignored until 
after the next newline character.

    # This is a single line comment in a KFlex input file

*Note: KFlex currently does not support intra-line comments.*

*Note: KFlex will also ignore any empty lines, or lines filled only with whitespace.*

##### Defining a Token Type
To define a token begin by giving it a name. The name does not need to start at the beginning
of the line, however it must be the first non-whitespace token on the line. The second non-whitespace
token on the line should represent the regular expression defining the token type. These two tokens 
should be the only things on this line.

    # The first part is the token types name, the second part is the regular expression
    decimal          [0-9]+
    hexadecimal      0x[0-9A-Fa-f]+
    binary           0b[01]+
    identifier       [A-Za-z_][0-9A-Za-z_]+

##### Custom Code Upon Token Instantiation
Similar to flex, KFlex supports user defined code that will be run upon the creation of a token of 
the defined type. To define this code begin with a line only that starts with a percent sign (```%```) and 
is immediately followed by a opening curly brace (```{```). This will tell KFlex you wish to begin a block 
of Kotlin code. To end this block make a new line containing only a percent sign (```%```) that is 
immediately followed by a closing curly brace (```}```). The contents between these two lines should contain
valid Kotlin code. This code will be copy and pasted into the generated file and placed in a lambda that
is of the type ((Token) -> Unit). The inputted token will be the instance of the token being created. The
contents of the Token class will be explained below in its own segment.

    %{
        /*
         * This will be the token that comes into the lambda,
         * optionally you could omit this lime and use 'it'
         * just as in a regular kotlin lambda.
         */
            tokenInst: Token ->
        println("A token of type '${tokenInst.tokenType}' was created from the string")
        /* The print statement below is added to keep the above line under 80 chars */
        println("\"${tokenInst.matchedText}\"")
        println(tokenInst)
        println()
    %}
    
*Note: Due to complications with KotlinPoet the **kotlin code written in these blocks should not be more
then 80 characters wide.** If the code in these blocks is wider then that limit, the code is at risk of
not being placed into the lambda in the same way it was written. This could have the side effect of 
adding a newline into a string before the closing quote to limit the line width which would invalidate 
the kotlin syntax for example.* 

### The Token Class
The KFlex Token is defined with the following code:

    /** Token class will have the following:
     *     matchedText: String     - the string of text it matched with
     *     tokenType:   TokenType  - the type of token it was evaluated to be
     *     lineNumber:  Int        - the line number the token matched on
     *     wordIndex:   Int        - the index of the word on the line (0 for first word, 1 for second, etc.)
     *
     *     Other Initialization:
     *         Calls the block of code associated with the TokenType
     */
    data class Token(val matchedText: String, 
                     val tokenType:   TokenType, 
                     val lineNumber:  Int, 
                     val wordIndex:   Int)
    {
        init {
            /* Check if there is code associated with the type of token, if yes, invoke that function */
            tokenInitBlocks[tokenType.ordinal]?.invoke(this)
        }
    }

The Token class is simply a data class with four members:
    
1. a ```String``` called ```matchedText``` that holds the text that caused the token to be instantiated
2. a ```TokenType``` member called ```tokenType``` that is an enum filled with all the names of the types
defined in the lex file. Their ordinals are given in the order they were specified in. Beginning with ```0```.
3. a ```Int``` member called ```lineNumber``` which is the line the token was matched on.
4. a ```Int``` member called ```wordIndex``` which is the number that specifies how many tokens were
made on the line before this token.

The init block inside the class definition simply checks to see if a lambda was defined for the token type 
and if one exists it will call it passing in itself to the lambda.

### Using the Generated File
The generated file will provide you with a class called ```Lexer```. The constructor of which will
take a String as input. This string should be the path to the file you would like to lexicographically
analyse. You can then call the member function ```tokenize()``` which takes no input. This will lex the
whole file and returns a ```MutableList<Token>``` to the caller. *That's it! It's that easy!*

    val lexer = Lexer("myInputFile.txt")
    val tokens = lexer.lexAll()
    
You can also use the lexer in a suspending manner using the ```lexSome()``` function.
Using kotlin Sequences you can grab the first *N* tokens from the file to use however you like.

    val lexer = Lexer("myInputFile.txt")
    runBlocking {
        val tokens = lexer.lexSome().take(10).toList()
        tokens.forEach {
            println(it)
        }
    }
    
*NOTE: ```lexSome()``` is still under experimentation and is liable to change in the future*