package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.utils.ToolHelper;
import moze_intel.projecte.utils.ToolHelper.ChargeAttributeCache;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class PEHammer extends PETool {

	private final ChargeAttributeCache attributeCache = new ChargeAttributeCache();

	public PEHammer(EnumMatterType matterType, int numCharges, Properties props) {
		super(matterType, PETags.Blocks.MINEABLE_WITH_PE_HAMMER, 10, -3, numCharges, props);
	}

	@Override
	public boolean hurtEnemy(@Nonnull ItemStack stack, @Nonnull LivingEntity damaged, @Nonnull LivingEntity damager) {
		ToolHelper.attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction) || ToolHelper.DEFAULT_PE_HAMMER_ACTIONS.contains(toolAction);
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		return ToolHelper.canMatterMine(matterType, state.getBlock()) ? 1_200_000 : super.getDestroySpeed(stack, state);
	}

	@Nonnull
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlot slot, ItemStack stack) {
		return attributeCache.addChargeAttributeModifier(super.getAttributeModifiers(slot, stack), slot, stack);
	}

	@Nonnull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		return ToolHelper.digAOE(context.getLevel(), player, context.getHand(), context.getItemInHand(), context.getClickedPos(), context.getClickedFace(), true, 0);
	}
}