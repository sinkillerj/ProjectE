package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import moze_intel.projecte.gameObjs.EnumMatterType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class DarkAxe extends PEToolBase
{
	public DarkAxe(Properties props)
	{
		super(props, (byte)2, new String[]{});
		this.peToolMaterial = EnumMatterType.DARK_MATTER;
		this.harvestMaterials.add(Material.WOOD);
		this.harvestMaterials.add(Material.PLANTS);
		this.harvestMaterials.add(Material.TALL_PLANTS);
	}

	// Only for RedAxe
	protected DarkAxe(Properties props, byte numCharges, String[] modeDesc)
	{
		super(props, numCharges, modeDesc);
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		clearTagAOE(world, stack, player, BlockTags.LOGS, 0, hand);
		clearTagAOE(world, stack, player, BlockTags.LEAVES, 0, hand);
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	@Nonnull
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack)
	{
		if (slot != EquipmentSlotType.MAINHAND) return super.getAttributeModifiers(slot, stack);
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", this instanceof RedAxe ? 9 : 8, AttributeModifier.Operation.ADDITION));
		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3, AttributeModifier.Operation.ADDITION));
		return multimap;
	}
}
