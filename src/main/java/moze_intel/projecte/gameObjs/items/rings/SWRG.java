package moze_intel.projecte.gameObjs.items.rings;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.IntFunction;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.items.ICapabilityAware;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.LevelHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SWRG extends ItemPE implements IPedestalItem, IProjectileShooter, ICapabilityAware {

	public SWRG(Properties props) {
		super(props.component(PEDataComponentTypes.SWRG_MODE, SWRGMode.OFF)
				.component(PEDataComponentTypes.STORED_EMC, 0L)
				.component(PEDataComponentTypes.UNPROCESSED_EMC, 0.0)
		);
	}

	private void tick(ItemStack stack, Player player) {
		SWRGMode mode = getMode(stack);
		if (mode.hasShield()) {
			// Repel on both sides - smooth animation
			WorldHelper.repelEntitiesSWRG(player.level(), player.getBoundingBox().inflate(5), player);
		}
		if (player.level().isClientSide()) {
			return;
		}
		if (!hasEmc(player, stack, 64, true)) {
			//If it is already off changeMode will just NO-OP
			changeMode(player, stack, mode, SWRGMode.OFF);
			return;
		}

		if (player.getAbilities().flying) {
			if (!mode.hasFlight()) {
				mode = changeMode(player, stack, mode, mode.toggleFlight());
			}
		} else if (mode.hasFlight()) {
			mode = changeMode(player, stack, mode, mode.toggleFlight());
		}

		float toRemove = mode.hasShield() ? 0.32F : 0;
		if (mode.hasFlight() && player.getAbilities().flying) {
			toRemove += 0.32F;
		}

		removeEmc(stack, toRemove);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
		super.inventoryTick(stack, level, entity, slot);
		if (hotBarOrOffHand(slot) && entity instanceof Player player) {
			tick(stack, player);
		}
	}

	private SWRGMode getMode(ItemStack stack) {
		return stack.getOrDefault(PEDataComponentTypes.SWRG_MODE, SWRGMode.OFF);
	}

	@NotNull
	@Override
	public InteractionResult use(Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			SWRGMode oldMode = getMode(stack);
			changeMode(player, stack, oldMode, oldMode.next());
		}
		return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
	}

	private SWRGMode changeMode(Player player, ItemStack stack, SWRGMode oldMode, SWRGMode mode) {
		if (mode == oldMode) {
			return mode;
		}
		stack.set(PEDataComponentTypes.SWRG_MODE, mode);
		if (player == null) {
			//Don't do sounds if the player is null
			return mode;
		}
		if (mode == SWRGMode.OFF || oldMode == SWRGMode.SHIELDED_FLIGHT) {
			//At least one mode deactivated
			player.level().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.UNCHARGE.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
		} else if (oldMode == SWRGMode.OFF || mode == SWRGMode.SHIELDED_FLIGHT) {
			//At least one mode activated
			player.level().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.HEAL.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
		}
		//Doesn't handle going from mode 1 to 2 or 2 to 1
		return mode;
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return false;
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		if (!level.isClientSide() && ProjectEConfig.server.cooldown.pedestal.swrg.get() != -1) {
			if (pedestal.getActivityCooldown() <= 0) {
				for (Mob living : level.getEntitiesOfClass(Mob.class, pedestal.getEffectBounds(),
						ent -> !ent.isSpectator() && (!(ent instanceof TamableAnimal tamableAnimal) || !tamableAnimal.isTame()))) {
					if (level instanceof ServerLevel serverLevel) {
						LightningBolt lightning = LevelHelper.createLightning(serverLevel, living.position());
						level.addFreshEntity(lightning);
					}
				}
				pedestal.setActivityCooldown(level, pos, ProjectEConfig.server.cooldown.pedestal.swrg.get());
			} else {
				pedestal.decrementActivityCooldown(level, pos);
			}
		}
		return false;
	}

	@NotNull
	@Override
	public List<Component> getPedestalDescription(float tickRate) {
		List<Component> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.swrg.get() != -1) {
			list.add(PELang.PEDESTAL_SWRG_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_SWRG_2.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.swrg.get(), tickRate)));
		}
		return list;
	}

	@Override
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
		EntitySWRGProjectile projectile = new EntitySWRGProjectile(player, false, player.level());
		projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
		player.level().addFreshEntity(projectile);
		return true;
	}

	@Override
	public void attachCapabilities(RegisterCapabilitiesEvent event) {
		IntegrationHelper.registerCuriosCapability(event, this);
	}

	public enum SWRGMode implements StringRepresentable {//Change the mode of SWRG. Modes:<p> 0 = Ring Off<p> 1 = Flight<p> 2 = Shield<p> 3 = Flight + Shield<p>
		OFF,
		FLIGHT,
		SHIELD,
		SHIELDED_FLIGHT;

		public static final Codec<SWRGMode> CODEC = StringRepresentable.fromEnum(SWRGMode::values);
		public static final IntFunction<SWRGMode> BY_ID = ByIdMap.continuous(SWRGMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
		public static final StreamCodec<ByteBuf, SWRGMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, SWRGMode::ordinal);

		private final String serializedName;

		SWRGMode() {
			this.serializedName = name().toLowerCase(Locale.ROOT);
		}

		@NotNull
		@Override
		public String getSerializedName() {
			return serializedName;
		}

		public boolean hasFlight() {
			return this == FLIGHT || this == SHIELDED_FLIGHT;
		}

		public boolean hasShield() {
			return this == SHIELD || this == SHIELDED_FLIGHT;
		}

		public SWRGMode toggleFlight() {
			return switch (this) {
				case OFF -> FLIGHT;
				case SHIELD -> SHIELDED_FLIGHT;
				case FLIGHT -> OFF;
				case SHIELDED_FLIGHT -> SHIELD;
			};
		}

		public SWRGMode next() {
			return switch (this) {
				case OFF -> SHIELD;
				case SHIELD -> OFF;
				case FLIGHT -> SHIELDED_FLIGHT;
				case SHIELDED_FLIGHT -> FLIGHT;
			};
		}
	}
}
