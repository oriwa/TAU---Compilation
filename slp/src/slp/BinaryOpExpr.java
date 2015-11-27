package slp;

/** An AST node for binary expressions.
 */
public class BinaryOpExpr extends Expr {
	public final int line;
	public final Expr lhs;
	public final Expr rhs;
	public final Operator op;
	
	public BinaryOpExpr(int line,Expr lhs, Expr rhs, Operator op) {
		this.line = line;
		this.lhs = lhs;
		this.rhs = rhs;
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
		String mathOp= line+": Mathematical binary operation: ";
		String logicOp = line+": Logical binary operation: ";
		switch(op){
		case MINUS:
		case PLUS:
		case MULTIPLY:
		case DIVIDE:
		case MOD:
			return mathOp+op;
		default:
			return logicOp+op;
		}
		
	}	
}