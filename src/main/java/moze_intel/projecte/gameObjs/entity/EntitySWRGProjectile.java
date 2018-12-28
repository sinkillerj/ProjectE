package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySWRGProjectile extends EntityThrowable
{
	private boolean fromArcana = false;

	public EntitySWRGProjectile(World world)
	{
		super(ObjHandler.SWRG_PROJECTILE, world);
	}

	public EntitySWRGProjectile(EntityPlayer player, boolean fromArcana, World world)
	{
		super(ObjHandler.SWRG_PROJECTILE, player, world);
		this.fromArcana = fromArcana;
	}

	@Override
	public void tick()
	{
		super.tick();

		if (!world.isRemote && ticksExisted > 400)
		{
			remove();
			return;
		}

		// Undo the 0.99 (0.8 in water) drag applied in superclass
		double inverse = 1D / (isInWater() ? 0.8D : 0.99D);
		motionX *= inverse;
		motionY *= inverse;
		motionZ *= inverse;

		if (!world.isRemote && isAlive() && posY > world.getHeight() && world.isRaining())
		{
			world.getWorldInfo().setThundering(true);
			remove();
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
		if (world.isRemote)
		{
			return;
		}

		if (!(getThrower() instanceof EntityPlayer))
		{
			remove();
			return;
		}

		EntityPlayer player = ((EntityPlayer) getThrower());
		ItemStack found = PlayerHelper.findFirstItem(player, fromArcana ? ObjHandler.arcana : ObjHandler.swrg);

		switch (mop.type)
		{
			case BLOCK:
			{
				if(!found.isEmpty() && ItemPE.consumeFuel(player, found, 768, true))
				{
					BlockPos pos = mop.getBlockPos();

					EntityLightningBolt lightning = new EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ(), false);
					world.addWeatherEffect(lightning);

					if (world.isThundering())
					{
						for (int i = 0; i < 3; i++)
						{
							EntityLightningBolt bonus = new EntityLightningBolt(world, pos.getX() + world.rand.nextGaussian(), pos.getY() + world.rand.nextGaussian(), pos.getZ() + world.rand.nextGaussian(), false);
							world.addWeatherEffect(bonus);
						}
					}
				}

				break;
			}
			case ENTITY:
			{
				if (mop.entity instanceof EntityLivingBase && !found.isEmpty() && ItemPE.consumeFuel(player, found, 64, true))
				{
					// Minor damage so we count as the attacker for launching the mob
					mop.entity.attackEntityFrom(DamageSource.causePlayerDamage(player), 1F);

					// Fake onGround before knockBack so you can re-launch mobs that have already been launched
					boolean oldOnGround = mop.entity.onGround;
					mop.entity.onGround = true;
					((EntityLivingBase) mop.entity).knockBack(null, 5F, -motionX * 0.25, -motionZ * 0.25);
					mop.entity.onGround = oldOnGround;
					mop.entity.motionY *= 3;
				}

				break;
			}
		}
		remove();
	}

	@Override
	public void readAdditional(NBTTagCompound compound)
	{
		super.readAdditional(compound);
		fromArcana = compound.getBoolean("fromArcana");
	}

	@Override
	public void writeAdditional(NBTTagCompound compound)
	{
		super.writeAdditional(compound);
		compound.putBoolean("fromArcana", fromArcana);
	}
}
