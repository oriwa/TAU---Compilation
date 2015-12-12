package slp;

public class VisitResult {
	
	
	public TypeEntry type;
	public Object value;
	public boolean hasReturnStatement;
	public String uninitializedId;
	public boolean isInitialized;
	public Scope prevScope;
	
	
	public VisitResult()
	{
		
	}
	
	public VisitResult(TypeEntry type)
	{
		this.type=type;
	}
	 
	
	public VisitResult(TypeEntry type,Object value)
	{
		this.type=type;
		this.value=value;
	}
	
	public VisitResult(boolean hasReturnStatement)
	{
		this.hasReturnStatement=hasReturnStatement;	
	}

}
