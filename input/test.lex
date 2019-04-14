# test.lex
#
# A test input file for KFlex development
# also doubles as the format explanation
#

# a comment must start with the first character of that line being a '#'
# blank lines are also ignored

# Error should be the first token definition in the file
error           .*


# The first part of the line should contain the name of the token type
# (Note the name of the type should be compliant with kotlin's identifier syntax)

# The second part of the line should be a regular expression describing the token

# The two parts can be separated by any number of white spaces character as long
# as they stay on the same line

decimal         [0-9]+
hexadecimal     0x[0-9a-fA-F]+
octal           0o[0-7]+
binary          0b[01]+
id              [A-Za-z_][A-Za-z0-9_]*


# To add code to a token type to be executed upon valid token creation make a line
# that contains only a single open curly brace preceded with a '%' with a matching
# line that contains only a single closing curly brace preceded by a '%''. All lines
# between the braces will be made into a lambda that takes a Token and returns Unit.
# This lambda will be set to execute when a token with the last defined token type
# prior to the lambda is created and will be given the instance of the token that was
# created. If two blocks like this are defined without defining a new token type
# between them, the last defined lambda will overwrite the previous.

# (Note that all code in this block should be made to be valid kotlin code)

# (Also note: due to complications with KotlinPoet, all lines in the lambda must be
# kept under 80 characters wide to be copied accurately into the generated file)

# The last defined type was for 'id' and as such this lambda will be called when a
# token of that type is created
%{
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
%}