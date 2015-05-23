package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.MatterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DarkHammer extends PEToolBase
{
	public DarkHammer() 
	{
		super("dm_hammer", (byte)2, new String[] {});
		this.setNoRepair();
		this.peToolMaterial = "dm_tools";
		this.pePrimaryToolClass = "hammer";
		this.harvestMaterials.add(Material.iron);
		this.harvestMaterials.add(Material.anvil);
		this.harvestMaterials.add(Material.rock);

		this.secondaryClasses.add("pickaxe");
		this.secondaryClasses.add("chisel");
	}

	// Only for RedHammer
	protected DarkHammer(String name, byte numCharges, String[] modeDesc)
	{
		super(name, numCharges, modeDesc);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase damaged, EntityLivingBase damager)
	{
		attackWithCharge(stack, damaged, damager, HAMMER_BASE_ATTACK);
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		digAOE(stack, world, player, true, 0);
		return stack;
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, IBlockState state)
	{
		Block block = state.getBlock();
		if ((block == ObjHandler.matterBlock && state.getValue(MatterBlock.TIER_PROP) == MatterBlock.EnumMatterType.DARK_MATTER)
				|| block == ObjHandler.dmFurnaceOff
				|| block == ObjHandler.dmFurnaceOn)
		{
			return 1200000.0F;
		}
		
		return super.getDigSpeed(stack, state);
	}
}
