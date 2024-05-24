package projeto

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

data class JScript(val instructions : List<JInstruction>){
    val errosList = mutableListOf<JVarError>()

    fun validate() : List<JVarError> {
        instructions.forEachIndexed{
                index, instruction ->
            when (instruction) {
                is JLoad -> TODO()
                is JSave -> TODO()
                is JAssign -> {
                    if (instruction.expression is JVariable && !instructions.take(index).any { it is JAssign && it.varId == instruction.expression.name }) {
                        errosList.add(JVarError(instruction.expression.toString(), index + 1))
                    }
                }
            }
        }
        return errosList
    }
}

sealed interface JValue {
    fun prettyString(indent: String = "", level: Int = 0): String
}

sealed interface JInstruction {
    fun prettyStringInstruction(indent: String = "", level: Int = 0): String
}

sealed interface JExpression {
    fun prettyStringExpression(indent: String = "", level: Int = 0): String
}

data class JArray(val elements: List<JValue>) : JValue {
    override fun toString(): String = prettyString()

    override fun prettyString(indent: String, level: Int): String {
        val indentedElements = elements.joinToString(",\n${indent.repeat(level + 1)}") { it.prettyString(indent, level + 1) }
        return "[\n${indent.repeat(level + 1)}$indentedElements\n${indent.repeat(level)}]"
    }
}

data class JNumber(val value: Number) : JValue {
    override fun toString(): String = prettyString()
    override fun prettyString(indent: String, level: Int): String = value.toString()
}

data class JString(val value: String) : JValue {
    override fun toString(): String = prettyString()
    override fun prettyString(indent: String, level: Int): String = "\"$value\""
}

object JNull : JValue {
    override fun toString(): String = prettyString()
    override fun prettyString(indent: String, level: Int): String = "null"
}

data class JObject(val fields: List<JField>) : JValue {
    override fun toString(): String = prettyString()

    override fun prettyString(indent: String, level: Int): String {
        val indentedFields = fields.joinToString(",\n${indent.repeat(level + 1)}") { it.prettyString(indent, level + 1) }
        return "{\n${indent.repeat(level + 1)}$indentedFields\n${indent.repeat(level)}}"
    }
}

data class JField(val name: String, val value: JValue) {
    override fun toString(): String = prettyString()
    fun prettyString(indent: String = "", level: Int = 0): String = "\"$name\": ${value.prettyString(indent, level)}"
}

data class JBoolean(val value: Boolean) : JValue {
    override fun toString(): String = prettyString()
    override fun prettyString(indent: String, level: Int): String = value.toString()
}

data class JVarError(val varID: String, val line: Int) {
    override fun toString(): String = prettyString()
    private fun prettyString(): String = "$varID ---> $line;"
}

data class JLoad(val ficheiro: String, val id: String) : JInstruction {
    override fun toString(): String = prettyStringInstruction()
    override fun prettyStringInstruction(indent: String, level: Int): String = "JLoad(ficheiro=\"$ficheiro\", id=\"$id\")"

    fun run(ficheiroNomeReal: String): JValue {
        val lexer = JSONLexer(CharStreams.fromFileName(ficheiroNomeReal))
        val parser = JSONParser(CommonTokenStream(lexer))
        val v = parser.value()

        //println("O $ficheiroNomeReal foi carregado para $id")
        return v.toAST()
    }
}

data class JSave(val ficheiro: String, val id: String) : JInstruction {
    override fun toString(): String = prettyStringInstruction()
    override fun prettyStringInstruction(indent: String, level: Int): String = "JSave(ficheiro=\"$ficheiro\", id=\"$id\")"

    fun run(objeto: JValue, fileName: String): JValue {
        val content = objeto.toString()
        val file = File(fileName)
        file.bufferedWriter().use { out ->
            out.write(content)
        }
        //println("$id foi guardado para o ficheiro '$fileName'")
        return JString(content)
    }
}

data class JAssign(val varId: String, val expression: JExpression) : JInstruction {
    override fun toString(): String = prettyStringInstruction()
    override fun prettyStringInstruction(indent: String, level: Int): String = "$varId = ${expression.prettyStringExpression(indent, level)}"
}

data class JVariable(val name: String) : JExpression, JValue {
    override fun toString(): String = prettyStringExpression()
    override fun prettyStringExpression(indent: String, level: Int): String = prettyString()
    override fun prettyString(indent: String, level: Int): String = name

}

data class JValueExpression(val value: JValue) : JExpression {
    override fun toString(): String = prettyStringExpression()
    override fun prettyStringExpression(indent: String, level: Int): String = value.prettyString(indent, level)
}

data class JPropertyAccess(val base: String, val property: List<String>) : JExpression {
    override fun toString(): String = prettyStringExpression()
    override fun prettyStringExpression(indent: String, level: Int): String = if (property.isEmpty()) {
        base
    } else {
        "${base}.${property.joinToString(".")}"
    }
}

data class JOperationsAcess(val value: JExpression, val operator: String) : JExpression {
    override fun toString(): String = prettyStringExpression()
    override fun prettyStringExpression(indent: String, level: Int): String = "${value.prettyStringExpression(indent, level)} | $operator"
}
