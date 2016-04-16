package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class DarkSword extends PEToolBase implements IExtraFunction
{
	public DarkSword() 
	{
		super("dm_sword", (byte)2, new String[] {});
		this.setNoRepair();
		this.peToolMaterial = "dm_tools";
		this.pePrimaryToolClass = "sword";
	}

	// Only for RedSword to use
	protected DarkSword(String name, byte numcharges, String[] modeDesc)
	{
		super(name, numcharges, modeDesc);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase damaged, EntityLivingBase damager)
	{
		boolean flag = ProjectEConfig.useOldDamage;
		attackWithCharge(stack, damaged, damager, flag ? DARKSWORD_BASE_ATTACK : 1.0F);
		return true;
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state)
	{
		if (state.getBlock() == Blocks.web)
		{
			return 15.0F;
		}
		else
		{
			Material material = state.getMaterial();
			return material != Material.plants && material != Material.vine && material != Material.coral && material != Material.leaves && material != Material.gourd ? 1.0F : 1.5F;
		}
	}

	@Override
	public boolean canHarvestBlock(IBlockState state, ItemStack stack)
	{
		return state.getBlock() == Blocks.web;
	}

	@Override
	public boolean doExtraFunction(ItemStack stack, EntityPlayer player, EnumHand hand)
	{
		attackAOE(stack, player, false, DARKSWORD_BASE_ATTACK, 0);
		return true;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		if (ProjectEConfig.useOldDamage || slot != EntityEquipmentSlot.MAINHAND)
		{
			return super.getAttributeModifiers(slot, stack);
		}

		byte charge = stack.getTagCompound() == null ? 0 : getCharge(stack);
		float damage = (this instanceof RedSword ? REDSWORD_BASE_ATTACK : DARKSWORD_BASE_ATTACK) + charge;

		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", damage, 0));
		return multimap;
	}
}
