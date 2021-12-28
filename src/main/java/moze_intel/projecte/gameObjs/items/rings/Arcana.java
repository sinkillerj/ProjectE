package moze_intel.projecte.gameObjs.items.rings;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class Arcana extends ItemPE implements IItemMode, IFlightProvider, IFireProtector, IExtraFunction, IProjectileShooter {

	private final static ILangEntry[] modes = new ILangEntry[]{
			PELang.MODE_ARCANA_1,
			PELang.MODE_ARCANA_2,
			PELang.MODE_ARCANA_3,
			PELang.MODE_ARCANA_4
	};

	public Arcana(Properties props) {
		super(props);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return stack.copy();
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> list) {
		//Only used on the client
		if (allowdedIn(group)) {
			for (byte i = 0; i < getModeCount(); ++i) {
				ItemStack stack = new ItemStack(this);
				stack.getOrCreateTag().putByte(Constants.NBT_KEY_MODE, i);
				list.add(stack);
			}
		}
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return modes;
	}

	private void tick(ItemStack stack, Level world, ServerPlayer player) {
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			switch (getMode(stack)) {
				case 0 -> WorldHelper.freezeInBoundingBox(world, player.getBoundingBox().inflate(5), player, true);
				case 1 -> WorldHelper.igniteNearby(world, player);
				case 2 -> WorldHelper.growNearbyRandomly(true, world, player.blockPosition(), player);
				case 3 -> WorldHelper.repelEntitiesSWRG(world, player.getBoundingBox().inflate(5), player);
			}
		}
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, Level world, @Nonnull Entity entity, int slot, boolean held) {
		if (!world.isClientSide && slot < Inventory.getSelectionSize() && entity instanceof ServerPlayer player) {
			tick(stack, world, player);
		}
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flags) {
		super.appendHoverText(stack, world, tooltips, flags);
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			tooltips.add(getToolTip(stack));
		} else {
			tooltips.add(PELang.TOOLTIP_ARCANA_INACTIVE.translateColored(ChatFormatting.RED));
		}
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(@Nonnull Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
		if (!world.isClientSide) {
			CompoundTag compound = player.getItemInHand(hand).getOrCreateTag();
			compound.putBoolean(Constants.NBT_KEY_ACTIVE, !compound.getBoolean(Constants.NBT_KEY_ACTIVE));
		}
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	@Nonnull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		if (getMode(ctx.getItemInHand()) == 1) {
			InteractionResult result = WorldHelper.igniteBlock(ctx);
			if (result != InteractionResult.PASS) {
				return result;
			}
		}
		return super.useOn(ctx);
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull Player player, InteractionHand hand) {
		//GIANT FIRE ROW OF DEATH
		Level world = player.getCommandSenderWorld();
		if (world.isClientSide) {
			return true;
		}
		if (getMode(stack) == 1) { // ignition
			switch (player.getDirection()) {
				case SOUTH: // fall through
				case NORTH:
					for (BlockPos pos : BlockPos.betweenClosed(player.blockPosition().offset(-30, -5, -3), player.blockPosition().offset(30, 5, 3))) {
						if (world.isEmptyBlock(pos)) {
							PlayerHelper.checkedPlaceBlock((ServerPlayer) player, pos.immutable(), Blocks.FIRE.defaultBlockState());
						}
					}
					break;
				case WEST: // fall through
				case EAST:
					for (BlockPos pos : BlockPos.betweenClosed(player.blockPosition().offset(-3, -5, -30), player.blockPosition().offset(3, 5, 30))) {
						if (world.isEmptyBlock(pos)) {
							PlayerHelper.checkedPlaceBlock((ServerPlayer) player, pos.immutable(), Blocks.FIRE.defaultBlockState());
						}
					}
					break;
			}
			world.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		}
		return true;
	}

	@Override
	public boolean shootProjectile(@Nonnull Player player, @Nonnull ItemStack stack, InteractionHand hand) {
		Level world = player.getCommandSenderWorld();
		if (world.isClientSide) {
			return false;
		}
		switch (getMode(stack)) {
			case 0 -> { // zero
				Snowball snowball = new Snowball(world, player);
				snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
				world.addFreshEntity(snowball);
				snowball.playSound(SoundEvents.SNOWBALL_THROW, 1.0F, 1.0F);
			}
			case 1 -> { // ignition
				EntityFireProjectile fire = new EntityFireProjectile(player, world);
				fire.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
				world.addFreshEntity(fire);
				fire.playSound(PESoundEvents.POWER.get(), 1.0F, 1.0F);
			}
			case 3 -> { // swrg
				EntitySWRGProjectile lightning = new EntitySWRGProjectile(player, true, world);
				lightning.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
				world.addFreshEntity(lightning);
			}
		}
		return true;
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, ServerPlayer player) {
		return true;
	}

	@Override
	public boolean canProvideFlight(ItemStack stack, ServerPlayer player) {
		return true;
	}
}