package projeto

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

fun main(args: Array<String>) {
    val lexer = JSONLexer(CharStreams.fromFileName(args[0]))
    val parser = JSONParser(CommonTokenStream(lexer))
    val script = parser.script()

    //println(script.toAst())

    val intepreter = JIntepreter(script.toAst())
    intepreter.run(args.drop(1))
}