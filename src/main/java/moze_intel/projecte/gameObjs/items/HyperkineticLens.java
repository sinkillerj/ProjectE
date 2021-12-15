package moze_intel.projecte.gameObjs.items;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.ChargeItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.Constants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class HyperkineticLens extends ItemPE implements IProjectileShooter, IItemCharge {

	public HyperkineticLens(Properties props) {
		super(props);
		addItemCapability(ChargeItemCapabilityWrapper::new);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!world.isClientSide) {
			shootProjectile(player, stack, hand);
		}
		return ActionResult.success(stack);
	}

	@Override
	public boolean shootProjectile(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand) {
		World world = player.getCommandSenderWorld();
		long requiredEmc = Constants.EXPLOSIVE_LENS_COST[this.getCharge(stack)];
		if (!consumeFuel(player, stack, requiredEmc, true)) {
			return false;
		}
		world.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
		EntityLensProjectile ent = new EntityLensProjectile(player, this.getCharge(stack), world);
		ent.shootFromRotation(player, player.xRot, player.yRot, 0, 1.5F, 1);
		world.addFreshEntity(ent);
		return true;
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		return 3;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1.0D - getChargePercent(stack);
	}
}