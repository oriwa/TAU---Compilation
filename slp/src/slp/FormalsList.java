package slp;

import java.util.ArrayList;
import java.util.List;

/** An AST node for a list of formals.
 */
public class FormalsList extends ASTNode {
	public final List<Formals> formals = new ArrayList<Formals>();
	


	/** Adds a formals to the tail of the list.
	 * 
	 * @param formal A program formals.
	 */
	public void addStmt(Formals formal) {
		formals.add(formal);
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