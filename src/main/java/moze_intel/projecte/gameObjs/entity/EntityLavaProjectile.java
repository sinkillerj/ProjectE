package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

public class EntityLavaProjectile extends EntityThrowable
{
	public EntityLavaProjectile(World world) 
	{
		super(ObjHandler.LAVA_PROJECTILE, world);
	}

	public EntityLavaProjectile(EntityPlayer entity, World world)
	{
		super(ObjHandler.LAVA_PROJECTILE, entity, world);
	}

	@Override
	public void tick()
	{
		super.tick();
		
		if (!this.getEntityWorld().isRemote)
		{
			if (ticksExisted > 400 || !this.getEntityWorld().isBlockLoaded(new BlockPos(this)))
			{
				this.remove();
				return;
			}

			if (getThrower() instanceof EntityPlayerMP)
			{
				EntityPlayerMP player = ((EntityPlayerMP) getThrower());
				for (BlockPos pos : BlockPos.getAllInBox(this.getPosition().add(-3, -3, -3), this.getPosition().add(3, 3, 3)))
                {
                    Block block = this.getEntityWorld().getBlockState(pos).getBlock();

                    if (block == Blocks.WATER)
                    {
                        if (PlayerHelper.hasBreakPermission(player, pos))
                        {
                            this.getEntityWorld().removeBlock(pos);
                            this.getEntityWorld().playSound(null, pos, SoundEvents.ENTITY_BLAZE_BURN, SoundCategory.BLOCKS, 0.5F, 2.6F + (this.getEntityWorld().rand.nextFloat() - this.getEntityWorld().rand.nextFloat()) * 0.8F);
                        }
                    }
                }
			}

			if (this.posY > 128)
			{
				WorldInfo worldInfo = this.getEntityWorld().getWorldInfo();
				worldInfo.setRaining(false);
				this.remove();
			}
		}
	}

	@Override
	public float getGravityVelocity()
	{
		return 0;
	}

	@Override
	protected void onImpact(RayTraceResult mop)
	{
		if (!this.getEntityWorld().isRemote || getThrower() instanceof EntityPlayer)
		{
			EntityPlayer player = ((EntityPlayer) getThrower());
			ItemStack found = PlayerHelper.findFirstItem(player, ObjHandler.volcanite);
			if (!found.isEmpty() && ItemPE.consumeFuel(player, found, 32, true))
			{
				switch (mop.type)
				{
					case BLOCK:
						PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) getThrower()), mop.getBlockPos().offset(mop.sideHit), Blocks.LAVA.getDefaultState());
						break;
					case ENTITY:
						Entity ent = mop.entity;
						ent.setFire(5);
						ent.attackEntityFrom(DamageSource.IN_FIRE, 5);
				}
			}
		}

		if (!world.isRemote)
		{
			remove();
		}
	}
}
