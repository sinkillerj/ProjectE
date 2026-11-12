package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.minecraft.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArchangelSmite extends PEToggleItem implements IPedestalItem {

	public ArchangelSmite(Properties props) {
		super(props.component(PEDataComponentTypes.STORED_EMC, 0L));
		NeoForge.EVENT_BUS.addListener(this::emptyLeftClick);
		NeoForge.EVENT_BUS.addListener(this::leftClickBlock);
	}

	public static void fireVolley(ItemStack stack, Player player) {
		for (int i = 0; i < 10; i++) {
			fireArrow(stack, player.level(), player, 4F);
		}
	}

	private void emptyLeftClick(PlayerInteractEvent.LeftClickEmpty evt) {
		PECore.packetHandler().activateArchangel();
	}

	private void leftClickBlock(PlayerInteractEvent.LeftClickBlock evt) {
		if (!evt.getLevel().isClientSide() && evt.getUseItem() != TriState.FALSE && !evt.getItemStack().isEmpty() && evt.getItemStack().is(this)) {
			fireVolley(evt.getItemStack(), evt.getEntity());
		}
	}

	@Override
	public boolean onLeftClickEntity(@NotNull ItemStack stack, Player player, @NotNull Entity entity) {
		if (!player.level().isClientSide()) {
			fireVolley(stack, player);
		}
		return super.onLeftClickEntity(stack, player, entity);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
		super.inventoryTick(stack, level, entity, slot);
		if (!level.isClientSide() && getMode(stack) && entity instanceof LivingEntity living) {
			fireArrow(stack, level, living, 1F);
		}
	}

	@NotNull
	@Override
	public InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		if (!level.isClientSide()) {
			fireArrow(player.getItemInHand(hand), level, player, 1F);
		}
		return InteractionResult.SUCCESS;
	}

	private static void fireArrow(ItemStack ring, Level level, LivingEntity shooter, float inaccuracy) {
		EntityHomingArrow arrow = new EntityHomingArrow(level, shooter, 2.0F);
		if (!(shooter instanceof Player player) || consumeFuel(player, ring, IEMCProxy.INSTANCE.getValue(Items.ARROW), true)) {
			arrow.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 3.0F, inaccuracy);
			level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F));
			level.addFreshEntity(arrow);
		}
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		if (!level.isClientSide() && ProjectEConfig.server.cooldown.pedestal.archangel.get() != -1) {
			if (pedestal.getActivityCooldown() == 0) {
				if (!level.getEntitiesOfClass(Mob.class, pedestal.getEffectBounds()).isEmpty()) {
					double centeredX = pos.getX() + 0.5;
					double centeredY = pos.getY() + 0.5;
					double centeredZ = pos.getZ() + 0.5;
					for (int i = 0; i < 3; i++) {
						EntityHomingArrow arrow = new EntityHomingArrow(level, FakePlayerFactory.get((ServerLevel) level, PECore.FAKEPLAYER_GAMEPROFILE), 2.0F);
						arrow.setPosRaw(centeredX, centeredY + 2, centeredZ);
						arrow.setDeltaMovement(0, 1, 0);
						arrow.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
						level.addFreshEntity(arrow);
					}
				}
				pedestal.setActivityCooldown(level, pos, ProjectEConfig.server.cooldown.pedestal.archangel.get());
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
		if (ProjectEConfig.server.cooldown.pedestal.archangel.get() != -1) {
			list.add(PELang.PEDESTAL_ARCHANGEL_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_ARCHANGEL_2.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.archangel.get(), tickRate)));
		}
		return list;
	}
}
