package slp;

public class IRVisitResult {

	public TypeEntry type;
	public Object value;
	
	public IRVisitResult()
	{
		
	}
	
	public IRVisitResult(TypeEntry type,Object value)
	{
		this.type=type;
		this.value=value;
	}
	
}
