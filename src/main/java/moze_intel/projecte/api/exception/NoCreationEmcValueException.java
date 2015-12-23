package moze_intel.projecte.api.exception;

public class NoCreationEmcValueException extends RuntimeException
{
	public NoCreationEmcValueException()
	{
	}

	public NoCreationEmcValueException(String message)
	{
		super(message);
	}

	public NoCreationEmcValueException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public NoCreationEmcValueException(Throwable cause)
	{
		super(cause);
	}
}
