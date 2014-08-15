package moze_intel.gameObjs.items;

import moze_intel.MozeCore;
import moze_intel.network.packets.ParticlePKT;
import moze_intel.utils.Constants;
import moze_intel.utils.Coordinates;
import moze_intel.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MercurialEye extends ItemMode implements IExtraFunction
{
	public MercurialEye() 
	{
		super("mercurial_eye", (byte) 4, new String[] {"Creation", "Extension", "Pillar Extension", "Transmutation"});
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(!world.isRemote)
		{
			player.addChatComponentMessage(new ChatComponentText("Sorry, still WIP! :P"));
		}
		return stack;
		/*MercurialEyeInventory inv = new MercurialEyeInventory(stack);
		ItemStack klein = inv.getKleinStack();
		ItemStack target = inv.getTargetStack();
		
		if (target == null)
			return stack;
		
		Block targetBlock = Block.getBlockFromItem(target.getItem());
		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
		
		if (mop == null || !mop.typeOfHit.equals(MovingObjectType.BLOCK))
			return stack;
		
		byte mode = this.GetMode(stack);
		int offset = 0;
		
		switch(this.GetCharge(stack))
		{
			case 0:
				offset = 0;
				break;
			case 1:
				offset = 1;
				break;
			case 2:
				offset = 4;
				break;
			case 3:
				offset = 8;
				break;
		}
		
		
		ForgeDirection direction = ForgeDirection.getOrientation(mop.sideHit);
		Coordinates coords = new Coordinates(mop);
		
		switch (mode)
		{
			case 0:
			{
				if (direction.offsetX != 0)
					coords.x += direction.offsetX;
				else if (direction.offsetY != 0)
					coords.y += direction.offsetY;
				else coords.z += direction.offsetZ;
				
				
				if (offset == 0)
				{
					if (world.getBlock (coords.x, coords.y, coords.z) == Blocks.air)
						changeBlock(player, world, coords, mop.sideHit, klein, target, targetBlock, true);
				}
				else
				{
					AxisAlignedBB bBox = getAABBFromDirection(player, direction, coords, offset, 0);
					System.out.println(bBox);
					for (int x = (int) bBox.minX; x <= bBox.maxX; x++)
						for (int y = (int) bBox.minY; y <= bBox.maxY; y++)
							for (int z = (int) bBox.minZ; z <= bBox.maxZ; z++)
							{
								if (world.getBlock(x, y, z) == Blocks.air)
								{
									changeBlock(player, world, new Coordinates(x, y, z), mop.sideHit, klein, target, targetBlock, true);
								}
							}
				}
			}
			
			default:
				return stack;
		}*/
	}
	
	private String getRelativeOrientation(Entity ent)
	{
		int dir = MathHelper.floor_double(ent.rotationPitch * 4.0F / 360.0F + 0.5D);
		
		if (dir == 1)
			return "DOWN";
		
		if (dir == -1)
			return "UP";
		
        return Direction.directions[MathHelper.floor_double(ent.rotationYaw * 4.0F / 360.0F + 0.5D) & 3];
	}
	
	private void changeBlock(EntityPlayer player, World world, Coordinates coords, int sideHit, ItemStack klein, ItemStack target, Block block, boolean useEmc)
	{
		if (useEmc)
		{
			if (klein == null)
				return;
			
			double kleinEmc = ItemBase.getEmc(klein);
			int targetEmc = Utils.getEmcValue(target);
			
			if (kleinEmc < targetEmc)
				return;
			
			this.removeEmc(klein, targetEmc);
		}
		
		world.setBlock(coords.x, coords.y, coords.z, block, target.getItemDamage(), 3);
		block.onBlockAdded(world, coords.x, coords.y, coords.z);
		block.onBlockPlaced(world, coords.x, coords.y, coords.z, sideHit, coords.x, coords.y, coords.z, target.getItemDamage());
		block.onBlockPlacedBy(world, coords.x, coords.y, coords.z, player, target);
		
		if (world.rand.nextInt(8) == 0)
		{
			MozeCore.pktHandler.sendToAllAround(new ParticlePKT("largesmoke", coords.x, coords.y + 1, coords.z), new TargetPoint(world.provider.dimensionId, coords.x, coords.y + 1, coords.z, 32));
		}
	}
	
	public AxisAlignedBB getAABBFromDirection(EntityPlayer player, ForgeDirection direction, Coordinates coords, int offset, int depth)
	{
		if (direction.offsetX != 0)
		{
			if (direction.offsetX > 0)
				return AxisAlignedBB.getBoundingBox(coords.x - depth, coords.y - offset, coords.z - offset, coords.x, coords.y + offset, coords.z + offset);
			else return AxisAlignedBB.getBoundingBox(coords.x, coords.y - offset, coords.z - offset, coords.x + depth, coords.y + offset, coords.z + offset);
		}
		else if (direction.offsetY != 0)
		{
			if (direction.offsetY > 0)
				return AxisAlignedBB.getBoundingBox(coords.x - offset, coords.y - depth, coords.z - offset, coords.x + offset, coords.y, coords.z + offset);
			else return AxisAlignedBB.getBoundingBox(coords.x - offset, coords.y, coords.z - offset, coords.x + offset, coords.y + depth, coords.z + offset);
		}
		else
		{
			if (direction.offsetZ > 0)
				return AxisAlignedBB.getBoundingBox(coords.x - offset, coords.y - offset, coords.z - depth, coords.x + offset, coords.y + offset, coords.z);
			else return AxisAlignedBB.getBoundingBox(coords.x - offset, coords.y - offset, coords.z, coords.x + offset, coords.y + offset, coords.z + depth);
		}
	}
	
	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player) 
	{
		player.openGui(MozeCore.instance, Constants.MERCURIAL_GUI, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) 
	{
		return 1; 
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        this.itemIcon = register.registerIcon(this.getTexture("mercurial_eye"));
    }
}
