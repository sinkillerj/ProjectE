package moze_intel.projecte.gameObjs.items.rings;

import java.util.List;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.items.ICapabilityAware;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Arcana extends ItemPE implements IItemMode, IFlightProvider, IFireProtector, IExtraFunction, IProjectileShooter, ICapabilityAware {

	private final static ILangEntry[] modes = new ILangEntry[]{
			PELang.MODE_ARCANA_1,
			PELang.MODE_ARCANA_2,
			PELang.MODE_ARCANA_3,
			PELang.MODE_ARCANA_4
	};

	public Arcana(Properties props) {
		super(props);
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack stack) {
		return stack.copy();
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return modes;
	}

	private void tick(ItemStack stack, Level level, ServerPlayer player) {
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			switch (getMode(stack)) {
				case 0 -> WorldHelper.freezeInBoundingBox(level, player.getBoundingBox().inflate(5), player, true);
				case 1 -> WorldHelper.igniteNearby(level, player);
				case 2 -> WorldHelper.growNearbyRandomly(true, level, player.blockPosition(), player);
				case 3 -> WorldHelper.repelEntitiesSWRG(level, player.getBoundingBox().inflate(5), player);
			}
		}
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slot, boolean held) {
		if (!level.isClientSide && slot < Inventory.getSelectionSize() && entity instanceof ServerPlayer player) {
			tick(stack, level, player);
		}
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			tooltips.add(getToolTip(stack));
		} else {
			tooltips.add(PELang.TOOLTIP_ARCANA_INACTIVE.translateColored(ChatFormatting.RED));
		}
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		if (!level.isClientSide) {
			CompoundTag compound = player.getItemInHand(hand).getOrCreateTag();
			compound.putBoolean(Constants.NBT_KEY_ACTIVE, !compound.getBoolean(Constants.NBT_KEY_ACTIVE));
		}
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	@NotNull
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
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, InteractionHand hand) {
		//GIANT FIRE ROW OF DEATH
		Level level = player.level();
		if (level.isClientSide) {
			return true;
		}
		if (getMode(stack) == 1) { // ignition
			switch (player.getDirection()) {
				case SOUTH, NORTH -> {
					for (BlockPos pos : BlockPos.betweenClosed(player.blockPosition().offset(-30, -5, -3), player.blockPosition().offset(30, 5, 3))) {
						if (level.isEmptyBlock(pos)) {
							PlayerHelper.checkedPlaceBlock((ServerPlayer) player, pos.immutable(), Blocks.FIRE.defaultBlockState());
						}
					}
				}
				case WEST, EAST -> {
					for (BlockPos pos : BlockPos.betweenClosed(player.blockPosition().offset(-3, -5, -30), player.blockPosition().offset(3, 5, 30))) {
						if (level.isEmptyBlock(pos)) {
							PlayerHelper.checkedPlaceBlock((ServerPlayer) player, pos.immutable(), Blocks.FIRE.defaultBlockState());
						}
					}
				}
			}
			level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		}
		return true;
	}

	@Override
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
		Level level = player.level();
		if (level.isClientSide) {
			return false;
		}
		switch (getMode(stack)) {
			case 0 -> { // zero
				Snowball snowball = new Snowball(level, player);
				snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
				level.addFreshEntity(snowball);
				snowball.playSound(SoundEvents.SNOWBALL_THROW, 1.0F, 1.0F);
			}
			case 1 -> { // ignition
				EntityFireProjectile fire = new EntityFireProjectile(player, true, level);
				fire.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
				level.addFreshEntity(fire);
				fire.playSound(PESoundEvents.POWER.get(), 1.0F, 1.0F);
			}
			case 3 -> { // swrg
				EntitySWRGProjectile lightning = new EntitySWRGProjectile(player, true, level);
				lightning.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
				level.addFreshEntity(lightning);
			}
		}
		return true;
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, Player player) {
		return true;
	}

	@Override
	public boolean canProvideFlight(ItemStack stack, Player player) {
		return true;
	}

	@Override
	public void attachCapabilities(RegisterCapabilitiesEvent event) {
		IntegrationHelper.registerCuriosCapability(event, this);
	}
}