package projeto

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

data class JScript(val instructions: List<JInstruction>) {
    val errorsList = mutableListOf<JError>()

    fun validate(): List<JError> {
        val definedVariables = mutableSetOf<String>()
        instructions.forEachIndexed { index, instruction ->
            when (instruction) {
                is JAssign -> {
                    val usedVariables = getUsedVariables(instruction.expression)
                    usedVariables.forEach { variable ->
                        if (variable !in definedVariables) {
                            errorsList.add(JVarError(variable, index + 1))
                        }
                    }

                    definedVariables.add(instruction.varId)

                    if (instruction.expression is JOperationsAccess) {
                        val operationAccess = instruction.expression
                        val expectedArgsCount = getExpectedArgsCount(operationAccess.operator)
                        val actualArgsCount = countArguments(operationAccess.value)
                        if (expectedArgsCount != actualArgsCount) {
                            errorsList.add(JArgsError(operationAccess.operator, index + 1, expectedArgsCount, actualArgsCount))
                        }
                    }
                }
                is JLoad -> {
                    definedVariables.add(instruction.id)
                }
                else -> Unit
            }
        }
        return errorsList
    }

    private fun getUsedVariables(expression: JExpression): Set<String> {
        val variables = mutableSetOf<String>()
        when (expression) {
            is JVariable -> variables.add(expression.name)
            is JValueExpression -> Unit
            is JPropertyAccess -> variables.add(expression.base)
            is JOperationsAccess -> variables.addAll(getUsedVariables(expression.value))
            else -> Unit
        }
        return variables
    }

    private fun getExpectedArgsCount(operator: String): Int {
        return when (operator) {
            "SUM", "MAX", "MIN", "COUNT", "AVG" -> 1
            else -> 0
        }
    }

    private fun countArguments(expression: JExpression): Int {
        return when (expression) {
            is JArray -> expression.elements.size
            is JValueExpression -> if (expression.value is JArray) (expression.value).elements.size else 1
            else -> 1
        }
    }

    fun execute(): List<JError> {
        val variableMap = mutableMapOf<String, JValue>()
        instructions.forEachIndexed { index, instruction ->
            try {
                when (instruction) {
                    is JAssign -> {
                        val value = evaluateExpression(instruction.expression, variableMap)
                        variableMap[instruction.varId] = value
                    }
                    is JLoad -> {
                        val value = instruction.run(instruction.ficheiro)
                        variableMap[instruction.id] = value
                    }
                    is JSave -> {
                        val value = variableMap[instruction.id]
                            ?: throw JExecutionException("Variable '${instruction.id}' not found", index + 1)
                        instruction.run(value, instruction.ficheiro)
                    }
                }
            } catch (e: JExecutionException) {
                errorsList.add(JExecutionError(e.message, index + 1))
            }
        }
        return errorsList
    }

    private fun evaluateExpression(expression: JExpression, variableMap: Map<String, JValue>): JValue {
        return when (expression) {
            is JVariable -> variableMap[expression.name]
                ?: throw JExecutionException("Variable '${expression.name}' not found", 0)
            is JValueExpression -> expression.value
            is JPropertyAccess -> {
                val baseValue = variableMap[expression.base]
                    ?: throw JExecutionException("Variable '${expression.base}' not found", 0)
                evaluatePropertyAccess(baseValue, expression.property)
            }
            is JOperationsAccess -> {
                val value = evaluateExpression(expression.value, variableMap)
                if (expression.operator == "SUM" && value is JArray) {
                    JNumber(value.elements.filterIsInstance<JNumber>().sumOf { it.value.toDouble() })
                } else {
                    throw JExecutionException("Unsupported operation '${expression.operator}'", 0)
                }
            }
            else -> throw JExecutionException("Unsupported expression", 0)
        }
    }

    private fun evaluatePropertyAccess(baseValue: JValue, property: List<String>): JValue {
        var currentValue = baseValue
        for (prop in property) {
            currentValue = when (currentValue) {
                is JObject -> currentValue.fields.find { it.name == prop }?.value
                    ?: throw JExecutionException("Field '$prop' not found", 0)
                else -> throw JExecutionException("Cannot access property '$prop' on non-object type", 0)
            }
        }
        return currentValue
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

data class JArray(val elements: List<JValue>) : JValue, JExpression {
    override fun toString(): String = prettyString()

    override fun prettyString(indent: String, level: Int): String {
        val indentedElements = elements.joinToString(",\n${indent.repeat(level + 1)}") { it.prettyString(indent, level + 1) }
        return "[\n${indent.repeat(level + 1)}$indentedElements\n${indent.repeat(level)}]"
    }

    override fun prettyStringExpression(indent: String, level: Int): String = prettyString(indent, level)
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

sealed class JError(open val message: String, open val line: Int) {
    override fun toString(): String = prettyString()
    protected open fun prettyString(): String = "$message at line $line;"
}

data class JVarError(val varID: String, override val line: Int) : JError("Undefined variable '$varID'", line) {
    override fun prettyString(): String = super.prettyString()
}

data class JArgsError(val operator: String, override val line: Int, val expected: Int, val actual: Int) : JError("Incorrect number of arguments for '$operator'", line) {
    override fun prettyString(): String = "$message: expected $expected, found $actual at line $line;"
}

data class JExecutionError(override val message: String, override val line: Int) : JError(message, line) {
    override fun prettyString(): String = super.prettyString()
}

class JExecutionException(override val message: String, val line: Int) : Exception(message)

data class JLoad(val ficheiro: String, val id: String) : JInstruction {
    override fun toString(): String = prettyStringInstruction()
    override fun prettyStringInstruction(indent: String, level: Int): String = "JLoad(ficheiro=\"$ficheiro\", id=\"$id\")"

    fun run(ficheiroNomeReal: String): JValue {
        val lexer = JSONLexer(CharStreams.fromFileName(ficheiroNomeReal))
        val parser = JSONParser(CommonTokenStream(lexer))
        val v = parser.value()

        // println("O $ficheiroNomeReal foi carregado para $id")
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
        // println("$id foi guardado para o ficheiro '$fileName'")
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

data class JOperationsAccess(val value: JExpression, val operator: String) : JExpression {
    override fun toString(): String = prettyStringExpression()
    override fun prettyStringExpression(indent: String, level: Int): String = "${value.prettyStringExpression(indent, level)} | $operator"
}