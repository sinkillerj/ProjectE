package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.api.state.PEStateProps;
import moze_intel.projecte.api.state.enums.EnumFuelType;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class FuelBlock extends Block 
{
	public final EnumFuelType fuelType;
	public FuelBlock(Builder builder, EnumFuelType type)
	{
		super(builder/*Material.ROCK*/);
		this.fuelType = type;
		this.setTranslationKey("pe_fuel_block");
		this.setCreativeTab(ObjHandler.cTab);
		this.setHardness(0.5f);
	}
}
