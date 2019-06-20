package moze_intel.projecte.gameObjs.items.rings;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.TNTBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Ignition extends RingToggle implements IPedestalItem, IFireProtector, IProjectileShooter
{
	public Ignition(Properties props)
	{
		super(props);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int inventorySlot, boolean held)
	{
		if (world.isRemote || inventorySlot > 8 || !(entity instanceof PlayerEntity)) return;
		
		super.inventoryTick(stack, world, entity, inventorySlot, held);
		ServerPlayerEntity player = (ServerPlayerEntity)entity;

        if (stack.getOrCreateTag().getBoolean(TAG_ACTIVE))
		{
			if (getEmc(stack) == 0 && !consumeFuel(player, stack, 64, false))
			{
				stack.getTag().putBoolean(TAG_ACTIVE, false);
			}
			else 
			{
				WorldHelper.igniteNearby(world, player);
				removeEmc(stack, 0.32F);
			}
		}
		else 
		{
			WorldHelper.extinguishNearby(world, player);
		}
	}

	@Override
	public boolean changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand)
	{
        CompoundNBT tag = stack.getOrCreateTag();
		tag.putBoolean(TAG_ACTIVE, !tag.getBoolean(TAG_ACTIVE));
		return true;
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof TNTBlock)
		{
			if (!world.isRemote && PlayerHelper.hasBreakPermission(((ServerPlayerEntity) ctx.getPlayer()), pos))
			{
				// Ignite TNT or derivatives
				// todo 1.14 doesn't work properly
				((TNTBlock) state.getBlock()).explode(world, pos);
				world.removeBlock(pos, false);
				world.playSound(null, ctx.getPlayer().posX, ctx.getPlayer().posY, ctx.getPlayer().posZ, PESounds.POWER, SoundCategory.PLAYERS, 1.0F, 1.0F);
			}

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.pedestalCooldown.ignition.get() != -1)
		{
			TileEntity te = world.getTileEntity(pos);
			if(!(te instanceof DMPedestalTile))
			{
				return;
			}
			DMPedestalTile tile = (DMPedestalTile) te;
			if (tile.getActivityCooldown() == 0)
			{
				List<MobEntity> list = world.getEntitiesWithinAABB(MobEntity.class, tile.getEffectBounds());
				for (MobEntity living : list)
				{
					living.attackEntityFrom(DamageSource.IN_FIRE, 3.0F);
					living.setFire(8);
				}

				tile.setActivityCooldown(ProjectEConfig.pedestalCooldown.ignition.get());
			}
			else
			{
				tile.decrementActivityCooldown();
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription()
	{
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.pedestalCooldown.ignition.get() != -1)
		{
			list.add(new TranslationTextComponent("pe.ignition.pedestal1").applyTextStyle(TextFormatting.BLUE));
			list.add(new TranslationTextComponent("pe.ignition.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.pedestalCooldown.ignition.get())).applyTextStyle(TextFormatting.BLUE));
		}
		return list;
	}
	
	@Override
	public boolean shootProjectile(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand)
	{
		World world = player.getEntityWorld();
		
		if(world.isRemote) return false;
		
		EntityFireProjectile fire = new EntityFireProjectile(player, world);
		fire.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
		world.addEntity(fire);
		
		return true;
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, ServerPlayerEntity player)
	{
		return true;
	}
}
