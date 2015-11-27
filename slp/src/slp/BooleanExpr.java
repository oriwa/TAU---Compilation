package slp;


/** An expression denoting a constant boolean.
*/
public class BooleanExpr extends Expr {
	/** The constant represented by this expression.
	 * 
	 */
	public final int line;
	public final boolean value;
	
	public BooleanExpr(int line,boolean value) {
		this.line = line;
		this.value = value;
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
		if(value)
			return line+": Boolean literal: true";
		else
			return line+": Boolean literal: false";
	}	
}