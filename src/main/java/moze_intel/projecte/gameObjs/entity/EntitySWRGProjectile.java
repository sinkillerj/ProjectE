package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySWRGProjectile extends PEProjectile
{
	private boolean fromArcana = false;

	public EntitySWRGProjectile(World world)
	{
		super(world);
	}

	public EntitySWRGProjectile(World world, EntityPlayer player, boolean fromArcana)
	{
		super(world, player);
		this.fromArcana = fromArcana;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (!world.isRemote && ticksExisted > 400)
		{
			setDead();
			return;
		}

		// Undo the 0.99 (0.8 in water) drag applied in superclass
		double inverse = 1D / (isInWater() ? 0.8D : 0.99D);
		motionX *= inverse;
		motionY *= inverse;
		motionZ *= inverse;

		if (!world.isRemote && !isDead && posY > world.getHeight() && world.isRaining())
		{
			world.getWorldInfo().setThundering(true);
			setDead();
		}
	}

	@Override
	protected void apply(RayTraceResult mop)
	{
		if (world.isRemote)
		{
			return;
		}

		ItemPE consumeFrom = (ItemPE) (fromArcana ? ObjHandler.arcana : ObjHandler.swrg);

		switch (mop.typeOfHit)
		{
			case BLOCK:
			{
				if(tryConsumeEmc(consumeFrom, 768))
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
				if (mop.entityHit instanceof EntityLivingBase && tryConsumeEmc(consumeFrom, 64))
				{
					EntityPlayer player = (EntityPlayer) getThrower();

					// Minor damage so we count as the attacker for launching the mob
					mop.entityHit.attackEntityFrom(DamageSource.causePlayerDamage(player), 1F);

					// Fake onGround before knockBack so you can re-launch mobs that have already been launched
					boolean oldOnGround = mop.entityHit.onGround;
					mop.entityHit.onGround = true;
					((EntityLivingBase) mop.entityHit).knockBack(null, 5F, -motionX * 0.25, -motionZ * 0.25);
					mop.entityHit.onGround = oldOnGround;
					mop.entityHit.motionY *= 3;
				}

				break;
			}
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		fromArcana = compound.getBoolean("fromArcana");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		compound.setBoolean("fromArcana", fromArcana);
	}
}
