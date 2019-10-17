package moze_intel.projecte.gameObjs.items.rings;

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
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

public class Arcana extends ItemPE implements IModeChanger, IFlightProvider, IFireProtector, IExtraFunction, IProjectileShooter
{
	public Arcana(Properties props)
	{
		super(props);
		addPropertyOverride(ACTIVE_NAME, ACTIVE_GETTER);
		addPropertyOverride(new ResourceLocation(PECore.MODID, "mode"), MODE_GETTER);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack)
	{
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack)
	{
		return stack.copy();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> list)
	{
		if (isInGroup(group))
		{
			for (byte i = 0; i < 4; ++i)
			{
				ItemStack stack = new ItemStack(this);
                stack.getOrCreateTag().putByte(TAG_MODE, i);
				list.add(stack);
			}
		}
	}

	@Override
	public byte getMode(@Nonnull ItemStack stack)
	{
        return stack.getOrCreateTag().getByte(TAG_MODE);
	}

	@Override
	public boolean changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand)
	{
        byte newMode = (byte) ((stack.getOrCreateTag().getByte(TAG_MODE) + 1) % 4);
		stack.getTag().putByte(TAG_MODE, newMode);
		return true;
	}
	
	private void tick(ItemStack stack, World world, ServerPlayerEntity player)
	{
        if(stack.getOrCreateTag().getBoolean(TAG_ACTIVE))
		{
			switch(stack.getTag().getByte(TAG_MODE))
			{
				case 0:
					WorldHelper.freezeInBoundingBox(world, player.getBoundingBox().grow(5), player, true);
					break;
				case 1:
					WorldHelper.igniteNearby(world, player);
					break;
				case 2:
					WorldHelper.growNearbyRandomly(true, world, new BlockPos(player), player);
					break;
				case 3:
					WorldHelper.repelEntitiesInAABBFromPoint(world, player.getBoundingBox().grow(5), player.posX, player.posY, player.posZ, true);
					break;
			}
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean held)
	{
		if(world.isRemote || slot > 8 || !(entity instanceof ServerPlayerEntity)) return;
		
		tick(stack, world, (ServerPlayerEntity)entity);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flags)
	{
		if(stack.hasTag())
		{
			if(!stack.getTag().getBoolean(TAG_ACTIVE))
			{
				list.add(new TranslationTextComponent("pe.arcana.inactive").setStyle(new Style().setColor(TextFormatting.RED)));
			}
			else
			{
				list.add(new TranslationTextComponent("pe.arcana.mode")
						.appendSibling(new TranslationTextComponent(I18n.format("pe.arcana.mode." + stack.getTag().getByte(TAG_MODE))).setStyle(new Style().setColor(TextFormatting.AQUA))));
			}
		}
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand)
	{
		if(!world.isRemote)
		{
            CompoundNBT compound = player.getHeldItem(hand).getOrCreateTag();

			compound.putBoolean(TAG_ACTIVE, !compound.getBoolean(TAG_ACTIVE));
		}
		
		return ActionResult.newResult(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand) // GIANT FIRE ROW OF DEATH
	{
		World world = player.getEntityWorld();
		
		if(world.isRemote) return true;

        switch(stack.getOrCreateTag().getByte(TAG_MODE))
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
								PlayerHelper.checkedPlaceBlock(((ServerPlayerEntity) player), pos.toImmutable(), Blocks.FIRE.getDefaultState());
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
								PlayerHelper.checkedPlaceBlock(((ServerPlayerEntity) player), pos.toImmutable(), Blocks.FIRE.getDefaultState());
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
	public boolean shootProjectile(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand)
	{
		World world = player.getEntityWorld();
		
		if(world.isRemote) return false;

        switch(stack.getOrCreateTag().getByte(TAG_MODE))
		{
			case 0: // zero
				SnowballEntity snowball = new SnowballEntity(world, player);
				snowball.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
				world.addEntity(snowball);
				snowball.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 1.0F, 1.0F);
				break;
			case 1: // ignition
				EntityFireProjectile fire = new EntityFireProjectile(player, world);
				fire.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
				world.addEntity(fire);
				fire.playSound(PESounds.POWER, 1.0F, 1.0F);
				break;
			case 3: // swrg
				EntitySWRGProjectile lightning = new EntitySWRGProjectile(player, true, world);
				lightning.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
				world.addEntity(lightning);
				break;
		}
		
		return true;
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, ServerPlayerEntity player)
	{
		return true;
	}

	@Override
	public boolean canProvideFlight(ItemStack stack, ServerPlayerEntity player)
	{
		return true;
	}
}
