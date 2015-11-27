package slp;


/** An AST node for program virtual calls.
 */
public class VirtualCall extends Call {
	public final int line;
	public final Expr expr;
	public final String name;
	public final ExprList callArgs;
	
	public VirtualCall(int line,String name,ExprList callArgs) {	
		this.line = line;
		this.expr = null;
		this.name = name;
		this.callArgs=callArgs;
	}
	public VirtualCall(int line,Expr expr,String name,ExprList callArgs)  {
		this.line = line;
		this.expr = expr;
		this.name = name;
		this.callArgs=callArgs;
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
		if(expr==null)
			return line+": Call to virtual method: " + name;
		else
			return line+": Call to virtual method: " + name+ ", in external scope";
	}	
}