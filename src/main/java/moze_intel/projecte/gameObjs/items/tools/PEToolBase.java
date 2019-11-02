package moze_intel.projecte.gameObjs.items.tools;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.items.ItemMode;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

public abstract class PEToolBase extends ItemMode {

	public static final float STAR_BASE_ATTACK = 20.0F;
	public static final float KATAR_BASE_ATTACK = 23.0F;
	protected EnumMatterType peToolMaterial;
	protected final Set<Material> harvestMaterials = new HashSet<>();

	public PEToolBase(Properties props, int numCharge, String[] modeDescrp) {
		super(props, numCharge, modeDescrp);
	}

	@Override
	public boolean canHarvestBlock(ItemStack stack, @Nonnull BlockState state) {
		return harvestMaterials.contains(state.getMaterial());
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		if (this.peToolMaterial == EnumMatterType.DARK_MATTER) {
			if (canHarvestBlock(stack, state)) {
				return 14.0f + (12.0f * this.getCharge(stack));
			}
		} else if (this.peToolMaterial == EnumMatterType.RED_MATTER) {
			if (canHarvestBlock(stack, state)) {
				return 16.0f + (14.0f * this.getCharge(stack));
			}
		}
		return 1.0F;
	}
}