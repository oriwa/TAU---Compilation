package slp;


/** An AST node for program class.
 */
public class Class extends ASTNode {
	public final int line;
	public final String name;
	public final String extends_name;
	public final DclrList dclrList;
	public Class extends_class;
	
	public Class(int line,String name,DclrList dclrList) {
		this.line = line;
		this.name = name;
		this.extends_name=null;
		this.dclrList = dclrList;
	}
	
	public Class(int line,String name,String extends_name,DclrList dclrList) {
		this.line = line;
		this.name = name;
		this.extends_name = extends_name;
		this.dclrList = dclrList;
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
		if(extends_name==null)
			return  line+": Declaration of class: "+name;
		else
			return line+": Declaration of class: "+name +" extends " + extends_name;
	}	
}