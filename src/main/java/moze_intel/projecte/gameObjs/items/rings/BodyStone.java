package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class BodyStone extends PEToggleItem implements IPedestalItem {

	public BodyStone(Properties props) {
		super(props);
		addItemCapability(new PedestalItemCapabilityWrapper());
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int slot, boolean held) {
		if (world.isRemote || slot > 8 || !(entity instanceof PlayerEntity)) {
			return;
		}
		super.inventoryTick(stack, world, entity, slot, held);
		PlayerEntity player = (PlayerEntity) entity;
		if (stack.getOrCreateTag().getBoolean(TAG_ACTIVE)) {
			long itemEmc = getEmc(stack);
			if (itemEmc < 64 && !consumeFuel(player, stack, 64, false)) {
				stack.getTag().putBoolean(TAG_ACTIVE, false);
			} else {
				player.getCapability(InternalTimers.CAPABILITY, null).ifPresent(timers -> {
					timers.activateFeed();
					if (player.getFoodStats().needFood() && timers.canFeed()) {
						world.playSound(null, player.posX, player.posY, player.posZ, PESounds.HEAL, SoundCategory.PLAYERS, 1.0F, 1.0F);
						player.getFoodStats().addStats(2, 10);
						removeEmc(stack, 64);
					}
				});
			}
		}
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		if (!world.isRemote && ProjectEConfig.server.cooldown.pedestal.body.get() != -1) {
			TileEntity te = world.getTileEntity(pos);
			if (!(te instanceof DMPedestalTile)) {
				return;
			}
			DMPedestalTile tile = (DMPedestalTile) te;
			if (tile.getActivityCooldown() == 0) {
				List<ServerPlayerEntity> players = world.getEntitiesWithinAABB(ServerPlayerEntity.class, tile.getEffectBounds());
				for (ServerPlayerEntity player : players) {
					if (player.getFoodStats().needFood()) {
						world.playSound(null, player.posX, player.posY, player.posZ, PESounds.HEAL, SoundCategory.PLAYERS, 1.0F, 1.0F);
						player.getFoodStats().addStats(1, 1); // 1/2 shank
					}
				}
				tile.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.body.get());
			} else {
				tile.decrementActivityCooldown();
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.body.get() != -1) {
			list.add(new TranslationTextComponent("pe.body.pedestal1").applyTextStyle(TextFormatting.BLUE));
			list.add(new TranslationTextComponent("pe.body.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.body.get())).applyTextStyle(TextFormatting.BLUE));
		}
		return list;
	}
}