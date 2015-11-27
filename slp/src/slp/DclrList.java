package slp;

import java.util.ArrayList;
import java.util.List;


/** An AST node for a list of declarations.
 */
public class DclrList extends ASTNode {
	public final List<Dclr> declarations = new ArrayList<Dclr>();
	


	/** Adds a statement to the tail of the list.
	 * 
	 * @param stmt A program statement.
	 */
	public void addDclr(Dclr dclr) {
		declarations.add(dclr);
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