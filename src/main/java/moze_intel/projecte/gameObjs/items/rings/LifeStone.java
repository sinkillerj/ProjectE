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
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class LifeStone extends PEToggleItem implements IPedestalItem {

	public LifeStone(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int slot, boolean held) {
		if (world.isRemote || slot > 8 || !(entity instanceof PlayerEntity)) {
			return;
		}
		super.inventoryTick(stack, world, entity, slot, held);
		PlayerEntity player = (PlayerEntity) entity;
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.getBoolean(Constants.NBT_KEY_ACTIVE)) {
			if (!consumeFuel(player, stack, 2 * 64, false)) {
				nbt.putBoolean(Constants.NBT_KEY_ACTIVE, false);
			} else {
				player.getCapability(InternalTimers.CAPABILITY, null).ifPresent(timers -> {
					timers.activateFeed();
					if (player.getFoodStats().needFood() && timers.canFeed()) {
						world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), PESounds.HEAL, SoundCategory.PLAYERS, 1, 1);
						player.getFoodStats().addStats(2, 10);
						removeEmc(stack, 64);
					}
					timers.activateHeal();
					if (player.getHealth() < player.getMaxHealth() && timers.canHeal()) {
						world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), PESounds.HEAL, SoundCategory.PLAYERS, 1, 1);
						player.heal(2.0F);
						removeEmc(stack, 64);
					}
				});
			}
		}
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		if (!world.isRemote && ProjectEConfig.server.cooldown.pedestal.life.get() != -1) {
			TileEntity te = world.getTileEntity(pos);
			if (!(te instanceof DMPedestalTile)) {
				return;
			}
			DMPedestalTile tile = (DMPedestalTile) te;
			if (tile.getActivityCooldown() == 0) {
				List<ServerPlayerEntity> players = world.getEntitiesWithinAABB(ServerPlayerEntity.class, tile.getEffectBounds());
				for (ServerPlayerEntity player : players) {
					if (player.getHealth() < player.getMaxHealth()) {
						world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), PESounds.HEAL, SoundCategory.BLOCKS, 1, 1);
						player.heal(1.0F); // 1/2 heart
					}
					if (player.getFoodStats().needFood()) {
						world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), PESounds.HEAL, SoundCategory.BLOCKS, 1, 1);
						player.getFoodStats().addStats(1, 1); // 1/2 shank
					}
				}
				tile.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.life.get());
			} else {
				tile.decrementActivityCooldown();
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.life.get() != -1) {
			list.add(new TranslationTextComponent("pe.life.pedestal1").applyTextStyle(TextFormatting.BLUE));
			list.add(new TranslationTextComponent("pe.life.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.life.get())).applyTextStyle(TextFormatting.BLUE));
		}
		return list;
	}
}