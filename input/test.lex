# test.lex
#
# A test input file for KFlex development
# also doubles as the format explanation
#

# a comment must start with the first character of that line being a '#'
# blank lines are also ignored

# Error should be the first token definition in the file
error           .*

# The first part of the line should contain the name of the token
# The second part of the line should be a regular expression describing the token
# The two parts can be separated by any number of spaces or tabs characters
decimal         [0-9]+
hexadecimal     0x[0-9a-fA-F]+
octal           0o[0-7]+
binary          0b[01]+
id              [A-Za-z_][A-Za-z0-9_]*