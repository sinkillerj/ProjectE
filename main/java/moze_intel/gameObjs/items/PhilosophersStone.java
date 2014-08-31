package moze_intel.gameObjs.items;

import appeng.api.definitions.Blocks;
import moze_intel.MozeCore;
import moze_intel.config.ProjectEConfig;
import moze_intel.gameObjs.entity.MobRandomizer;
import moze_intel.network.packets.ParticlePKT;
import moze_intel.network.packets.SwingItemPKT;
import moze_intel.utils.Constants;
import moze_intel.utils.Coordinates;
import moze_intel.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.ZombieEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
		//TODO Add cool sound effects and fancing rendering for the transumtation block.
		if (world.isRemote)
		{
			if(ProjectEConfig.UseOldResources == false){
				
			}
			else{
				player.playSound(MozeCore.MODID + ":transmute_old", 0.5f, 1.0f);
			}
			return stack;
		}
		
		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);
		Block block = (mop == null || mop.typeOfHit != MovingObjectType.BLOCK) ? null : world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
		Block result = (block == null) ? null : Utils.getTransmutationResult(block, player.isSneaking());
		
		if (result != null)
		{
			Coordinates pos = new Coordinates(mop);
			int mode = this.getMode(stack);
			int charge = this.getCharge(stack);			
			ForgeDirection direction = ForgeDirection.getOrientation(mop.sideHit);
			
			if (mode == 0)
			{
				doWorldTransmutation(world, block, result, pos, 0, 0, charge);
			}
			else if (mode == 1)
			{
				getAxisOrientedPanel(direction, charge, block, result, pos, world);
			}
			else 
			{
				getAxisOrientedLine(direction, charge, block, result, pos, world, player);
			}
			
			MozeCore.pktHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
		}
		
		return stack;
	}
	
	private void getAxisOrientedPanel(ForgeDirection direction, int charge, Block pointed, Block result, Coordinates coords, World world)
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
	
	private void getAxisOrientedLine(ForgeDirection direction, int charge, Block pointed, Block result, Coordinates coords, World world, EntityPlayer player)
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
	private void doWorldTransmutation(World world, Block pointed, Block result, Coordinates coords, int type, int side, int charge)
	{
		if (type == 0)
			for (int i = coords.x - charge; i <= coords.x + charge; i++)
				for (int j = coords.y - charge; j <= coords.y + charge; j++)
					for (int k = coords.z - charge; k <= coords.z + charge; k++)
					{
						changeBlock(world, pointed, result, i, j, k);
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
	
	private void changeBlock(World world, Block pointed, Block result, int x, int y, int z)
	{
		if (world.getBlock(x, y, z) == pointed)
		{
			world.setBlock(x, y, z, result);
			
			if (world.rand.nextInt(8) == 0)
			{
				MozeCore.pktHandler.sendToAllAround(new ParticlePKT("largesmoke", x, y + 1, z), new TargetPoint(world.provider.dimensionId, x, y + 1, z, 32));
			}
		}
	}
	
	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack) 
	{
		World world = player.worldObj;
		world.spawnEntityInWorld(new MobRandomizer(world, player));
		//Sound Effect
		if(ProjectEConfig.UseOldResources==false){
			
		}else{
			player.playSound(MozeCore.MODID + ":philball_old", 1.0f, 1.0f);
		}
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		if(ProjectEConfig.UseOldResources == false){
		this.itemIcon = register.registerIcon(this.getTexture("philosophers_stone"));
		}
		else{
			this.itemIcon = register.registerIcon(this.getTexture("philosophers_stone_old"));
		}
	}

	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player) 
	{
		if (!player.worldObj.isRemote)
		{
			//player.openGui(MozeCore.instance, Constants.PHILOS_STONE_GUI, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
			player.addChatMessage(new ChatComponentText("This feature is not yet implemented. Please try again later."));
		}
	}
}
