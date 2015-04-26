package moze_intel.projecte.gameObjs.items.tools;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		deforestAOE(world, stack, player, 0);
		return stack;
	}
}
