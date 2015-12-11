package slp;

public class VisitResult {
	
	
	public TypeEntry type;
	public int arrayDimension=0;
	public Object value;
	public boolean hasReturnStatement;
	
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
