# KFlex - Kotlins Fast Lexical Analyzer
A flex implementation in Kotlin

### What is flex?
flex is a tool used to create scanners, which are programs that recognizes lexical patterns in text. 
Scanners are generally useful when attempting to abstract a stream of characters into some more meaningful 
representation.

flex is an open source project that can be found right [here on GitHub](https://github.com/westes/flex).

### What is KFlex?
KFLex is an implementation of flex in the Kotlin programming langugage.

### Why port flex to Kotlin?
flex is written in C and will generate C code to analyze the inputed text. 
This makes it great if you are writing a C or C++ program that you want to analyze text with.
However, it you want to then write a Kotlin program that analyzes text using flex, you will have to
interface the generated C code with Kotlin.

KFlex, on the other hand, will generate Kotlin code that will analyze the text making it easier to 
embed in other Kotlin programs.

### How do I use KFlex?
KFlex uses input files just like flex does. 
The syntax of which will be described here in this README file at a later time...
