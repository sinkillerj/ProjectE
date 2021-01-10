package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.common.ToolType;

public class PEHammer extends PETool {

	public PEHammer(EnumMatterType matterType, int numCharges, Properties props) {
		super(matterType, 10, -3, numCharges, props
				.addToolType(ToolType.PICKAXE, matterType.getHarvestLevel())
				.addToolType(ToolHelper.TOOL_TYPE_HAMMER, matterType.getHarvestLevel()));
	}

	/**
	 * Simple copy of {@link net.minecraft.item.PickaxeItem#canHarvestBlock(BlockState)}'s fallback/shortcut to allow the hammer to also mine all blocks of that
	 * material.
	 *
	 * @implNote This method is overridden instead of {@link net.minecraftforge.common.extensions.IForgeItem#canHarvestBlock(ItemStack, BlockState)} so that it is used as
	 * a fallback if {@link PETool#canHarvestBlock(ItemStack, BlockState)} does not find a matching tool/required level for the tool. As the default implementation that
	 * gets used is one where the stack does not matter (which would be this)
	 */
	@Override
	public boolean canHarvestBlock(BlockState state) {
		//Note: These checks cover the need of overriding/shortcutting the destroy speed
		Material material = state.getMaterial();
		return material == Material.ROCK || material == Material.IRON || material == Material.ANVIL;
	}

	@Override
	public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity damaged, @Nonnull LivingEntity damager) {
		ToolHelper.attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		return ToolHelper.canMatterMine(matterType, state.getBlock()) ? 1_200_000 : super.getDestroySpeed(stack, state);
	}

	@Nonnull
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
		return ToolHelper.addChargeAttributeModifier(super.getAttributeModifiers(slot, stack), slot, stack);
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		if (player == null) {
			return ActionResultType.PASS;
		}
		return ToolHelper.digAOE(context.getWorld(), player, context.getHand(), context.getItem(), context.getPos(), context.getFace(), true, 0);
	}
}