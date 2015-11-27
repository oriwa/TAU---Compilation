package slp;

/** An AST node for program variables.
 */
public class VarExpr extends Location {
	public final int line;
	public final String name;
	public final Expr target_expr;
	
	public VarExpr(int line,String name) {
		this.line = line;
		this.name = name;
		this.target_expr=null;
	}
	public VarExpr(int line,Expr target_expr,String name) {
		this.line = line;
		this.name = name;
		this.target_expr=target_expr;
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
		if(target_expr==null)
			return line+": Reference to variable: " + name;
		else
			return line+": Reference to variable: " + name+ ", in external scope";
	}	
}