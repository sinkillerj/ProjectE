package moze_intel.projecte.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;

public class ProjectileShooterItemCapabilityWrapper extends BasicItemCapability<IProjectileShooter> implements IProjectileShooter {

	@Override
	public Capability<IProjectileShooter> getCapability() {
		return ProjectEAPI.PROJECTILE_SHOOTER_ITEM_CAPABILITY;
	}

	@Override
	public boolean shootProjectile(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, @Nullable Hand hand) {
		return getItem().shootProjectile(player, stack, hand);
	}
}