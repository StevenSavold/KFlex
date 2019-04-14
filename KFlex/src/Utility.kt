import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

fun createFileFromTemplate(definitions: MutableList<TokenDefinition>, outputFileName: String? = null) : FileSpec
{
    /* TokenFn TypeAlias Definition */
    val tokenFnTypeAlias = TypeAliasSpec.builder(
        "TokenFn",
        LambdaTypeName.get(
            parameters = *arrayOf(
                TypeVariableName("Token")
            ),
            returnType = Unit::class.asClassName()
        )
    ).build()

    /* TokenType Enum Definition */
    val tokenTypeEnumBuilder = TypeSpec.enumBuilder("TokenType")
        .primaryConstructor(
            FunSpec.constructorBuilder()
            .addParameter("value", Int::class)
            .build()
        )
    /* For Each Defined Token Generate a TokeType Entry */
    definitions.forEach {
        tokenTypeEnumBuilder.addEnumConstant(
            it.type.first, TypeSpec.anonymousClassBuilder()
            .addSuperclassConstructorParameter("%L", it.type.second)
            .build()
        )
    }
    /* Finished Constructing the TokenType Enum */
    val tokenTypeEnum = tokenTypeEnumBuilder.build()


    /* Feature Coming Soon! */
    val tokenInitBlockPropertyCodeBuilder = CodeBlock.builder()
        .add("arrayOf(\n")

    /* Generate array contents here */
    definitions.forEachIndexed { idx: Int, def: TokenDefinition ->
        if (def.block.isEmpty())
        {
            tokenInitBlockPropertyCodeBuilder.add("null,\n")
        }
        else
        {
            tokenInitBlockPropertyCodeBuilder.add("{\n")
            def.block.forEach {
                tokenInitBlockPropertyCodeBuilder.add("%L\n", it)
            }
            tokenInitBlockPropertyCodeBuilder.add("}")
            // add a comma + newline on all lines except the last one
            if (idx != definitions.size - 1)
                tokenInitBlockPropertyCodeBuilder.add(",")
            tokenInitBlockPropertyCodeBuilder.add("\n")
        }
    }

    tokenInitBlockPropertyCodeBuilder.add(")")
    val tokenInitBlockPropertyCode = tokenInitBlockPropertyCodeBuilder.build()

    val tokenInitBlockProperty = PropertySpec.builder(
            "tokenInitBlocks",
            ClassName("kotlin", "Array").parameterizedBy(tokenFnTypeAlias.type.copy(nullable = true)),
            KModifier.PRIVATE
        )
        .initializer(tokenInitBlockPropertyCode)
        .build()


    val tokenDefInitCodeBuilder = CodeBlock.builder()
        .add("arrayOf(")
    definitions.forEachIndexed { idx: Int, def: TokenDefinition ->
        tokenDefInitCodeBuilder.add(
            "TokenDefinition(TokenType.values()[%L], %T(%S))",
            idx,
            Regex::class.asClassName(),
            def.rule.pattern
        )
        if (idx != definitions.size - 1)
        {
            // add a comma + newline on all lines except the last one
            tokenDefInitCodeBuilder.add(",\n")
        }
    }
    /* Add closing paren for arrayOf */
    tokenDefInitCodeBuilder.add(")")
    /* Build the property */
    val tokenDefinitionInitCode = tokenDefInitCodeBuilder.build()

    /* Use the above code to generate a private property */
    val tokenDefinitionsProperty = PropertySpec.builder(
            "tokenDefinitions",
            ClassName("kotlin", "Array").parameterizedBy(TokenDefinition::class.asClassName()),
            KModifier.PRIVATE
        )
        .initializer(tokenDefinitionInitCode)
        .build()


    /* The Definition of the TokenDefinition Data Class */
    val tokenDefinitionDataClass = TypeSpec.classBuilder("TokenDefinition")
        .addModifiers(KModifier.DATA)
        .primaryConstructor(FunSpec.constructorBuilder()
            .addParameter("type", TokenType::class)
            .addParameter("rule", Regex::class)
            .build()
        )
        .addProperty(PropertySpec.builder("type", TokenType::class)
            .initializer("type")
            .build()
        )
        .addProperty(PropertySpec.builder("rule", Regex::class)
            .initializer("rule")
            .build()
        )
        .addKdoc("    A class that defines the definition of a Token.\n")
        .addKdoc("    The is what the lex file is parsed into")
        .build()


    /* The Definition of the Token Data Class */
    val tokenDataClass = TypeSpec.classBuilder("Token")
        .addModifiers(KModifier.DATA)
        .primaryConstructor(FunSpec.constructorBuilder()
            .addParameter("matchedText", String::class)
            .addParameter("tokenType", TokenType::class)
            .addParameter("lineNumber", Int::class)
            .addParameter("wordIndex", Int::class)
            .build()
        )
        .addProperty(PropertySpec.builder("matchedText", String::class)
            .initializer("matchedText")
            .build()
        )
        .addProperty(PropertySpec.builder("tokenType", TokenType::class)
            .initializer("tokenType")
            .build()
        )
        .addProperty(PropertySpec.builder("lineNumber", Int::class)
            .initializer("lineNumber")
            .build()
        )
        .addProperty(PropertySpec.builder("wordIndex", Int::class)
            .initializer("wordIndex")
            .build()
        )
        .addInitializerBlock(CodeBlock.of("tokenInitBlocks[tokenType.ordinal]?.invoke(this)\n"))/* Feature coming soon... */
        .addKdoc("Token class will have the following:\n")
        .addKdoc("    matchedText: String     - the string of text it matched with\n")
        .addKdoc("    tokenType:   TokenType  - the type of token it was evaluated to be\n")
        .addKdoc("    lineNumber:  Int        - the line number the token matched on\n")
        .addKdoc("    wordIndex:   Int        - the index of the word on the line (0 for first word, 1 for second, etc.)\n")
        .addKdoc("\n")
        .addKdoc("    Other Initialization:\n")
        .addKdoc("        Calls the block of code associated with the TokenType")
        .build()


    val tokenizeClassFunc = FunSpec.builder("tokenize")
        .returns(ClassName("kotlin.collections", "MutableList").parameterizedBy(Token::class.asClassName()))
        .addCode("""
            |val output = mutableListOf<Token>()
            |lines.forEachIndexed { lineNumber: Int, line: String ->
            |    val chunks = line.split(""${'"'}\s""${'"'}.toRegex()).toMutableList()
            |    chunks.removeIf { chunk -> chunk.isEmpty() }
            |
            |    var tokenType: TokenType?
            |    chunks.forEachIndexed { wordIndex: Int, inputToken: String ->
            |        tokenType = null
            |        tokenDefinitions.forEach { tokenDef ->
            |            if (tokenDef.rule.matches(inputToken))
            |                tokenType = tokenDef.type
            |        }
            |
            |        if (tokenType != null)
            |            output.add(Token(inputToken, tokenType!!, lineNumber, wordIndex))
            |        else {
            |            /* No valid token was found! (Did the user not supply an error type??) */
            |        }
            |    }
            |}
            |
            |return output
            |""".trimMargin()
        )
        .build()


    val lexerClass = TypeSpec.classBuilder("Lexer")
        .primaryConstructor(FunSpec.constructorBuilder()
            .addParameter("inputFilepath", String::class)
            .build()
        )
        .addProperty(PropertySpec.builder(
                "lines",
                ClassName("kotlin.collections", "MutableList").parameterizedBy(String::class.asClassName()),
                KModifier.PRIVATE
            )
            .initializer(CodeBlock.of("mutableListOf()"))
            .build()
        )
        .addInitializerBlock(CodeBlock.of("""
            |val file = File(inputFilepath)
            |if (file.exists())
            |    file.forEachLine { lines.add(it) }
            |""".trimMargin())
        )
        .addFunction(tokenizeClassFunc)
        .build()

    return FileSpec.builder("KFlex", outputFileName ?: "Output")
        .addImport("java.io", "File")
        .addTypeAlias(tokenFnTypeAlias)
        .addType(tokenTypeEnum)
        .addProperty(tokenInitBlockProperty) // Coming soon!
        .addProperty(tokenDefinitionsProperty)
        .addType(tokenDefinitionDataClass)
        .addType(tokenDataClass)
        .addType(lexerClass)
        .build()

}