package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.to_server.LeftClickArchangelPKT;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

public class ArchangelSmite extends PEToggleItem implements IPedestalItem {

	public ArchangelSmite(Properties props) {
		super(props);
		MinecraftForge.EVENT_BUS.addListener(this::emptyLeftClick);
		MinecraftForge.EVENT_BUS.addListener(this::leftClickBlock);
		addItemCapability(PedestalItemCapabilityWrapper::new);
	}

	public void fireVolley(ItemStack stack, Player player) {
		for (int i = 0; i < 10; i++) {
			fireArrow(stack, player.level, player, 4F);
		}
	}

	private void emptyLeftClick(PlayerInteractEvent.LeftClickEmpty evt) {
		PacketHandler.sendToServer(new LeftClickArchangelPKT());
	}

	private void leftClickBlock(PlayerInteractEvent.LeftClickBlock evt) {
		if (!evt.getLevel().isClientSide && evt.getUseItem() != Event.Result.DENY && !evt.getItemStack().isEmpty() && evt.getItemStack().getItem() == this) {
			fireVolley(evt.getItemStack(), evt.getEntity());
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		if (!player.level.isClientSide) {
			fireVolley(stack, player);
		}
		return super.onLeftClickEntity(stack, player, entity);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int invSlot, boolean isSelected) {
		if (!level.isClientSide && getMode(stack) == 1 && entity instanceof LivingEntity) {
			fireArrow(stack, level, (LivingEntity) entity, 1F);
		}
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		if (!level.isClientSide) {
			fireArrow(player.getItemInHand(hand), level, player, 1F);
		}
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	private void fireArrow(ItemStack ring, Level level, LivingEntity shooter, float inaccuracy) {
		EntityHomingArrow arrow = new EntityHomingArrow(level, shooter, 2.0F);
		if (!(shooter instanceof Player player) || consumeFuel(player, ring, EMCHelper.getEmcValue(Items.ARROW), true)) {
			arrow.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, 3.0F, inaccuracy);
			level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.random.nextFloat() * 0.4F + 1.2F));
			level.addFreshEntity(arrow);
		}
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		if (!level.isClientSide && ProjectEConfig.server.cooldown.pedestal.archangel.get() != -1) {
			if (pedestal.getActivityCooldown() == 0) {
				if (!level.getEntitiesOfClass(Mob.class, pedestal.getEffectBounds()).isEmpty()) {
					double centeredX = pos.getX() + 0.5;
					double centeredY = pos.getY() + 0.5;
					double centeredZ = pos.getZ() + 0.5;
					for (int i = 0; i < 3; i++) {
						EntityHomingArrow arrow = new EntityHomingArrow(level, FakePlayerFactory.get((ServerLevel) level, PECore.FAKEPLAYER_GAMEPROFILE), 2.0F);
						arrow.setPosRaw(centeredX, centeredY + 2, centeredZ);
						arrow.setDeltaMovement(0, 1, 0);
						arrow.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (level.random.nextFloat() * 0.4F + 1.2F) + 0.5F);
						level.addFreshEntity(arrow);
					}
				}
				pedestal.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.archangel.get());
			} else {
				pedestal.decrementActivityCooldown();
			}
		}
		return false;
	}

	@NotNull
	@Override
	public List<Component> getPedestalDescription() {
		List<Component> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.archangel.get() != -1) {
			list.add(PELang.PEDESTAL_ARCHANGEL_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_ARCHANGEL_2.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.archangel.get())));
		}
		return list;
	}
}