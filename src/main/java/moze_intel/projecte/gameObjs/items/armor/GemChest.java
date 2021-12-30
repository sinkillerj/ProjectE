package moze_intel.projecte.gameObjs.items.armor;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class GemChest extends GemArmorBase implements IFireProtector {

	public GemChest(Properties props) {
		super(EquipmentSlot.CHEST, props);
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(PELang.GEM_LORE_CHEST.translate());
	}

	@Override
	public void onArmorTick(ItemStack chest, Level level, Player player) {
		if (!level.isClientSide) {
			player.getCapability(InternalTimers.CAPABILITY).ifPresent(timers -> {
				timers.activateFeed();
				if (player.getFoodData().needsFood() && timers.canFeed()) {
					player.getFoodData().eat(2, 10);
				}
			});
		}
	}

	public void doExplode(Player player) {
		if (ProjectEConfig.server.difficulty.offensiveAbilities.get()) {
			WorldHelper.createNovaExplosion(player.getCommandSenderWorld(), player, player.getX(), player.getY(), player.getZ(), 9.0F);
		}
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, ServerPlayer player) {
		return player.getItemBySlot(EquipmentSlot.CHEST) == stack;
	}
}