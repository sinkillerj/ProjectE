package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.api.tile.IDMPedestal;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SWRG extends ItemPE implements IPedestalItem, IFlightProvider, IProjectileShooter {

	public SWRG(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	private void tick(ItemStack stack, Player player) {
		CompoundTag nbt = stack.getOrCreateTag();
		if (nbt.getInt(Constants.NBT_KEY_MODE) > 1) {
			// Repel on both sides - smooth animation
			WorldHelper.repelEntitiesSWRG(player.getCommandSenderWorld(), player.getBoundingBox().inflate(5), player);
		}
		if (player.getCommandSenderWorld().isClientSide) {
			return;
		}
		ServerPlayer playerMP = (ServerPlayer) player;
		if (getEmc(stack) == 0 && !consumeFuel(player, stack, 64, false)) {
			if (nbt.getInt(Constants.NBT_KEY_MODE) > 0) {
				changeMode(player, stack, 0);
			}
			if (playerMP.getAbilities().mayfly) {
				playerMP.getCapability(InternalAbilities.CAPABILITY).ifPresent(InternalAbilities::disableSwrgFlightOverride);
			}
			return;
		}

		if (!playerMP.getAbilities().mayfly) {
			playerMP.getCapability(InternalAbilities.CAPABILITY).ifPresent(InternalAbilities::enableSwrgFlightOverride);
		}

		if (playerMP.getAbilities().flying) {
			if (!isFlyingEnabled(nbt)) {
				changeMode(player, stack, nbt.getInt(Constants.NBT_KEY_MODE) == 0 ? 1 : 3);
			}
		} else if (isFlyingEnabled(nbt)) {
			changeMode(player, stack, nbt.getInt(Constants.NBT_KEY_MODE) == 1 ? 0 : 2);
		}

		float toRemove = 0;

		if (playerMP.getAbilities().flying) {
			toRemove = 0.32F;
		}

		if (nbt.getInt(Constants.NBT_KEY_MODE) == 2) {
			toRemove = 0.32F;
		} else if (nbt.getInt(Constants.NBT_KEY_MODE) == 3) {
			toRemove = 0.64F;
		}

		removeEmc(stack, EMCHelper.removeFractionalEMC(stack, toRemove));

		playerMP.fallDistance = 0;
	}

	private boolean isFlyingEnabled(CompoundTag nbt) {
		return nbt.getInt(Constants.NBT_KEY_MODE) == 1 || nbt.getInt(Constants.NBT_KEY_MODE) == 3;
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull Entity entity, int invSlot, boolean isHeldItem) {
		if (invSlot < Inventory.getSelectionSize() && entity instanceof Player player) {
			tick(stack, player);
		}
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!world.isClientSide) {
			int newMode = switch (stack.getOrCreateTag().getInt(Constants.NBT_KEY_MODE)) {
				case 0 -> 2;
				case 1 -> 3;
				case 2 -> 0;
				case 3 -> 1;
				default -> 0;
			};
			changeMode(player, stack, newMode);
		}
		return InteractionResultHolder.success(stack);
	}

	/**
	 * Change the mode of SWRG. Modes:<p> 0 = Ring Off<p> 1 = Flight<p> 2 = Shield<p> 3 = Flight + Shield<p>
	 */
	public void changeMode(Player player, ItemStack stack, int mode) {
		CompoundTag nbt = stack.getOrCreateTag();
		int oldMode = nbt.getInt(Constants.NBT_KEY_MODE);
		if (mode == oldMode) {
			return;
		}
		nbt.putInt(Constants.NBT_KEY_MODE, mode);
		if (player == null) {
			//Don't do sounds if the player is null
			return;
		}
		if (mode == 0 || oldMode == 3) {
			//At least one mode deactivated
			player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.HEAL.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
		} else if (oldMode == 0 || mode == 3) {
			//At least one mode activated
			player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.UNCHARGE.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
		}
		//Doesn't handle going from mode 1 to 2 or 2 to 1
	}

	@Override
	public boolean canProvideFlight(ItemStack stack, ServerPlayer player) {
		// Dummy result - swrg needs special-casing
		return false;
	}

	@Override
	public boolean isBarVisible(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull BlockPos pos,
			@Nonnull PEDESTAL pedestal) {
		if (!world.isClientSide && ProjectEConfig.server.cooldown.pedestal.swrg.get() != -1) {
			if (pedestal.getActivityCooldown() <= 0) {
				for (Mob living : world.getEntitiesOfClass(Mob.class, pedestal.getEffectBounds(),
						ent -> !ent.isSpectator() && (!(ent instanceof TamableAnimal tamableAnimal) || !tamableAnimal.isTame()))) {
					LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world);
					if (lightning != null) {
						lightning.moveTo(living.position());
						world.addFreshEntity(lightning);
					}
				}
				pedestal.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.swrg.get());
			} else {
				pedestal.decrementActivityCooldown();
			}
		}
		return false;
	}

	@Nonnull
	@Override
	public List<Component> getPedestalDescription() {
		List<Component> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.swrg.get() != -1) {
			list.add(PELang.PEDESTAL_SWRG_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_SWRG_2.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.swrg.get())));
		}
		return list;
	}

	@Override
	public boolean shootProjectile(@Nonnull Player player, @Nonnull ItemStack stack, @Nullable InteractionHand hand) {
		EntitySWRGProjectile projectile = new EntitySWRGProjectile(player, false, player.level);
		projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
		player.level.addFreshEntity(projectile);
		return true;
	}
}