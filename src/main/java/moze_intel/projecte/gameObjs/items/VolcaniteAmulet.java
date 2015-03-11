package moze_intel.projecte.gameObjs.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.api.IProjectileShooter;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.KeyBinds;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidHandler;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class VolcaniteAmulet extends ItemPE implements IProjectileShooter, IBauble, IPedestalItem
{
	public VolcaniteAmulet()
	{
		this.setUnlocalizedName("volcanite_amulet");
		this.setMaxStackSize(1);
		this.setContainerItem(this);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int sideHit, float f1, float f2, float f3)
	{
		if (!world.isRemote)
		{
			TileEntity tile = world.getTileEntity(x, y, z);

			if (tile instanceof IFluidHandler)
			{
				IFluidHandler tank = (IFluidHandler) tile;

				if (Utils.canFillTank(tank, FluidRegistry.LAVA, sideHit))
				{
					int consumed = (int) Utils.consumePlayerFuel(player, 32);

					if (consumed != -1)
					{
						Utils.fillTank(tank, FluidRegistry.LAVA, sideHit, 1000 * (consumed / 32));
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
			if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			{
				int i = mop.blockX;
				int j = mop.blockY;
				int k = mop.blockZ;
				if (!(world.getTileEntity(i, j, k) instanceof IFluidHandler))
				{
					switch(mop.sideHit) // Ripped from vanilla ItemBucket and simplified
					{
						case 0: --j; break;
						case 1: ++j; break;
						case 2: --k; break;
						case 3: ++k; break;
						case 4: --i; break;
						case 5: ++i; break;
						default: break;
					}

					int consumed = (int) Utils.consumePlayerFuel(player, 32);
					if (consumed != -1)
					{
						placeLava(world, i, j, k);
					}
				}
			}
		}

		return stack;
	}


	private void placeLava(World world, int i, int j, int k)
	{
		Material material = world.getBlock(i, j, k).getMaterial();
		if (!world.isRemote && !material.isSolid() && !material.isLiquid())
		{
			world.func_147480_a(i, j, k, true);
		}
		world.setBlock(i, j, k, Blocks.flowing_lava, 0, 3);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean par5)
	{
		if (invSlot > 8 || !(entity instanceof EntityPlayer)) return;
		
		EntityPlayer player = (EntityPlayer) entity;

		int x = (int) Math.floor(player.posX);
		int y = (int) (player.posY - player.getYOffset());
		int z = (int) Math.floor(player.posZ);
		
		if ((world.getBlock(x, y - 1, z) == Blocks.lava || world.getBlock(x, y - 1, z) == Blocks.flowing_lava) && world.getBlock(x, y, z) == Blocks.air)
		{
			if (!player.isSneaking())
			{
				player.motionY = 0.0D;
				player.fallDistance = 0.0F;
				player.onGround = true;
			}
				
			if (!world.isRemote && player.capabilities.getWalkSpeed() < 0.25F)
			{
				Utils.setPlayerWalkSpeed(player, 0.25F);
			}
		}
		else if (!world.isRemote)
		{
			if (player.capabilities.getWalkSpeed() != Constants.PLAYER_WALK_SPEED)
			{
				Utils.setPlayerWalkSpeed(player, Constants.PLAYER_WALK_SPEED);
			}
		}
		
		if (!world.isRemote)
		{
			if (!player.isImmuneToFire())
			{
				Utils.setPlayerFireImmunity(player, true);
			}

			PlayerChecks.addPlayerFireChecks((EntityPlayerMP) player);
		}
	}
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack)
	{
		return false;
	}
	
	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack) 
	{
		if (Utils.consumePlayerFuel(player, 32) != -1)
		{
			player.worldObj.spawnEntityInWorld(new EntityLavaProjectile(player.worldObj, player));
			return true;
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("rings", "volcanite_amulet"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		if (KeyBinds.getExtraFuncKeyCode() >= 0 && KeyBinds.getExtraFuncKeyCode() < Keyboard.getKeyCount())
		{
			list.add("Press " + Keyboard.getKeyName(KeyBinds.getProjectileKeyCode()) + " to fire a lava projectile");
		}
		list.add("Acts as refilling lava bucket");
		list.add("Right-click to fill tanks");
		list.add("All operations cost 32 EMC!");
	}
	
	@Override
	@Optional.Method(modid = "Baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.AMULET;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase ent) 
	{
		if (!(ent instanceof EntityPlayer)) 
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) ent;
		World world = player.worldObj;

		int x = (int) Math.floor(player.posX);
		int y = (int) (player.posY - player.getYOffset());
		int z = (int) Math.floor(player.posZ);
		
		if ((world.getBlock(x, y - 1, z) == Blocks.lava || world.getBlock(x, y - 1, z) == Blocks.flowing_lava) && world.getBlock(x, y, z) == Blocks.air)
		{
			if (!player.isSneaking())
			{
				player.motionY = 0.0D;
				player.fallDistance = 0.0F;
				player.onGround = true;
			}
				
			if (!world.isRemote && player.capabilities.getWalkSpeed() < 0.25F)
			{
				Utils.setPlayerWalkSpeed(player, 0.25F);
			}
		}
		else if (!world.isRemote)
		{
			if (player.capabilities.getWalkSpeed() != Constants.PLAYER_WALK_SPEED)
			{
				Utils.setPlayerWalkSpeed(player, Constants.PLAYER_WALK_SPEED);
			}
		}
		
		if (!world.isRemote && !player.isImmuneToFire())
		{
			Utils.setPlayerFireImmunity(player, true);
		}
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) 
	{
		if (player instanceof EntityPlayer && !player.worldObj.isRemote)
		{
			Utils.setPlayerFireImmunity((EntityPlayer) player, false);
		}
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	public void updateInPedestal(World world, int x, int y, int z)
	{
		if (!world.isRemote && world.getWorldTime() % 20 == 0)
		{
			world.getWorldInfo().setRainTime(0);
			world.getWorldInfo().setThunderTime(0);
			world.getWorldInfo().setRaining(false);
			world.getWorldInfo().setThundering(false);
		}
	}

	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = new ArrayList<String>();
		list.add("Prevents rain/snow storms");
		return list;
	}
}
