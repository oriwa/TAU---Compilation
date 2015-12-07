package slp;

import java.util.ArrayList;
import java.util.List;


/** An AST node for a list of declarations.
 */
public class DclrList extends ASTNode {
	
	
	public final List<Dclr> declarations = new ArrayList<Dclr>();
	public final List<Field> fields = new ArrayList<Field>();
	public final List<Method> methods = new ArrayList<Method>();
	


	/** Adds a method to the tail of the list.
	 * 
	 * @param method A program method.
	 */
	public void addMethod(Method method) {
		if (method != null)
		{
			methods.add(method);
			declarations.add(method);
		}
		
	}
	
	/** Adds a field to the tail of the list.
	 * 
	 * @param field A program field.
	 */
	public void addField(Field field) {
		if (field != null)
		{
			fields.add(field);
			declarations.add(field);
		}
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
}