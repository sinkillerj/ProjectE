package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class Arcana extends ItemPE implements IBauble, IModeChanger, IFlightProvider, IFireProtector, IExtraFunction, IProjectileShooter
{
	public Arcana()
	{
		setTranslationKey("arcana_ring");
		setMaxStackSize(1);
		setNoRepair();
		setContainerItem(this);
		addPropertyOverride(ACTIVE_NAME, ACTIVE_GETTER);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(CreativeTabs cTab, NonNullList<ItemStack> list)
	{
		if (isInCreativeTab(cTab))
		{
			for (byte i = 0; i < 4; ++i)
			{
				ItemStack stack = new ItemStack(this);
				ItemHelper.getOrCreateCompound(stack).setByte(TAG_MODE, i);
				list.add(stack);
			}
		}
	}

	@Override
	public byte getMode(@Nonnull ItemStack stack)
	{
		return ItemHelper.getOrCreateCompound(stack).getByte(TAG_MODE);
	}

	@Override
	public boolean changeMode(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
		byte newMode = (byte) ((ItemHelper.getOrCreateCompound(stack).getByte(TAG_MODE) + 1) % 4);
		stack.getTagCompound().setByte(TAG_MODE, newMode);
		return true;
	}
	
	private void tick(ItemStack stack, World world, EntityPlayerMP player)
	{
		if(ItemHelper.getOrCreateCompound(stack).getBoolean(TAG_ACTIVE))
		{
			switch(stack.getTagCompound().getByte(TAG_MODE))
			{
				case 0:
					WorldHelper.freezeInBoundingBox(world, player.getEntityBoundingBox().grow(5), player, true);
					break;
				case 1:
					WorldHelper.igniteNearby(world, player);
					break;
				case 2:
					WorldHelper.growNearbyRandomly(true, world, new BlockPos(player), player);
					break;
				case 3:
					WorldHelper.repelEntitiesInAABBFromPoint(world, player.getEntityBoundingBox().grow(5), player.posX, player.posY, player.posZ, true);
					break;
			}
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held)
	{
		if(world.isRemote || slot > 8 || !(entity instanceof EntityPlayerMP)) return;
		
		tick(stack, world, (EntityPlayerMP)entity);
	}

	@Override
	@Optional.Method(modid = "baubles")
	public BaubleType getBaubleType(ItemStack stack)
	{
		return BaubleType.RING;
	}

	@Override
	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase entity)
	{
		if(entity.getEntityWorld().isRemote || !(entity instanceof EntityPlayerMP)) return;
		
		tick(stack, entity.getEntityWorld(), (EntityPlayerMP)entity);
	}

	@Override
	@Optional.Method(modid = "baubles")
	public void onEquipped(ItemStack stack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "baubles")
	public void onUnequipped(ItemStack stack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "baubles")
	public boolean canEquip(ItemStack stack, EntityLivingBase player)
	{
		return true;
	}

	@Override
	@Optional.Method(modid = "baubles")
	public boolean canUnequip(ItemStack stack, EntityLivingBase player)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
	{
		if(stack.hasTagCompound())
		{
			if(!stack.getTagCompound().getBoolean(TAG_ACTIVE))
			{
				list.add(TextFormatting.RED + I18n.format("pe.arcana.inactive"));
			}
			else
			{
				list.add(I18n.format("pe.arcana.mode") + TextFormatting.AQUA + I18n.format("pe.arcana.mode." + stack.getTagCompound().getByte(TAG_MODE)));
			}
		}
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		if(!world.isRemote)
		{
			NBTTagCompound compound = ItemHelper.getOrCreateCompound(player.getHeldItem(hand));

			compound.setBoolean(TAG_ACTIVE, !compound.getBoolean(TAG_ACTIVE));
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, EnumHand hand) // GIANT FIRE ROW OF DEATH
	{
		World world = player.getEntityWorld();
		
		if(world.isRemote) return true;
		
		switch(ItemHelper.getOrCreateCompound(stack).getByte(TAG_MODE))
		{
			case 1: // ignition
				switch(player.getHorizontalFacing())
				{
					case SOUTH: // fall through
					case NORTH:
					{
						for (BlockPos pos : BlockPos.getAllInBoxMutable(player.getPosition().add(-30, -5, -3), player.getPosition().add(30, 5, 3)))
						{
							if (world.isAirBlock(pos))
							{
								PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), pos.toImmutable(), Blocks.FIRE.getDefaultState());
							}
						}
						break;
					}
					case WEST: // fall through
					case EAST:
					{
						for (BlockPos pos : BlockPos.getAllInBoxMutable(player.getPosition().add(-3, -5, -30), player.getPosition().add(3, 5, 30)))
						{
							if (world.isAirBlock(pos))
							{
								PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), pos.toImmutable(), Blocks.FIRE.getDefaultState());
							}
						}
						break;
					}
				}
				world.playSound(null, player.posX, player.posY, player.posZ, PESounds.POWER, SoundCategory.PLAYERS, 1.0F, 1.0F);
				break;
		}

		return true;
	}

	@Override
	public boolean shootProjectile(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
		World world = player.getEntityWorld();
		
		if(world.isRemote) return false;
		
		switch(ItemHelper.getOrCreateCompound(stack).getByte(TAG_MODE))
		{
			case 0: // zero
				EntitySnowball snowball = new EntitySnowball(world, player);
				snowball.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
				world.spawnEntity(snowball);
				snowball.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 1.0F, 1.0F);
				break;
			case 1: // ignition
				EntityFireProjectile fire = new EntityFireProjectile(world, player);
				fire.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
				world.spawnEntity(fire);
				fire.playSound(PESounds.POWER, 1.0F, 1.0F);
				break;
			case 3: // swrg
				EntitySWRGProjectile lightning = new EntitySWRGProjectile(world, player, true);
				lightning.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
				world.spawnEntity(lightning);
				break;
		}
		
		return true;
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, EntityPlayerMP player)
	{
		return true;
	}

	@Override
	public boolean canProvideFlight(ItemStack stack, EntityPlayerMP player)
	{
		return true;
	}
}
