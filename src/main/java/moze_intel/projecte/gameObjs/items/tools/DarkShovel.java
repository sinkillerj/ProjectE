package moze_intel.projecte.gameObjs.items.tools;

import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SwingItemPKT;
import moze_intel.projecte.utils.Coordinates;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

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
	}

	// Only for RedShovel
	protected DarkShovel(String name, byte numCharges, String[] modeDesc)
	{
		super(name, numCharges, modeDesc);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		digAOE(stack, world, player, false);
		return stack;
	}
}
