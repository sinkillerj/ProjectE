package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.tiles.TileEmc;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ParticlePKT;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.Coordinates;
import moze_intel.projecte.utils.MetaBlock;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class PhilosophersStone extends ItemMode implements IProjectileShooter, IExtraFunction
{
	public PhilosophersStone()
	{
		super("philosophers_stone", (byte)4, new String[] {
				StatCollector.translateToLocal("pe.philstone.mode1"),
				StatCollector.translateToLocal("pe.philstone.mode2"),
				StatCollector.translateToLocal("pe.philstone.mode3")});
		this.setContainerItem(this);
		this.setNoRepair();
	}
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack)
	{
		return false;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int blockX, int blockY, int blockZ, int sideHit, float px, float py, float pz)
	{
		if (world.isRemote)
		{
			return false;
		}

		MetaBlock mBlock = new MetaBlock(world, blockX, blockY, blockZ);

		MetaBlock result = WorldTransmutations.getWorldTransmutation(world, blockX, blockY, blockZ, player.isSneaking());

		if (result != null)
		{
			Coordinates pos = new Coordinates(blockX, blockY,blockZ);
			int mode = this.getMode(stack);
			int charge = this.getCharge(stack);			
			ForgeDirection direction = ForgeDirection.getOrientation(sideHit);
			
			if (mode == 0)
			{
				doWorldTransmutation(world, mBlock, result, pos, 0, 0, charge, player);
			}
			else if (mode == 1)
			{
				getAxisOrientedPanel(direction, charge, mBlock, result, pos, world, player);
			}
			else 
			{
				getAxisOrientedLine(direction, charge, mBlock, result, pos, world, player);
			}

			world.playSoundAtEntity(player, "projecte:item.petransmute", 1.0F, 1.0F);

			PlayerHelper.swingItem(player);
		}
		
		return true;
	}
	
	private void getAxisOrientedPanel(ForgeDirection direction, int charge, MetaBlock pointed, MetaBlock result, Coordinates coords, World world, EntityPlayer player)
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
		
		doWorldTransmutation(world, pointed, result, coords, 1, side, charge, player);
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
		
		doWorldTransmutation(world, pointed, result, coords, 2, side, charge, player);
	}
	
	/**
	 * type 0 = cube, type 1 = panel, type 2 = line
	 */
	private void doWorldTransmutation(World world, MetaBlock pointed, MetaBlock result, Coordinates coords, int type, int side, int charge, EntityPlayer player)
	{
		if (type == 0)
		{
			for (int i = coords.x - charge; i <= coords.x + charge; i++)
				for (int j = coords.y - charge; j <= coords.y + charge; j++)
					for (int k = coords.z - charge; k <= coords.z + charge; k++)
					{
						changeBlock(world, pointed, result, i, j, k, player);
					}
		}
		else if (type == 1)
		{
			if (side == 0)
			{
				for (int i = coords.x - charge; i <= coords.x + charge; i++)
					for (int j = coords.z - charge; j <= coords.z + charge; j++)
					{
						changeBlock(world, pointed, result, i, coords.y, j, player);
					}
			}
			else if (side == 1)
			{
				for (int i = coords.y - charge; i <= coords.y + charge; i++)
					for (int j = coords.z - charge; j <= coords.z + charge; j++)
					{
						changeBlock(world, pointed, result, coords.x, i, j, player);
					}
			}
			else
			{
				for (int i = coords.x - charge; i <= coords.x + charge; i++)
					for (int j = coords.y - charge; j <= coords.y + charge; j++)
					{
						changeBlock(world, pointed, result, i, j, coords.z, player);
					}
			}
		}
		else
		{
			if (side == 0)
			{
				for (int i = coords.z - charge; i <= coords.z + charge; i++)
				{
					changeBlock(world, pointed, result, coords.x, coords.y, i, player);
				}
			}
			else 
			{
				for (int i = coords.x - charge; i <= coords.x + charge; i++)
				{
					changeBlock(world, pointed, result, i, coords.y, coords.z, player);
				}
			}
		}
	}
	
	private void changeBlock(World world, MetaBlock pointed, MetaBlock result, int x, int y, int z, EntityPlayer player)
	{
		MetaBlock block = new MetaBlock(world, x, y, z);

		if (block.equals(pointed))
		{
			PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), x, y, z, result.getBlock(), result.getMeta());
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
		world.playSoundAtEntity(player, "projecte:item.petransmute", 1.0F, 1.0F);
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
		list.add(String.format(StatCollector.translateToLocal("pe.philstone.tooltip1"), ClientKeyHelper.getKeyName(PEKeybind.EXTRA_FUNCTION)));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("philosophers_stone"));
	}
}
