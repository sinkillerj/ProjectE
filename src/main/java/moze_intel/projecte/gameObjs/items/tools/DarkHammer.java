package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
		attackWithCharge(stack, damaged, damager, 13.0F);
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		digAOE(stack, world, player, true);
		return stack;
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int metadata)
	{
		if ((block == ObjHandler.matterBlock && metadata == 0) || block == ObjHandler.dmFurnaceOff || block == ObjHandler.dmFurnaceOn)
		{
			return 1200000.0F;
		}
		
		return super.getDigSpeed(stack, block, metadata);
	}
}
