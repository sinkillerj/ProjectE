package moze_intel.projecte.gameObjs.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class NovaCatalyst extends BlockTNT
{
	@SideOnly(Side.CLIENT)
	protected IIcon topIcon;
	@SideOnly(Side.CLIENT)
	protected IIcon bottomIcon;
	
	public NovaCatalyst()
	{
		this.setBlockName("pe_nova_catalyst");
		this.setCreativeTab(ObjHandler.cTab);
	}
	
	@Override
	public void func_150114_a(World world, int x, int y, int z, int par5, EntityLivingBase entity)
	{
		if (world.isRemote || par5 != 1)
		{
			return;
		}
		
		if (entity == null)
		{
			entity = world.getClosestPlayer(x, y, z, 64);
		}

		EntityNovaCatalystPrimed ent = new EntityNovaCatalystPrimed(world, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), entity); 
		world.spawnEntityInWorld(ent);
		world.playSoundAtEntity(ent, "game.tnt.primed", 1.0F, 1.0F);
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion)
	{
		func_150114_a(world, x, y, z, 1, null);
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_)
	{
		return p_149691_1_ == 0 ? bottomIcon : (p_149691_1_ == 1 ? topIcon : this.blockIcon);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register)
	{
		this.blockIcon = register.registerIcon("projecte:explosives/nova_side");
		topIcon = register.registerIcon("projecte:explosives/top");
		bottomIcon = register.registerIcon("projecte:explosives/bottom");
	}
}
