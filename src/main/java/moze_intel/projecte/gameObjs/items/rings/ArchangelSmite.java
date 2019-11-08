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
import moze_intel.projecte.network.packets.LeftClickArchangelPKT;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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
		addItemCapability(new PedestalItemCapabilityWrapper());
	}

	public void fireVolley(ItemStack stack, PlayerEntity player) {
		for (int i = 0; i < 10; i++) {
			fireArrow(stack, player.world, player, 4F);
		}
	}

	private void emptyLeftClick(PlayerInteractEvent.LeftClickEmpty evt) {
		PacketHandler.sendToServer(new LeftClickArchangelPKT());
	}

	private void leftClickBlock(PlayerInteractEvent.LeftClickBlock evt) {
		if (!evt.getWorld().isRemote && evt.getUseItem() != Event.Result.DENY && !evt.getItemStack().isEmpty() && evt.getItemStack().getItem() == this) {
			fireVolley(evt.getItemStack(), evt.getPlayer());
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		if (!player.world.isRemote) {
			fireVolley(stack, player);
		}
		return super.onLeftClickEntity(stack, player, entity);
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int par4, boolean par5) {
		if (!world.isRemote && getMode(stack) == 1 && entity instanceof LivingEntity) {
			fireArrow(stack, world, (LivingEntity) entity, 1F);
		}
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		if (!world.isRemote) {
			fireArrow(player.getHeldItem(hand), world, player, 1F);
		}
		return ActionResult.newResult(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}

	private void fireArrow(ItemStack ring, World world, LivingEntity shooter, float inaccuracy) {
		EntityHomingArrow arrow = new EntityHomingArrow(world, shooter, 2.0F);
		if (!(shooter instanceof PlayerEntity) || consumeFuel((PlayerEntity) shooter, ring, EMCHelper.getEmcValue(Items.ARROW), true)) {
			arrow.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 3.0F, inaccuracy);
			world.playSound(null, shooter.posX, shooter.posY, shooter.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F));
			world.addEntity(arrow);
		}
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		if (!world.isRemote && ProjectEConfig.pedestalCooldown.archangel.get() != -1) {
			TileEntity te = world.getTileEntity(pos);
			if (!(te instanceof DMPedestalTile)) {
				return;
			}
			DMPedestalTile tile = (DMPedestalTile) te;
			if (tile.getActivityCooldown() == 0) {
				if (!world.getEntitiesWithinAABB(MobEntity.class, tile.getEffectBounds()).isEmpty()) {
					for (int i = 0; i < 3; i++) {
						EntityHomingArrow arrow = new EntityHomingArrow(world, FakePlayerFactory.get((ServerWorld) world, PECore.FAKEPLAYER_GAMEPROFILE), 2.0F);
						arrow.posX = tile.centeredX;
						arrow.posY = tile.centeredY + 2;
						arrow.posZ = tile.centeredZ;
						arrow.setMotion(0, 1, 0);
						arrow.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + 0.5F);
						world.addEntity(arrow);
					}
				}
				tile.setActivityCooldown(ProjectEConfig.pedestalCooldown.archangel.get());
			} else {
				tile.decrementActivityCooldown();
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.pedestalCooldown.archangel.get() != -1) {
			list.add(new TranslationTextComponent("pe.archangel.pedestal1").applyTextStyle(TextFormatting.BLUE));
			list.add(new TranslationTextComponent("pe.archangel.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.pedestalCooldown.archangel.get())).applyTextStyle(TextFormatting.BLUE));
		}
		return list;
	}
}