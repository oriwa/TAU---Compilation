package slp;

/** A base class for AST nodes for declarations.
 */
public abstract class Dclr extends ASTNode {
	/** Accepts a visitor object as part of the visitor pattern.
	 * @param visitor A visitor.
	 */
	public abstract void accept(Visitor visitor);
}