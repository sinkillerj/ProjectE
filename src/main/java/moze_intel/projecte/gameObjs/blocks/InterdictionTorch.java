package moze_intel.projecte.gameObjs.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.InterdictionTile;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

public class InterdictionTorch extends BlockTorch
{
	public InterdictionTorch()
	{
		this.setCreativeTab(ObjHandler.cTab);
		this.setBlockName("pe_interdiction_torch");
		this.setLightLevel(0.9F);
		this.setTickRandomly(true);
	}

	@Override
	public boolean hasTileEntity(int meta)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta)
	{
		return new InterdictionTile();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand)
	{
		int l = world.getBlockMetadata(x, y, z);
		double d0 = (double)((float)x + 0.5F);
		double d1 = (double)((float)y + 0.7F);
		double d2 = (double)((float)z + 0.5F);
		double d3 = 0.2199999988079071D;
		double d4 = 0.27000001072883606D;

		if (l == 1)
			world.spawnParticle("smoke", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
		else if (l == 2)
			world.spawnParticle("smoke", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
		else if (l == 3)
			world.spawnParticle("smoke", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
		else if (l == 4)
			world.spawnParticle("smoke", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
		else
			world.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register)
	{
		this.blockIcon = register.registerIcon("projecte:interdiction_torch");
	}
}
