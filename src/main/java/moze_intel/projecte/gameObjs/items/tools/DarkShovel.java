package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class DarkShovel extends PEToolBase
{
	public DarkShovel() 
	{
		super("dm_shovel", (byte)1, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "dm_tools";
		this.pePrimaryToolClass = "shovel";
		this.harvestMaterials.add(Material.grass);
		this.harvestMaterials.add(Material.ground);
		this.harvestMaterials.add(Material.sand);
		this.harvestMaterials.add(Material.snow);
		this.harvestMaterials.add(Material.clay);
	}

	// Only for RedShovel
	protected DarkShovel(String name, byte numCharges, String[] modeDesc)
	{
		super(name, numCharges, modeDesc);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (world.isRemote)
		{
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}

		RayTraceResult mop = this.getMovingObjectPositionFromPlayer(world, player, false);
		if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK
				&& world.getBlockState(mop.getBlockPos()).getBlock() == Blocks.gravel)
		{
			tryVeinMine(stack, player, mop);
		}
		else
		{
			digAOE(stack, world, player, false, 0);
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		if (slot != EntityEquipmentSlot.MAINHAND) return super.getAttributeModifiers(slot, stack);
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", this instanceof RedShovel ? 6 : 5, 0));
		return multimap;
	}
}
