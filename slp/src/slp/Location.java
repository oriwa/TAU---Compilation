package slp;

/** A base class for AST nodes for location.
 */
public abstract class Location extends ASTNode {
	/** Accepts a visitor object as part of the visitor pattern.
	 * @param visitor A visitor.
	 */
	public abstract void accept(Visitor visitor);
	
	
}