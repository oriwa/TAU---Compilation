package slp;

/** An AST node for program static calls.
 */
public class StaticCall extends Call {
	public final int line;
	public final String className;
	public final String name;
	public final ExprList callArgs;
	
 
	public StaticCall(int line,String className,String name,ExprList callArgs)  {
		this.line = line;
		this.className = className;
		this.name = name;
		this.callArgs=callArgs;
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
		return line+": Call to static method: " + name+ ", in class "+ className;
	}	
}