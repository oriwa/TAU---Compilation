package slp;


/** An AST node for declaration statements.
 */
public class DeclarationStmt extends Stmt {

	public final int line;
	public final Type type;
	public final String name;
	public final Expr value;
	
	
	public DeclarationStmt(int line,Type type,String name){
		this.line = line;
		this.type=type;
		this.name=name;
		this.value=null;
	}
	
	public DeclarationStmt(int line,Type type,String name,Expr value)
	{
		this.line = line;
		this.type=type;
		this.name=name;
		this.value=value;
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
		
		String desc= line+": Declaration of local variable: "+name;
		if(value!=null)
			desc+=", with initial value";
		return desc;
	
	}	
}