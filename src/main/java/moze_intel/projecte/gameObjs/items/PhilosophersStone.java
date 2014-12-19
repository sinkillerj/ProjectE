package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.IExtraFunction;
import moze_intel.projecte.api.IProjectileShooter;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.tiles.TileEmc;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ParticlePKT;
import moze_intel.projecte.network.packets.SwingItemPKT;
import moze_intel.projecte.utils.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class PhilosophersStone extends ItemMode implements IProjectileShooter, IExtraFunction
{
	public PhilosophersStone()
	{
		super("philosophers_stone", (byte) 4, new String[] {"Cube", "Panel", "Line"});
		this.setContainerItem(this);
	}
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack)
	{
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote)
		{
			return stack;
		}
		
		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);
		
		if (mop == null)
		{
			return stack;
		}
		
		MetaBlock mBlock = new MetaBlock(world, mop.blockX, mop.blockY, mop.blockZ);

		if (mBlock.getBlock() != Blocks.air)
		{
			TileEntity tile = world.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
			
			if (player.isSneaking())
			{
				if (tile instanceof TileEmc)
				{
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setBoolean("ProjectEBlock", true);
					tile.writeToNBT(nbt);
					
					if (mBlock.getBlock() == ObjHandler.dmFurnaceOn)
					{
						mBlock.setBlock(ObjHandler.dmFurnaceOff);
					}
					else if (mBlock.getBlock() == ObjHandler.rmFurnaceOn)
					{
						mBlock.setBlock(ObjHandler.rmFurnaceOff);
					}
					
					ItemStack s = mBlock.toItemStack();
					
					if (s.getHasSubtypes())
					{
						s.setItemDamage(world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ));
					}
					else
					{
						s.setItemDamage(0);
					}
					
					s.setTagCompound(nbt);
					
					world.removeTileEntity(mop.blockX, mop.blockY, mop.blockZ);
					world.setBlock(mop.blockX, mop.blockY, mop.blockZ, Blocks.air, 0, 2);
					Utils.spawnEntityItem(world, s, mop.blockX, mop.blockY, mop.blockZ);
				}
			}
		}

		MetaBlock result = WorldTransmutations.getWorldTransmutation(world, mop.blockX, mop.blockY, mop.blockZ, player.isSneaking());

		if (result != null)
		{
			Coordinates pos = new Coordinates(mop);
			int mode = this.getMode(stack);
			int charge = this.getCharge(stack);			
			ForgeDirection direction = ForgeDirection.getOrientation(mop.sideHit);
			
			if (mode == 0)
			{
				doWorldTransmutation(world, mBlock, result, pos, 0, 0, charge);
			}
			else if (mode == 1)
			{
				getAxisOrientedPanel(direction, charge, mBlock, result, pos, world);
			}
			else 
			{
				getAxisOrientedLine(direction, charge, mBlock, result, pos, world, player);
			}
			
			PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
		}
		
		return stack;
	}
	
	private void getAxisOrientedPanel(ForgeDirection direction, int charge, MetaBlock pointed, MetaBlock result, Coordinates coords, World world)
	{
		int side;
		
		if (direction.offsetY != 0)
		{
			side = 0;
		}
		else if (direction.offsetX != 0)
		{
			side = 1;
		}
		else
		{
			side = 2;
		}
		
		doWorldTransmutation(world, pointed, result, coords, 1, side, charge);
	}
	
	private void getAxisOrientedLine(ForgeDirection direction, int charge, MetaBlock pointed, MetaBlock result, Coordinates coords, World world, EntityPlayer player)
	{
		int side;
		
		if (direction.offsetX != 0)
		{
			side = 0;
		}
		else if (direction.offsetZ != 0)
		{
			side = 1;
		}
		else
		{
			String dir = Direction.directions[MathHelper.floor_double((double)((player.rotationYaw * 4F) / 360F) + 0.5D) & 3];
			
			if (dir.equals("NORTH") || dir.equals("SOUTH"))
			{
				side = 0;
			}
			else
			{
				side = 1;
			}
		}
		
		doWorldTransmutation(world, pointed, result, coords, 2, side, charge);
	}
	
	/**
	 * type 0 = cube, type 1 = panel, type 2 = line
	 */
	private void doWorldTransmutation(World world, MetaBlock pointed, MetaBlock result, Coordinates coords, int type, int side, int charge)
	{
		if (type == 0)
		{
			for (int i = coords.x - charge; i <= coords.x + charge; i++)
				for (int j = coords.y - charge; j <= coords.y + charge; j++)
					for (int k = coords.z - charge; k <= coords.z + charge; k++)
					{
						changeBlock(world, pointed, result, i, j, k);
					}
		}
		else if (type == 1)
		{
			if (side == 0)
			{
				for (int i = coords.x - charge; i <= coords.x + charge; i++)
					for (int j = coords.z - charge; j <= coords.z + charge; j++)
					{
						changeBlock(world, pointed, result, i, coords.y, j);
					}
			}
			else if (side == 1)
			{
				for (int i = coords.y - charge; i <= coords.y + charge; i++)
					for (int j = coords.z - charge; j <= coords.z + charge; j++)
					{
						changeBlock(world, pointed, result, coords.x, i, j);
					}
			}
			else
			{
				for (int i = coords.x - charge; i <= coords.x + charge; i++)
					for (int j = coords.y - charge; j <= coords.y + charge; j++)
					{
						changeBlock(world, pointed, result, i, j, coords.z);
					}
			}
		}
		else
		{
			if (side == 0)
			{
				for (int i = coords.z - charge; i <= coords.z + charge; i++)
				{
					changeBlock(world, pointed, result, coords.x, coords.y, i);
				}
			}
			else 
			{
				for (int i = coords.x - charge; i <= coords.x + charge; i++)
				{
					changeBlock(world, pointed, result, i, coords.y, coords.z);
				}
			}
		}
	}
	
	private void changeBlock(World world, MetaBlock pointed, MetaBlock result, int x, int y, int z)
	{
		MetaBlock block = new MetaBlock(world, x, y, z);

		if (block.equals(pointed))
		{
			result.setInWorld(world, x, y, z);

			if (world.rand.nextInt(8) == 0)
			{
				PacketHandler.sendToAllAround(new ParticlePKT("largesmoke", x, y + 1, z), new TargetPoint(world.provider.dimensionId, x, y + 1, z, 32));
			}
		}
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) 
	{
		super.onCreated(stack, world, player);
		
		if (!world.isRemote)
		{
			player.addStat(AchievementHandler.PHIL_STONE, 1);
		}
	}
	
	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack) 
	{
		World world = player.worldObj;
		world.spawnEntityInWorld(new EntityMobRandomizer(world, player));
		return true;
	}
	
	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player) 
	{
		if (!player.worldObj.isRemote)
		{
			player.openGui(PECore.instance, Constants.PHILOS_STONE_GUI, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) 
	{
		if (KeyBinds.getExtraFuncKeyCode() >= 0 && KeyBinds.getExtraFuncKeyCode() < Keyboard.getKeyCount())
		{
			list.add("Press " + Keyboard.getKeyName(KeyBinds.getExtraFuncKeyCode()) + " to open the crafting grid.");
		}
		
		list.add("Acts like a wrench for ProjectE blocks.");
		list.add("Left clicking changes the block's orientation.");
		list.add("Shift right clicking will pick up the block.");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("philosophers_stone"));
	}
}
