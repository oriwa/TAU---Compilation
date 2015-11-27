package slp;

/** An AST node for program type.
 */
public class Type extends ASTNode {
	
	
	private static final String INT="int";
	private static final String BOOLEAN="boolean";
	private static final String STRING="string";
			

	public final int line;
	public String name;
	public int array_dimension=0;
	
	
	public Type(int line)
	{
		this.line = line;
	}
	
	
	public void setName(String name)
	{
		this.name=name;
	}
	
	public void addDimension()
	{
		array_dimension++;
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
		String desc=line+": ";
		if(isPrimitive())
			desc+="Primitive ";
		else
			desc+="User-defined ";
		if(array_dimension==0)
			return desc+"data type: "+name;
		else
			return desc+"data type: "+array_dimension +"-dimensional array of " + name;
	}
	
	public boolean isPrimitive()
	{
		return name==INT || name==BOOLEAN|| name==STRING;
	}
	
	
}