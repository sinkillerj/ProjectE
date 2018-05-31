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

import javax.annotation.Nonnull;

public class DarkShovel extends PEToolBase
{
	public DarkShovel() 
	{
		super("dm_shovel", (byte)1, new String[]{});
		this.setNoRepair();
		this.peToolMaterial = "dm_tools";
		this.toolClasses.add("shovel");
		this.harvestMaterials.add(Material.GRASS);
		this.harvestMaterials.add(Material.GROUND);
		this.harvestMaterials.add(Material.SAND);
		this.harvestMaterials.add(Material.SNOW);
		this.harvestMaterials.add(Material.CLAY);
	}

	// Only for RedShovel
	protected DarkShovel(String name, byte numCharges, String[] modeDesc)
	{
		super(name, numCharges, modeDesc);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote)
		{
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}

		RayTraceResult mop = this.rayTrace(world, player, false);
		if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK
				&& world.getBlockState(mop.getBlockPos()).getBlock() == Blocks.GRAVEL)
		{
			tryVeinMine(stack, player, mop);
		}
		else
		{
			digAOE(stack, world, player, false, 0, hand);
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Nonnull
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, ItemStack stack)
	{
		if (slot != EntityEquipmentSlot.MAINHAND) return super.getAttributeModifiers(slot, stack);
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", this instanceof RedShovel ? 6 : 5, 0));
		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3, 0));
		return multimap;
	}
}
