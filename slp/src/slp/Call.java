package slp;

/** A base class for AST nodes for calls.
 */
public abstract class Call extends Expr {
	/** Accepts a visitor object as part of the visitor pattern.
	 * @param visitor A visitor.
	 */
	public abstract void accept(Visitor visitor);
}
