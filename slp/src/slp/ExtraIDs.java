package slp;

import java.util.ArrayList;
import java.util.List;

public class ExtraIDs  extends ASTNode {
	public final List<String> ids = new ArrayList<String>();
	
	
	
	/** Adds a statement to the tail of the list.
	 * 
	 * @param stmt A program statement.
	 */
	public void addID(String id) {
		ids.add(id);
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
		
		String desc="";		
		for(String id : ids)
			desc+=" , "+id;		
		return desc;
	
	}	

}
