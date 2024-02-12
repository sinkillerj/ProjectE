package moze_intel.projecte.gameObjs.items.rings;

import java.util.List;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.items.ICapabilityAware;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.gameObjs.items.IModeEnum;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.items.rings.Arcana.ArcanaMode;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Arcana extends ItemPE implements IItemMode<ArcanaMode>, IFlightProvider, IFireProtector, IExtraFunction, IProjectileShooter, ICapabilityAware {

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

	private void tick(ItemStack stack, Level level, ServerPlayer player) {
		if (stack.getData(PEAttachmentTypes.ACTIVE)) {
			switch (getMode(stack)) {
				case ZERO -> WorldHelper.freezeInBoundingBox(level, player.getBoundingBox().inflate(5), player, true);
				case IGNITION -> WorldHelper.igniteNearby(level, player);
				case HARVEST -> WorldHelper.growNearbyRandomly(true, level, player);
				case SWRG -> WorldHelper.repelEntitiesSWRG(level, player.getBoundingBox().inflate(5), player);
			}
		}
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isHeld) {
		super.inventoryTick(stack, level, entity, slot, isHeld);
		if (!level.isClientSide && hotBarOrOffHand(slot) && entity instanceof ServerPlayer player) {
			tick(stack, level, player);
		}
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		if (stack.getData(PEAttachmentTypes.ACTIVE)) {
			tooltips.add(getToolTip(stack));
		} else {
			tooltips.add(PELang.TOOLTIP_ARCANA_INACTIVE.translateColored(ChatFormatting.RED));
		}
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		if (!level.isClientSide) {
			ItemStack stack = player.getItemInHand(hand);
			stack.setData(PEAttachmentTypes.ACTIVE, !stack.getData(PEAttachmentTypes.ACTIVE));
		}
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		if (getMode(ctx.getItemInHand()) == ArcanaMode.IGNITION) {
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
		if (getMode(stack) == ArcanaMode.IGNITION) {
			switch (player.getDirection()) {
				case SOUTH, NORTH -> igniteNear(player, 30, 5, 3);
				case WEST, EAST -> igniteNear(player, 3, 5, 30);
			}
			level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		}
		return true;
	}

	private void igniteNear(Player player, int xOffset, int yOffset, int zOffset) {
		for (BlockPos pos : WorldHelper.getPositionsInBox(player.getBoundingBox().inflate(xOffset, yOffset, zOffset))) {
			if (player.level().isEmptyBlock(pos)) {
				PlayerHelper.checkedPlaceBlock(player, pos.immutable(), Blocks.FIRE.defaultBlockState());
			}
		}
	}

	@Override
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
		Level level = player.level();
		if (level.isClientSide) {
			return false;
		}
		SoundEvent sound = null;
		Projectile projectile = switch (getMode(stack)) {
			case ZERO -> {
				sound = SoundEvents.SNOWBALL_THROW;
				yield new Snowball(level, player);
			}
			case IGNITION -> {
				sound = PESoundEvents.POWER.get();
				yield new EntityFireProjectile(player, true, level);
			}
			case SWRG -> new EntitySWRGProjectile(player, true, level);
			default -> null;
		};
		if (projectile == null) {
			return false;
		}
		projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
		level.addFreshEntity(projectile);
		if (sound != null) {
			projectile.playSound(sound, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
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

	@Override
	public AttachmentType<ArcanaMode> getAttachmentType() {
		return PEAttachmentTypes.ARCANA_MODE.get();
	}

	public enum ArcanaMode implements IModeEnum<ArcanaMode> {
		ZERO(PELang.MODE_ARCANA_1),
		IGNITION(PELang.MODE_ARCANA_2),
		HARVEST(PELang.MODE_ARCANA_3),
		SWRG(PELang.MODE_ARCANA_4);

		private final IHasTranslationKey langEntry;

		ArcanaMode(IHasTranslationKey langEntry) {
			this.langEntry = langEntry;
		}

		@Override
		public String getTranslationKey() {
			return langEntry.getTranslationKey();
		}

		@Override
		public ArcanaMode next(ItemStack stack) {
			return switch (this) {
				case ZERO -> IGNITION;
				case IGNITION -> HARVEST;
				case HARVEST -> SWRG;
				case SWRG -> ZERO;
			};
		}
	}
}