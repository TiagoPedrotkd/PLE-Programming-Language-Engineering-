package projeto

data class JIntepreter(val script : JScript){
    val memory = mutableMapOf<String, JValue>()
    private val argJScript = mutableMapOf<String, String>()

    fun run(arguments : Array<String>){
        var index = 1
        arguments.forEach {
                it2 ->
            argJScript["$$index"] = it2
            index++
        }

        script.instructions.forEachIndexed { indexVal, instruction ->
            try {
                executeInstruction(instruction, indexVal)
            }catch (e : Exception){
                println("Error at line ${indexVal + 1}: ${e.message}")
                script.validate()
                script.execute()
                script.errorsList.add(JVarError(instruction.toString(), indexVal + 1))
            }
        }
    }

    private fun executeInstruction(instruction: JInstruction, line: Int) {
        when (instruction){
            is JLoad -> {
                memory[instruction.id] = instruction.run(argJScript[instruction.ficheiro] ?: instruction.ficheiro)
                //println("Memory after load: $memory")
                //println("ArgJScript after load: $argJScript")
            }
            is JSave -> {
                val objeto = memory[instruction.id] ?: throw IllegalArgumentException("No value found for id: ${instruction.id}")
                instruction.run(objeto, argJScript[instruction.ficheiro].toString())
            }
            is JAssign -> {
                memory[instruction.varId] = evaluateExpression(instruction.expression, line)
                //println("Memory after assign: $memory")
                //println("ArgJScript after assign: $argJScript")
            }
        }
    }

    private fun evaluateExpression(expression: JExpression, line: Int): JValue {
        return when (expression) {
            is JVariable -> evaluateVariable(expression, line)
            is JValueExpression -> evaluateValueExpression(expression, line)
            is JPropertyAccess -> evaluatePropertyAccess(expression, line)
            is JOperationsAccess -> evaluateOperationsAccess(expression, line)
            else -> TODO()
        }
    }

    private fun evaluateVariable(expression: JVariable, line: Int): JValue {
        return memory[expression.name] ?: throw IllegalArgumentException("No value found for variable: ${expression.name}").also {
            script.errorsList.add(JVarError(expression.name, line))
        }
    }

    private fun evaluateValueExpression(expression: JValueExpression, line: Int): JValue {
        return if (expression.value is JObject) {
            val newFields = expression.value.fields.map { field ->
                val fieldValue = memory[field.value.toString()]
                if (fieldValue != null) {
                    JField(field.name, fieldValue)
                } else {
                    script.errorsList.add(JVarError(field.value.toString(), line))
                    throw IllegalArgumentException("No value found for variable: ${field.value}")
                }
            }
            JObject(newFields)
        } else {
            expression.value
        }
    }

    private fun evaluatePropertyAccess(expression: JPropertyAccess, line: Int): JValue {
        val baseValue = evaluateExpression(JVariable(expression.base), line)
        return when {
            expression.property.size > 1 -> evaluateNestedProperty(baseValue, expression, line)
            expression.property.isEmpty() -> baseValue
            else -> evaluateSingleProperty(baseValue, expression.property[0], expression, line)
        }
    }

    private fun evaluateNestedProperty(baseValue: JValue, expression: JPropertyAccess, line: Int): JValue {
        return when (baseValue) {
            is JObject -> {
                val result = baseValue.fields.find { it.name == expression.property[0] }?.value
                if (result is JArray) {
                    val values = result.elements.mapNotNull {
                        if (it is JObject) {
                            it.fields.find { field -> field.name == expression.property[1] }?.value
                        } else null
                    }
                    JArray(values)
                } else {
                    throw IllegalArgumentException("Property ${expression.property[1]} not found in object").also {
                        script.errorsList.add(JVarError(expression.toString(), line))
                    }
                }
            }
            else -> throw IllegalArgumentException("Property ${expression.property[0]} not found in object").also {
                script.errorsList.add(JVarError(expression.toString(), line))
            }
        }
    }

    private fun evaluateSingleProperty(baseValue: JValue, property: String, expression: JPropertyAccess, line: Int): JValue {
        return when (baseValue) {
            is JObject -> {
                baseValue.fields.find { it.name == property }?.value
                    ?: throw IllegalArgumentException("Property $property not found in object").also {
                        script.errorsList.add(JVarError(expression.toString(), line))
                    }
            }
            else -> throw IllegalArgumentException("Property $property not found in object").also {
                script.errorsList.add(JVarError(expression.toString(), line))
            }
        }
    }

    private fun evaluateOperationsAccess(expression: JOperationsAccess, line: Int): JValue {
        val value = evaluateExpression(expression.value, line)
        return when (val operator = expression.operator) {
            "SUM" -> evaluateSum(value, expression, line)
            "COUNT" -> evaluateCount(value, expression, line)
            "MAX" -> evaluateMax(value, expression, line)
            "MIN" -> evaluateMin(value, expression, line)
            "AVG" -> evaluateAvg(value, expression, line)
            else -> throw IllegalArgumentException("Unsupported operator: $operator").also {
                script.errorsList.add(JVarError(expression.toString(), line))
            }
        }
    }

    private fun evaluateSum(value: JValue, expression: JOperationsAccess, line: Int): JValue {
        return if (value is JArray) {
            val sum = value.elements.filterIsInstance<JNumber>().sumOf { it.value.toDouble() }
            JNumber(sum)
        } else {
            throw IllegalArgumentException("SUM operation requires an array of numbers").also {
                script.errorsList.add(JVarError(expression.toString(), line))
            }
        }
    }

    private fun evaluateCount(value: JValue, expression: JOperationsAccess, line: Int): JValue {
        return if (value is JArray) {
            val count = value.elements.count()
            JNumber(count)
        } else {
            throw IllegalArgumentException("COUNT operation requires an array of a value").also {
                script.errorsList.add(JVarError(expression.toString(), line))
            }
        }
    }

    private fun evaluateMax(value: JValue, expression: JOperationsAccess, line: Int): JValue {
        return if (value is JArray) {
            val max = value.elements.filterIsInstance<JNumber>().maxOf { it.value.toDouble() }
            JNumber(max)
        } else {
            throw IllegalArgumentException("MAX operation requires an array of numbers").also {
                script.errorsList.add(JVarError(expression.toString(), line))
            }
        }
    }

    private fun evaluateMin(value: JValue, expression: JOperationsAccess, line: Int): JValue {
        return if (value is JArray) {
            val min = value.elements.filterIsInstance<JNumber>().minOf { it.value.toDouble() }
            JNumber(min)
        } else {
            throw IllegalArgumentException("MIN operation requires an array of numbers").also {
                script.errorsList.add(JVarError(expression.toString(), line))
            }
        }
    }

    private fun evaluateAvg(value: JValue, expression: JOperationsAccess, line: Int): JValue {
        return if (value is JArray) {
            val numbers = value.elements.filterIsInstance<JNumber>()
            val avg = numbers.map { it.value.toDouble() }.average()
            JNumber(avg)
        } else {
            throw IllegalArgumentException("AVG operation requires an array of numbers").also {
                script.errorsList.add(JVarError(expression.toString(), line))
            }
        }
    }
}