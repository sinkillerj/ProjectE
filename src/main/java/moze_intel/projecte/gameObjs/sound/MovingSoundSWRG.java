package moze_intel.projecte.gameObjs.sound;

import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

// [VanillaCopy] MovingSoundMinecart
//Only used on the client
public class MovingSoundSWRG extends TickableSound {//TODO - 1.16: Double check this copy of vanilla and also check other vanilla copies we have

	private final EntitySWRGProjectile swrgProjectile;
	private float distance = 0.0F;

	public MovingSoundSWRG(EntitySWRGProjectile swrgProjectile) {
		super(PESoundEvents.WIND_MAGIC.get(), SoundCategory.WEATHER);
		this.swrgProjectile = swrgProjectile;
		this.volume = 0.6F;
	}

	@Override
	public void tick() {
		if (!this.swrgProjectile.isAlive()) {
			this.finishPlaying();
		} else {
			this.x = (float) this.swrgProjectile.getPosX();
			this.y = (float) this.swrgProjectile.getPosY();
			this.z = (float) this.swrgProjectile.getPosZ();
			float f = MathHelper.sqrt(Entity.horizontalMag(this.swrgProjectile.getMotion()));

			if ((double) f >= 0.01D) {
				this.distance = MathHelper.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
				this.volume = 0.0F + MathHelper.clamp(f, 0.0F, 0.5F) * 0.7F;
			} else {
				this.distance = 0.0F;
				this.volume = 0.0F;
			}
		}
	}
}