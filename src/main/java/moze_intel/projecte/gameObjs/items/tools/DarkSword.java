package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class DarkSword extends PEToolBase implements IExtraFunction {

	public DarkSword(Properties props) {
		super(props, (byte) 2, new String[]{});
		this.peToolMaterial = EnumMatterType.DARK_MATTER;
		addItemCapability(new ExtraFunctionItemCapabilityWrapper());
	}

	// Only for RedSword to use
	protected DarkSword(Properties props, byte numcharges, String[] modeDesc) {
		super(props, numcharges, modeDesc);
	}

	@Override
	public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity damaged, @Nonnull LivingEntity damager) {
		attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		if (state.getBlock() == Blocks.COBWEB) {
			return 15.0F;
		} else {
			Material material = state.getMaterial();
			return material != Material.PLANTS && material != Material.TALL_PLANTS && material != Material.CORAL && material != Material.LEAVES && material != Material.GOURD ? 1.0F : 1.5F;
		}
	}

	@Override
	public boolean canHarvestBlock(ItemStack stack, @Nonnull BlockState state) {
		return state.getBlock() == Blocks.COBWEB;
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand) {
		if (player.getCooledAttackStrength(0F) == 1) {
			attackAOE(stack, player, false, DARKSWORD_BASE_ATTACK, 0, hand);
			PlayerHelper.resetCooldown(player);
			return true;
		} else {
			return false;
		}
	}

	@Nonnull
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
		if (slot != EquipmentSlotType.MAINHAND) {
			return super.getAttributeModifiers(slot, stack);
		}

		int charge = getCharge(stack);
		float damage = (this instanceof RedSword ? REDSWORD_BASE_ATTACK : DARKSWORD_BASE_ATTACK) + charge;

		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", damage, AttributeModifier.Operation.ADDITION));
		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.4, AttributeModifier.Operation.ADDITION));
		return multimap;
	}
}