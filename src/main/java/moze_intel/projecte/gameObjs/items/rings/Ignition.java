package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.items.IFireProtector;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class Ignition extends PEToggleItem implements IPedestalItem, IFireProtector, IProjectileShooter {

	public Ignition(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, Level level, @Nonnull Entity entity, int inventorySlot, boolean held) {
		if (level.isClientSide || inventorySlot >= Inventory.getSelectionSize() || !(entity instanceof Player player)) {
			return;
		}
		super.inventoryTick(stack, level, entity, inventorySlot, held);
		CompoundTag nbt = stack.getOrCreateTag();
		if (nbt.getBoolean(Constants.NBT_KEY_ACTIVE)) {
			if (getEmc(stack) == 0 && !consumeFuel(player, stack, 64, false)) {
				nbt.putBoolean(Constants.NBT_KEY_ACTIVE, false);
			} else {
				WorldHelper.igniteNearby(level, player);
				removeEmc(stack, EMCHelper.removeFractionalEMC(stack, 0.32F));
			}
		} else {
			WorldHelper.extinguishNearby(level, player);
		}
	}

	@Nonnull
	@Override
	public InteractionResult useOn(@Nonnull UseOnContext ctx) {
		return WorldHelper.igniteBlock(ctx);
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull BlockPos pos,
			@Nonnull PEDESTAL pedestal) {
		if (!level.isClientSide && ProjectEConfig.server.cooldown.pedestal.ignition.get() != -1) {
			if (pedestal.getActivityCooldown() == 0) {
				for (Mob living : level.getEntitiesOfClass(Mob.class, pedestal.getEffectBounds())) {
					living.hurt(DamageSource.IN_FIRE, 3.0F);
					living.setSecondsOnFire(8);
				}
				pedestal.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.ignition.get());
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
		if (ProjectEConfig.server.cooldown.pedestal.ignition.get() != -1) {
			list.add(PELang.PEDESTAL_IGNITION_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_IGNITION_2.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.ignition.get())));
		}
		return list;
	}

	@Override
	public boolean shootProjectile(@Nonnull Player player, @Nonnull ItemStack stack, InteractionHand hand) {
		Level level = player.getCommandSenderWorld();
		if (level.isClientSide) {
			return false;
		}
		EntityFireProjectile fire = new EntityFireProjectile(player, false, level);
		fire.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
		level.addFreshEntity(fire);
		return true;
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, ServerPlayer player) {
		return true;
	}
}