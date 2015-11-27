package slp;

/** An AST node for program field.
 */
public class Field extends Dclr {

	public final int line;
	public final Type type;
	public final String name;
	public final ExtraIDs extraIDs;
	
	
	public Field(int line,Type type,String name,ExtraIDs extraIDs)
	{
		this.line = line;
		this.type=type;
		this.name=name;
		this.extraIDs=extraIDs;
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
		
		return line+": Declaration of field: "+name + extraIDs.toString();
	
	}	
}