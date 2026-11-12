package moze_intel.projecte.gameObjs.items;

import java.util.function.IntFunction;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HyperkineticLens extends ItemPE implements IProjectileShooter, IItemCharge, IBarHelper {

	public HyperkineticLens(Properties props) {
		super(props.component(PEDataComponentTypes.CHARGE, 0)
				.component(PEDataComponentTypes.STORED_EMC, 0L)
		);
	}

	@NotNull
	@Override
	public InteractionResult use(Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			shootProjectile(player, stack, hand);
		}
		return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
	}

	@Override
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
		ExplosiveLensCharge charge = ExplosiveLensCharge.BY_ID.apply(getCharge(stack));
		if (!consumeFuel(player, stack, charge.emcCost(), true)) {
			return false;
		}
		Level level = player.level();
		level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		EntityLensProjectile ent = new EntityLensProjectile(player, charge);
		ent.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
		level.addFreshEntity(ent);
		return true;
	}

	@Override
	public int getNumCharges(@NotNull ItemStack stack) {
		return 3;
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return true;
	}

	@Override
	public float getWidthForBar(ItemStack stack) {
		return 1 - getChargePercent(stack);
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		return getScaledBarWidth(stack);
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		return getColorForBar(stack);
	}

	public enum ExplosiveLensCharge {
		UNCHARGED(4, 384),
		SINGLE_CHARGE(8, 768),
		DOUBLE_CHARGE(12, 1_536),
		MAX_CHARGE(16, 2_304);

		public static final IntFunction<ExplosiveLensCharge> BY_ID = ByIdMap.continuous(ExplosiveLensCharge::ordinal, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);

		private final float radius;
		private final long emcCost;

		ExplosiveLensCharge(float radius, long emcCost) {
			this.radius = radius;
			this.emcCost = emcCost;
		}

		public float radius() {
			return radius;
		}

		public long emcCost() {
			return emcCost;
		}
	}
}
