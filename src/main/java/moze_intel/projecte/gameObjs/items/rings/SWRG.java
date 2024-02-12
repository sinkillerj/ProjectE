package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.items.ICapabilityAware;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

public class SWRG extends ItemPE implements IPedestalItem, IFlightProvider, IProjectileShooter, ICapabilityAware {

	public SWRG(Properties props) {
		super(props);
	}

	private void tick(ItemStack stack, Player player) {
		SWRGMode mode = getMode(stack);
		if (mode.hasShield()) {
			// Repel on both sides - smooth animation
			WorldHelper.repelEntitiesSWRG(player.level(), player.getBoundingBox().inflate(5), player);
		}
		if (player.level().isClientSide) {
			return;
		}
		if (getEmc(stack) == 0 && !consumeFuel(player, stack, 64, false)) {
			//If it is already off changeMode will just NO-OP
			changeMode(player, stack, mode, SWRGMode.OFF);
			if (player.getAbilities().mayfly) {
				player.getData(PEAttachmentTypes.INTERNAL_ABILITIES).disableSwrgFlightOverride();
			}
			return;
		}

		if (!player.getAbilities().mayfly) {
			player.getData(PEAttachmentTypes.INTERNAL_ABILITIES).enableSwrgFlightOverride();
		}

		if (player.getAbilities().flying) {
			if (!mode.hasFlight()) {
				mode = changeMode(player, stack, mode, mode == SWRGMode.OFF ? SWRGMode.FLIGHT : SWRGMode.SHIELDED_FLIGHT);
			}
		} else if (mode.hasFlight()) {
			mode = changeMode(player, stack, mode, mode == SWRGMode.FLIGHT ? SWRGMode.OFF : SWRGMode.SHIELD);
		}

		float toRemove = 0;

		if (player.getAbilities().flying) {
			toRemove = 0.32F;
		}

		if (mode == SWRGMode.SHIELD) {
			toRemove = 0.32F;
		} else if (mode == SWRGMode.SHIELDED_FLIGHT) {
			toRemove = 0.64F;
		}

		removeEmc(stack, EMCHelper.removeFractionalEMC(stack, toRemove));

		player.fallDistance = 0;
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isHeld) {
		super.inventoryTick(stack, level, entity, slot, isHeld);
		if (hotBarOrOffHand(slot) && entity instanceof Player player) {
			tick(stack, player);
		}
	}

	private SWRGMode getMode(ItemStack stack) {
		return stack.getData(PEAttachmentTypes.SWRG_MODE);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide) {
			SWRGMode oldMode = getMode(stack);
			changeMode(player, stack, oldMode, oldMode.next());
		}
		return InteractionResultHolder.success(stack);
	}

	private SWRGMode changeMode(Player player, ItemStack stack, SWRGMode oldMode, SWRGMode mode) {
		if (mode == oldMode) {
			return mode;
		}
		stack.setData(PEAttachmentTypes.SWRG_MODE, mode);
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
	public boolean canProvideFlight(ItemStack stack, Player player) {
		// Dummy result - swrg needs special-casing
		return false;
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return false;
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		if (!level.isClientSide && ProjectEConfig.server.cooldown.pedestal.swrg.get() != -1) {
			if (pedestal.getActivityCooldown() <= 0) {
				for (Mob living : level.getEntitiesOfClass(Mob.class, pedestal.getEffectBounds(),
						ent -> !ent.isSpectator() && (!(ent instanceof TamableAnimal tamableAnimal) || !tamableAnimal.isTame()))) {
					LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
					if (lightning != null) {
						lightning.moveTo(living.position());
						level.addFreshEntity(lightning);
					}
				}
				pedestal.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.swrg.get());
			} else {
				pedestal.decrementActivityCooldown();
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

	public enum SWRGMode {//Change the mode of SWRG. Modes:<p> 0 = Ring Off<p> 1 = Flight<p> 2 = Shield<p> 3 = Flight + Shield<p>
		OFF,
		FLIGHT,
		SHIELD,
		SHIELDED_FLIGHT;

		public boolean hasFlight() {
			return this == FLIGHT || this == SHIELDED_FLIGHT;
		}

		public boolean hasShield() {
			return this == SHIELD || this == SHIELDED_FLIGHT;
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