package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class FuelBlock extends Block 
{
	public FuelBlock() 
	{
		super(Material.rock);
		this.setUnlocalizedName("pe_fuel_block");
		this.setCreativeTab(ObjHandler.cTab);
		this.setHardness(0.5f);
	}
	
	@Override
	public int damageDropped(IBlockState state)
	{
		return meta;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs cTab, List list)
	{
		for (int i = 0; i < 3; i++)
		{
			list.add(new ItemStack(item , 1, i));
		}
	}
}
