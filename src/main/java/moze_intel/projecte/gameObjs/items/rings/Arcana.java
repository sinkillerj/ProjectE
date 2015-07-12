package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import moze_intel.projecte.api.IExtraFunction;
import moze_intel.projecte.api.IFireProtectionItem;
import moze_intel.projecte.api.IFlightItem;
import moze_intel.projecte.api.IModeChanger;
import moze_intel.projecte.api.IProjectileShooter;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class Arcana extends ItemPE implements IBauble, IModeChanger, IFlightItem, IFireProtectionItem, IExtraFunction, IProjectileShooter
{
	public Arcana()
	{
		super();
		setUnlocalizedName("arcana_ring");
		setMaxStackSize(1);
		setNoRepair();
		setContainerItem(this);
	}

	@Override
	public byte getMode(ItemStack stack)
	{
		return (byte)stack.getItemDamage();
	}

	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		stack.setItemDamage((stack.getItemDamage() + 1) % 4);
	}
	
	private void tick(ItemStack stack, World world, EntityPlayerMP player)
	{
		if(!player.capabilities.isCreativeMode)
		{
			if(!player.capabilities.allowFlying)
			{
				PlayerHelper.enableFlight(player);
				PlayerChecks.addPlayerFlyChecks(player);
			}
			
			if(!player.isImmuneToFire())
			{
				PlayerHelper.setPlayerFireImmunity(player, true);
				PlayerChecks.addPlayerFireChecks(player);
			}
		}

		if(stack.getTagCompound().getBoolean("Active"))
		{
			switch(stack.getItemDamage())
			{
				case 0:
					WorldHelper.freezeNearbyRandomly(world, player);
					break;
				case 1:
					WorldHelper.igniteNearby(world, player);
					break;
				case 2:
					WorldHelper.growNearbyRandomly(true, world, player);
					break;
				case 3:
					WorldHelper.repelEntitiesInAABBFromPoint(world, player.getEntityBoundingBox().expand(5, 5, 5), player.posX, player.posY, player.posZ, true);
					break;
			}
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held)
	{
		if(stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
		
		if(world.isRemote || slot > 8 || !(entity instanceof EntityPlayerMP)) return;
		
		tick(stack, world, (EntityPlayerMP)entity);
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public BaubleType getBaubleType(ItemStack stack)
	{
		return BaubleType.RING;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase entity)
	{
		if(stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
		
		if(entity.worldObj.isRemote || !(entity instanceof EntityPlayerMP)) return;
		
		tick(stack, entity.worldObj, (EntityPlayerMP)entity);
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onEquipped(ItemStack stack, EntityLivingBase player)
	{
		
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onUnequipped(ItemStack stack, EntityLivingBase player)
	{
		
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canEquip(ItemStack stack, EntityLivingBase player)
	{
		return true;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canUnequip(ItemStack stack, EntityLivingBase player)
	{
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
	{
		if(stack.hasTagCompound())
		{
			if(!stack.getTagCompound().getBoolean("Active"))
			{
				list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("pe.arcana.inactive"));
			}
			else
			{
				list.add(StatCollector.translateToLocal("pe.arcana.mode") + EnumChatFormatting.AQUA + StatCollector.translateToLocal("pe.arcana.mode." + stack.getItemDamage()));
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(!world.isRemote)
		{
			NBTTagCompound compound = stack.getTagCompound();
			
			compound.setBoolean("Active", !compound.getBoolean("Active"));
		}
		
		return stack;
	}

	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player) // GIANT FIRE ROW OF DEATH
	{
		World world = player.worldObj;
		
		if(world.isRemote) return;
		
		switch(stack.getItemDamage())
		{
			case 1: // ignition
				switch(player.getHorizontalFacing())
				{
					case SOUTH: // fall through
					case NORTH:
					{
						for (BlockPos pos : WorldHelper.getPositionsFromCorners(player.getPosition().add(-30, -5, -3), player.getPosition().add(30, 5, 3)))
						{
							if (world.isAirBlock(pos))
							{
								world.setBlockState(pos, Blocks.fire.getDefaultState());
							}
						}
						break;
					}
					case WEST: // fall through
					case EAST:
					{
						for (BlockPos pos : WorldHelper.getPositionsFromCorners(player.getPosition().add(-3, -5, -30), player.getPosition().add(3, 5, 30)))
						{
							if (world.isAirBlock(pos))
							{
								world.setBlockState(pos, Blocks.fire.getDefaultState());
							}
						}
						break;
					}
				}
				world.playSoundAtEntity(player, "projecte:item.pepower", 1.0F, 1.0F);
				break;
		}
	}

	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack)
	{
		World world = player.worldObj;
		
		if(world.isRemote) return false;
		
		switch(stack.getItemDamage())
		{
			case 0: // zero
				EntitySnowball snowball = new EntitySnowball(world, player);
				world.spawnEntityInWorld(snowball);
				world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F);
				break;
			case 1: // ignition
				EntityFireProjectile fire = new EntityFireProjectile(world, player);
				world.spawnEntityInWorld(fire);
				world.playSoundAtEntity(player, "projecte:item.pepower", 1.0F, 1.0F);
				break;
			case 3: // swrg
				EntitySWRGProjectile lightning = new EntitySWRGProjectile(world, player);
				world.spawnEntityInWorld(lightning);
				// world.playSoundAtEntity(player, "projecte:item.pewindmagic", 1.0F, 1.0F);
				break;
		}
		
		return true;
	}
}
