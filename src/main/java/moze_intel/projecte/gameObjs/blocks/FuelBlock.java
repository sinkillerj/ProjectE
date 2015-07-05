package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public class FuelBlock extends Block 
{
	@SideOnly(Side.CLIENT)
	private IIcon icons[];
	
	public FuelBlock() 
	{
		super(Material.rock);
		this.setBlockName("pe_fuel_block");
		this.setCreativeTab(ObjHandler.cTab);
		this.setHardness(0.5f);
	}
	
	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item fuelBlock, CreativeTabs cTab, List list)
	{
		for (int i = 0; i < 3; i++)
		{
			list.add(new ItemStack(fuelBlock , 1, i));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register)
	{
		icons = new IIcon[3];
		
		for (int i = 0; i < 3; i++)
		{
			icons[i] = register.registerIcon("projecte:fuels_"+i);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		return icons[MathHelper.clamp_int(meta, 0, 2)];
	}
}
