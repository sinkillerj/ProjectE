package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.to_server.LeftClickArchangelPKT;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;

public class ArchangelSmite extends PEToggleItem implements IPedestalItem {

	public ArchangelSmite(Properties props) {
		super(props);
		MinecraftForge.EVENT_BUS.addListener(this::emptyLeftClick);
		MinecraftForge.EVENT_BUS.addListener(this::leftClickBlock);
		addItemCapability(PedestalItemCapabilityWrapper::new);
	}

	public void fireVolley(ItemStack stack, PlayerEntity player) {
		for (int i = 0; i < 10; i++) {
			fireArrow(stack, player.level, player, 4F);
		}
	}

	private void emptyLeftClick(PlayerInteractEvent.LeftClickEmpty evt) {
		PacketHandler.sendToServer(new LeftClickArchangelPKT());
	}

	private void leftClickBlock(PlayerInteractEvent.LeftClickBlock evt) {
		if (!evt.getWorld().isClientSide && evt.getUseItem() != Event.Result.DENY && !evt.getItemStack().isEmpty() && evt.getItemStack().getItem() == this) {
			fireVolley(evt.getItemStack(), evt.getPlayer());
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		if (!player.level.isClientSide) {
			fireVolley(stack, player);
		}
		return super.onLeftClickEntity(stack, player, entity);
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int invSlot, boolean isSelected) {
		if (!world.isClientSide && getMode(stack) == 1 && entity instanceof LivingEntity) {
			fireArrow(stack, world, (LivingEntity) entity, 1F);
		}
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> use(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
		if (!world.isClientSide) {
			fireArrow(player.getItemInHand(hand), world, player, 1F);
		}
		return ActionResult.success(player.getItemInHand(hand));
	}

	private void fireArrow(ItemStack ring, World world, LivingEntity shooter, float inaccuracy) {
		EntityHomingArrow arrow = new EntityHomingArrow(world, shooter, 2.0F);
		if (!(shooter instanceof PlayerEntity) || consumeFuel((PlayerEntity) shooter, ring, EMCHelper.getEmcValue(Items.ARROW), true)) {
			arrow.shootFromRotation(shooter, shooter.xRot, shooter.yRot, 0.0F, 3.0F, inaccuracy);
			world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F));
			world.addFreshEntity(arrow);
		}
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		if (!world.isClientSide && ProjectEConfig.server.cooldown.pedestal.archangel.get() != -1) {
			DMPedestalTile tile = WorldHelper.getTileEntity(DMPedestalTile.class, world, pos, true);
			if (tile != null) {
				if (tile.getActivityCooldown() == 0) {
					if (!world.getEntitiesOfClass(MobEntity.class, tile.getEffectBounds()).isEmpty()) {
						for (int i = 0; i < 3; i++) {
							EntityHomingArrow arrow = new EntityHomingArrow(world, FakePlayerFactory.get((ServerWorld) world, PECore.FAKEPLAYER_GAMEPROFILE), 2.0F);
							arrow.setPosRaw(tile.centeredX, tile.centeredY + 2, tile.centeredZ);
							arrow.setDeltaMovement(0, 1, 0);
							arrow.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + 0.5F);
							world.addFreshEntity(arrow);
						}
					}
					tile.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.archangel.get());
				} else {
					tile.decrementActivityCooldown();
				}
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.archangel.get() != -1) {
			list.add(PELang.PEDESTAL_ARCHANGEL_1.translateColored(TextFormatting.BLUE));
			list.add(PELang.PEDESTAL_ARCHANGEL_2.translateColored(TextFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.archangel.get())));
		}
		return list;
	}
}