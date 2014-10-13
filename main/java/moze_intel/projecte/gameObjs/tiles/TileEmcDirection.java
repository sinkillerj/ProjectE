package moze_intel.projecte.gameObjs.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEmcDirection extends TileEmc
{
	private ForgeDirection orientation;
	
	public TileEmcDirection()
	{
		super();
		this.orientation = ForgeDirection.SOUTH;
	}
	
	public TileEmcDirection(int maxEmc)
	{
		super(maxEmc);
		this.orientation = ForgeDirection.SOUTH;
	}
	
	public ForgeDirection getOrientation()
    {
        return orientation;
    }

    public void setOrientation(ForgeDirection orientation)
    {
        this.orientation = orientation;
    }

    public void setOrientation(int orientation)
    {
        this.orientation = ForgeDirection.getOrientation(orientation);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);

        if (nbtTagCompound.hasKey("Direction"))
            this.orientation = ForgeDirection.getOrientation(nbtTagCompound.getByte("Direction"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);

        nbtTagCompound.setByte("Direction", (byte) orientation.ordinal());
    }
    
    @Override
	public Packet getDescriptionPacket() 
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}
		
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) 
	{
		this.readFromNBT(packet.func_148857_g());
	}
}
