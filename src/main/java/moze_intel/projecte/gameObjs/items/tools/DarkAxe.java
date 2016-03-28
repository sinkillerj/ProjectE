package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class DarkAxe extends PEToolBase
{
	public DarkAxe()
	{
		super("dm_axe", (byte)2, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "dm_tools";
		this.pePrimaryToolClass = "axe";
		this.harvestMaterials.add(Material.wood);
		this.harvestMaterials.add(Material.plants);
		this.harvestMaterials.add(Material.vine);
	}

	// Only for RedAxe
	protected DarkAxe(String name, byte numCharges, String[] modeDesc)
	{
		super(name, numCharges, modeDesc);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		clearOdAOE(world, stack, player, "logWood", 0);
		clearOdAOE(world, stack, player, "treeLeaves", 0);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		if (slot != EntityEquipmentSlot.MAINHAND) return super.getAttributeModifiers(slot, stack);
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", this instanceof RedAxe ? 9 : 8, 0));
		return multimap;
	}
}
