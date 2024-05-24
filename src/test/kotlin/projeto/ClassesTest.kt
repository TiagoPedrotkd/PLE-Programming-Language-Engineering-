package projeto

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.BeforeEach
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ClassesTest {

    private lateinit var interpreter: JIntepreter
    @BeforeEach
    fun setup() {
        val script = JScript(listOf(
            JLoad("lei.json", "doc"),
            JAssign("curso", JPropertyAccess("doc", listOf("curso"))),
            JAssign("ucs", JPropertyAccess("doc", listOf("ucs"))),
            JAssign("siglas", JPropertyAccess("doc", listOf("ucs", "sigla"))),
            JAssign("creditos", JOperationsAcess(JPropertyAccess("doc", listOf("ucs", "creditos")), "SUM")),
            JAssign("maxHorasUc", JOperationsAcess(JPropertyAccess("doc", listOf("ucs", "horas")), "MAX")),
            JAssign("total", JOperationsAcess(JPropertyAccess("ucs", emptyList()), "COUNT"))
        ))
        interpreter = JIntepreter(script)
    }


    @Test
    fun scriptTest(){

        val text = """
            load ${'$'}1 to doc
            curso = doc.curso
            ucs = doc.ucs
            siglas = doc.ucs.sigla
            creditos = doc.ucs.creditos | SUM
            maxHorasUc = doc.ucs.horas | MAX
            total = ucs | COUNT
            
            resumo = {
            "curso": curso,
            "ucs": siglas,
            "creditos": creditos,
            "maxHoras": maxHorasUc,
            "totalUcs": total
            }
            
            save resumo to ${'$'}2
            """.trimIndent()


        val expected = JScript(listOf(
            JLoad("\$1", "doc"),
            JAssign("curso", JPropertyAccess("doc", listOf("curso"))),
            JAssign("ucs", JPropertyAccess("doc", listOf("ucs"))),
            JAssign("siglas", JPropertyAccess("doc", listOf("ucs", "sigla"))),
            JAssign("creditos", JOperationsAcess(JPropertyAccess("doc", listOf("ucs", "creditos")), "SUM")),
            JAssign("maxHorasUc", JOperationsAcess(JPropertyAccess("doc", listOf("ucs", "horas")), "MAX")),
            JAssign("total", JOperationsAcess(JPropertyAccess("ucs", emptyList()), "COUNT")),
            JAssign("resumo", JValueExpression(JObject(listOf(
                JField("curso", JVariable("curso")),
                JField("ucs", JVariable("siglas")),
                JField("creditos", JVariable("creditos")),
                JField("maxHoras", JVariable("maxHorasUc")),
                JField("totalUcs", JVariable("total"))
            )))),
            JSave("\$2", "resumo")
        ))

        val lexer = JSONLexer(CharStreams.fromString(text))
        val parser = JSONParser(CommonTokenStream(lexer))
        val jsonContext = parser.script()
        val json = jsonContext.toAst()

        assertEquals(expected, json)
    }

    @Test
    fun testJLoad(){
        val filename = "lei.json"
        val jload = JLoad("\$1", "doc")

        val result = jload.run(filename)

        val expected = JObject(listOf(
            JField("curso", JString("LEI")),
            JField("ucs", JArray(listOf(
                JObject(listOf(
                    JField("sigla", JString("IP")),
                    JField("creditos", JNumber(6.0)),
                    JField("horas", JNumber(4.5)),
                )),
                JObject(listOf(
                    JField("sigla", JString("POO")),
                    JField("creditos", JNumber(6.0)),
                    JField("horas", JNumber(4.5)),
                )),
                JObject(listOf(
                    JField("sigla", JString("ELP")),
                    JField("creditos", JNumber(6.0)),
                    JField("horas", JNumber(3.0)),
                ))
            )))
        ))

        assertEquals(expected, result)
    }

    @Test
    fun testJSave(){
        val filename = "output_test.json"

        val jSave = JSave("\$2", "resumo")
        val resumo = JObject(listOf(
            JField("curso", JString("Engenharia Informática")),
            JField("ucs", JArray(listOf(
                JObject(listOf(
                    JField("sigla", JString("P1")),
                    JField("creditos", JNumber(6)),
                    JField("horas", JNumber(60))
                )),
                JObject(listOf(
                    JField("sigla", JString("M1")),
                    JField("creditos", JNumber(6)),
                    JField("horas", JNumber(75))
                ))
            )))
        ))

        jSave.run(resumo, filename)

        val savedFile = File(filename)
        val savedContent = savedFile.readText()

        val expectedContent = """
        {
        "curso": "Engenharia Informática",
        "ucs": [
        {
        "sigla": "P1",
        "creditos": 6,
        "horas": 60
        },
        {
        "sigla": "M1",
        "creditos": 6,
        "horas": 75
        }
        ]
        }
        """.trimIndent()

        savedFile.delete()

        assertEquals(expectedContent, savedContent)
    }

    @Test
    fun `test curso assignment`() {
        val arguments = arrayOf("input.json")
        interpreter.run(arguments)
        assertEquals(JString("LEI"), interpreter.memory["curso"])
    }

    @Test
    fun `test ucs assignment`() {
        val arguments = arrayOf("input.json")
        interpreter.run(arguments)
        val expectedUcs = JArray(listOf(
            JObject(listOf(JField("sigla", JString("IP")), JField("creditos", JNumber(6.0)), JField("horas", JNumber(4.5)))),
            JObject(listOf(JField("sigla", JString("POO")), JField("creditos", JNumber(6.0)), JField("horas", JNumber(4.5)))),
            JObject(listOf(JField("sigla", JString("ELP")), JField("creditos", JNumber(6.0)), JField("horas", JNumber(3.0))))
        ))
        assertEquals(expectedUcs, interpreter.memory["ucs"])
    }

    @Test
    fun `test siglas assignment`() {
        val arguments = arrayOf("input.json")
        interpreter.run(arguments)
        val expectedSiglas = JArray(listOf(JString("IP"), JString("POO"), JString("ELP")))
        assertEquals(expectedSiglas, interpreter.memory["siglas"])
    }

    @Test
    fun `test creditos assignment`() {
        val arguments = arrayOf("input.json")
        interpreter.run(arguments)
        assertEquals(JNumber(18.0), interpreter.memory["creditos"])
    }

    @Test
    fun `test maxHorasUc assignment`() {
        val arguments = arrayOf("input.json")
        interpreter.run(arguments)
        assertEquals(JNumber(4.5), interpreter.memory["maxHorasUc"])
    }

    @Test
    fun `test total assignment`() {
        val arguments = arrayOf("input.json")
        interpreter.run(arguments)
        assertEquals(JNumber(3), interpreter.memory["total"])
    }

}