package slp;

/** An AST node for unary expressions.
 */
public class UnaryOpExpr extends Expr {

	public final int line;
	public final Operator op;
	public final Expr operand;
	
	public UnaryOpExpr(int line,Expr operand, Operator op) {
		this.line = line;
		this.operand = operand;
		this.op = op;
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
		if(op==Operator.MINUS) return  line+": Mathematical unary operation: minus";
		if(op==Operator.LNEG) return  line+": Logical unary operation: negation";
		return line+": ERROR: unary operation is invalid";
	}
}