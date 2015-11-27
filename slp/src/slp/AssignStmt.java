package slp;

/**
 * An AST node for assignment statements.
 */
public class AssignStmt extends Stmt {
	public final int line;
	public final Location location;
	public final Expr rhs;

	public AssignStmt(int line,Location location, Expr rhs) {
		this.line = line;
		this.location = location;
		this.rhs = rhs;
	}

	/**
	 * Accepts a visitor object as part of the visitor pattern.
	 * 
	 * @param visitor
	 *            A visitor.
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
	
	public String toString(){
		return line+": Assignment statement";
	}
}