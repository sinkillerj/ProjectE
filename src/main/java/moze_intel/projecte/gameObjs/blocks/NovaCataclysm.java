package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class NovaCataclysm extends NovaCatalyst
{
	public NovaCataclysm()
	{
		this.setUnlocalizedName("pe_nova_cataclysm");
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

		EntityNovaCataclysmPrimed ent = new EntityNovaCataclysmPrimed(world, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), entity); 
		world.spawnEntityInWorld(ent);
		world.playSoundAtEntity(ent, "game.tnt.primed", 1.0F, 1.0F);
	}
}
