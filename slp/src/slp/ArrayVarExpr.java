package slp;


/** An AST node for program array variables.
 */
public class ArrayVarExpr extends Location {
	public final int line;
	public final Expr target_expr;
	public final Expr index_expr;
	
	public ArrayVarExpr(int line,Expr target_expr,Expr index_expr) {
		this.line = line;
		this.target_expr=target_expr;
		this.index_expr=index_expr;
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
	
	public String toString() {
			return line+": Reference to array";
	}	
}