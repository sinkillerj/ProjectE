package moze_intel.projecte.gameObjs.tiles;

public class DMFurnaceTile extends RMFurnaceTile
{
	public DMFurnaceTile()
	{
		super(10, 3);
	}
	
	@Override
	protected int getInvSize()
	{
		return 9;
	}

	@Override
	protected float getOreDoubleChance() {
		return 0.5F;
	}

	@Override
	public int getCookProgressScaled(int value)
	{
		return furnaceCookTime * value / ticksBeforeSmelt;
	}
}