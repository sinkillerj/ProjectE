package moze_intel.projecte.api.exception;

public class NoDestructionEmcValueException extends RuntimeException
{
	public NoDestructionEmcValueException()
	{
	}

	public NoDestructionEmcValueException(String message)
	{
		super(message);
	}

	public NoDestructionEmcValueException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public NoDestructionEmcValueException(Throwable cause)
	{
		super(cause);
	}
}
