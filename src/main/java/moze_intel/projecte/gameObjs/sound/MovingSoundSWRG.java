package moze_intel.projecte.gameObjs.sound;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// [VanillaCopy] MovingSoundMinecart
@SideOnly(Side.CLIENT)
public class MovingSoundSWRG extends MovingSound
{
	private final EntitySWRGProjectile swrgProjectile;
	private float distance = 0.0F;

	public MovingSoundSWRG(EntitySWRGProjectile swrgProjectile)
	{
		super(PESounds.WIND, SoundCategory.WEATHER);
		this.swrgProjectile = swrgProjectile;
		this.volume = 0.6F;
	}

	@Override
	public void update()
	{
		if (this.swrgProjectile.isDead)
		{
			this.donePlaying = true;
		}
		else
		{
			this.xPosF = (float) this.swrgProjectile.posX;
			this.yPosF = (float) this.swrgProjectile.posY;
			this.zPosF = (float) this.swrgProjectile.posZ;
			float f = MathHelper.sqrt(this.swrgProjectile.motionX * this.swrgProjectile.motionX + this.swrgProjectile.motionZ * this.swrgProjectile.motionZ);

			if ((double) f >= 0.01D)
			{
				this.distance = MathHelper.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
				this.volume = 0.0F + MathHelper.clamp(f, 0.0F, 0.5F) * 0.7F;
			}
			else
			{
				this.distance = 0.0F;
				this.volume = 0.0F;
			}
		}
	}
}