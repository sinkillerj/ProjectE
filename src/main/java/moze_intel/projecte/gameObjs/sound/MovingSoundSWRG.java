package moze_intel.projecte.gameObjs.sound;

import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

// [VanillaCopy] MinecartTickableSound
//Only used on the client
public class MovingSoundSWRG extends AbstractTickableSoundInstance {

	private final EntitySWRGProjectile swrgProjectile;
	private float distance = 0.0F;

	public MovingSoundSWRG(EntitySWRGProjectile swrgProjectile) {
		super(PESoundEvents.WIND_MAGIC.get(), SoundSource.WEATHER);
		this.swrgProjectile = swrgProjectile;
		this.volume = 0.6F;
	}

	@Override
	public void tick() {
		if (!this.swrgProjectile.isAlive()) {
			this.stop();
		} else {
			this.x = (float) this.swrgProjectile.getX();
			this.y = (float) this.swrgProjectile.getY();
			this.z = (float) this.swrgProjectile.getZ();
			float f = (float) this.swrgProjectile.getDeltaMovement().horizontalDistance();

			if ((double) f >= 0.01D) {
				this.distance = Mth.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
				this.volume = 0.0F + Mth.clamp(f, 0.0F, 0.5F) * 0.7F;
			} else {
				this.distance = 0.0F;
				this.volume = 0.0F;
			}
		}
	}
}