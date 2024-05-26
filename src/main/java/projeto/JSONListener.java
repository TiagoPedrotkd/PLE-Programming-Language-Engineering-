// Generated from C:/Users/tiago/OneDrive/Documentos/GitHub/PLE-Programming-Language-Engineering-/src/main/kotlin/projeto/JSON.g4 by ANTLR 4.13.1
package projeto;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JSONParser}.
 */
public interface JSONListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JSONParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(JSONParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSONParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(JSONParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSONParser#object}.
	 * @param ctx the parse tree
	 */
	void enterObject(JSONParser.ObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSONParser#object}.
	 * @param ctx the parse tree
	 */
	void exitObject(JSONParser.ObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSONParser#pair}.
	 * @param ctx the parse tree
	 */
	void enterPair(JSONParser.PairContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSONParser#pair}.
	 * @param ctx the parse tree
	 */
	void exitPair(JSONParser.PairContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSONParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(JSONParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSONParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(JSONParser.ArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSONParser#script}.
	 * @param ctx the parse tree
	 */
	void enterScript(JSONParser.ScriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSONParser#script}.
	 * @param ctx the parse tree
	 */
	void exitScript(JSONParser.ScriptContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSONParser#instruction}.
	 * @param ctx the parse tree
	 */
	void enterInstruction(JSONParser.InstructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSONParser#instruction}.
	 * @param ctx the parse tree
	 */
	void exitInstruction(JSONParser.InstructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSONParser#loadStatement}.
	 * @param ctx the parse tree
	 */
	void enterLoadStatement(JSONParser.LoadStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSONParser#loadStatement}.
	 * @param ctx the parse tree
	 */
	void exitLoadStatement(JSONParser.LoadStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSONParser#saveStatement}.
	 * @param ctx the parse tree
	 */
	void enterSaveStatement(JSONParser.SaveStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSONParser#saveStatement}.
	 * @param ctx the parse tree
	 */
	void exitSaveStatement(JSONParser.SaveStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSONParser#assign}.
	 * @param ctx the parse tree
	 */
	void enterAssign(JSONParser.AssignContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSONParser#assign}.
	 * @param ctx the parse tree
	 */
	void exitAssign(JSONParser.AssignContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSONParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(JSONParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSONParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(JSONParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSONParser#expressionAccess}.
	 * @param ctx the parse tree
	 */
	void enterExpressionAccess(JSONParser.ExpressionAccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSONParser#expressionAccess}.
	 * @param ctx the parse tree
	 */
	void exitExpressionAccess(JSONParser.ExpressionAccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link JSONParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(JSONParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link JSONParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(JSONParser.VariableContext ctx);
}