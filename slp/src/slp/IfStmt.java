package slp;

/**
 * An AST node for if statements.
 */
public class IfStmt extends Stmt {
	public final int line;
	public final Expr expr;
	public final Stmt ifStmt;
	public final Stmt elseStmt;
	
	public IfStmt(int line,Expr expr,Stmt ifStmt) {
		this.line = line;
		this.expr = expr;
		this.ifStmt = ifStmt;
		this.elseStmt = null;
	}
	
	public IfStmt(int line,Expr expr,Stmt ifStmt,Stmt elseStmt) {
		this.line = line;
		this.expr = expr;
		this.ifStmt = ifStmt;
		this.elseStmt = elseStmt;
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
	
	public String toString() {
		return line+": If"+((elseStmt==null)?"":"-else")+" statement";
	}
}