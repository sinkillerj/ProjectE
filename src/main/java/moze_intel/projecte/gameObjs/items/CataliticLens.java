package moze_intel.projecte.gameObjs.items;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CataliticLens extends DestructionCatalyst implements IProjectileShooter {

	public CataliticLens(Properties props) {
		super(props);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
	}

	@Override
	public boolean shootProjectile(@Nonnull Player player, @Nonnull ItemStack stack, InteractionHand hand) {
		return PEItems.HYPERKINETIC_LENS.get().shootProjectile(player, stack, hand);
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		return 7;
	}
}