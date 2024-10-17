package net.swedz.extended_industrialization.machines.component.tesla.receiver;

public enum TeslaReceiverState
{
	SUCCESS,
	NO_LINK,
	UNLOADED_TRANSMITTER,
	MISMATCHING_VOLTAGE,
	TOO_FAR,
	UNDEFINED;
	
	public boolean isSuccess()
	{
		return this == SUCCESS;
	}
	
	public boolean isFailure()
	{
		return !this.isSuccess();
	}
}
