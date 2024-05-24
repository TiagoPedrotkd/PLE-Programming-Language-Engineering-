// Generated from C:/Users/tiago/OneDrive/Ambiente de Trabalho/Projetos/PPM/ELP_1/src/main/kotlin/projeto/JSON.g4 by ANTLR 4.13.1
package projeto;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link JSONParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface JSONVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link JSONParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(JSONParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link JSONParser#object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject(JSONParser.ObjectContext ctx);
	/**
	 * Visit a parse tree produced by {@link JSONParser#pair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair(JSONParser.PairContext ctx);
	/**
	 * Visit a parse tree produced by {@link JSONParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(JSONParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link JSONParser#script}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScript(JSONParser.ScriptContext ctx);
	/**
	 * Visit a parse tree produced by {@link JSONParser#instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstruction(JSONParser.InstructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JSONParser#loadStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoadStatement(JSONParser.LoadStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JSONParser#saveStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSaveStatement(JSONParser.SaveStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JSONParser#assign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign(JSONParser.AssignContext ctx);
	/**
	 * Visit a parse tree produced by {@link JSONParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(JSONParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JSONParser#expressionAccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionAccess(JSONParser.ExpressionAccessContext ctx);
	/**
	 * Visit a parse tree produced by {@link JSONParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(JSONParser.VariableContext ctx);
}