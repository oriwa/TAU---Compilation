package slp;

import java.util.List;
import java.util.ArrayList;

/** An AST node for a list of expressions.
 */
public class ExprList extends ASTNode {
	public final List<Expr> expressions = new ArrayList<Expr>();
	
 
	/** Adds a expression to the tail of the list.
	 * 
	 * @param expr A program expression.
	 */
	public void addExpr(Expr expr) {
		expressions.add(expr);
	}

	/** Accepts a visitor object as part of the visitor pattern.
	 * @param visitor A visitor.
	 */
	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	/** Accepts a propagating visitor parameterized by two types.
	 * 
	 * @param <DownType> The type of the object holding the context.
	 * @param <UpType> The type of the result object.
	 * @param visitor A propagating visitor.
	 * @param context An object holding context information.
	 * @return The result of visiting this node.
	 */
	@Override
	public <DownType, UpType> UpType accept(
			PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}	
}