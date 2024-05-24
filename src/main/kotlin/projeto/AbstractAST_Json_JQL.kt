package projeto

import projeto.JSONParser.*

fun ValueContext.toAST() : JValue {
    return when {
        STRING() != null -> {
            val string = STRING().text
            JString(string.substring(1, string.length - 1))
        }
        NUMBER() != null -> {
            JNumber(NUMBER().text.toDouble())
        }
        BOOLEAN() != null -> {
            JBoolean(BOOLEAN().text.toBoolean())
        }
        `object`() != null -> {
            val fields = `object`().pair().map { JField(it.STRING().text.substring(1, it.STRING().text.length - 1), it.value().toAST()) }
            JObject(fields)
        }
        array() != null -> {
            val fields = array().value().map { it.toAST() }
            JArray(fields)
        }
        variable() != null -> {
            val variable = variable()
            JVariable(variable.ID().text)
        }
        else -> {
            JNull
        }
    }
}

fun InstructionContext.toAst() : JInstruction{
    if (loadStatement() == null) {
        if(saveStatement() != null){
            val save = saveStatement()
            return JSave(save.PARAMETRO().text, save.ID().text)
        } else if(assign() != null){
            val assign = assign()
            return JAssign(assign.ID().text, assign.expression().toAst())
        } else{
            TODO()
        }
    } else {
        val load = loadStatement()
        return JLoad(load.PARAMETRO().text, load.ID().text)
    }
}

fun ExpressionContext.toAst(): JExpression {
    when {
        value() != null -> {
            val value = value()
            return JValueExpression(value.toAST())
        }
        expressionAccess() != null -> {
            val expressionAccess = expressionAccess().text.split("|")
            return when (expressionAccess.size) {
                2 -> {
                    val expressionB = expressionAccess[0].split(".")
                    JOperationsAcess(JPropertyAccess(expressionB[0], expressionB.drop(1)), expressionAccess[1])
                }

                else -> {
                    val expressionB = expressionAccess().text.split(".")
                    JPropertyAccess(expressionB[0], expressionB.drop(1))
                }
            }
        }
        variable() != null -> {
            val variable = variable()
            return JVariable(variable.ID().text)
        }
        else -> {
            TODO()
        }
    }
}

fun ScriptContext.toAst() : JScript{
    return JScript(instruction().map { it.toAst() })
}