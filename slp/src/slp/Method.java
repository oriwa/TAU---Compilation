package slp;


/** An AST node for program method.
 */
public class Method extends Dclr {

	public final int line;
	public final boolean isStatic;
	public final Type type;
	public final String name;
	public final FormalsList formalsList;
	public final StmtList stmtList;
	
	

	public Method(int line,boolean isStatic,Type type,String name,FormalsList formalsList,StmtList stmtList)
	{
		this.line = line;
		this.isStatic=isStatic;
		this.type=type;
		this.name=name;	
		this.formalsList=formalsList;
		this.stmtList=stmtList;
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
		
		if(isStatic)
			return line+": Declaration of static method: "+ name;
		else
			return line+": Declaration of virtual method: "+ name;
	
	}	
}