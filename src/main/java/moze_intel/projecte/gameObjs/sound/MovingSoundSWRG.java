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
		this.x = this.swrgProjectile.getX();
		this.y = this.swrgProjectile.getY();
		this.z = this.swrgProjectile.getZ();
		this.volume = 0.6F;
	}

	@Override
	public void tick() {
		if (this.swrgProjectile.isRemoved()) {
			this.stop();
		} else {
			this.x = this.swrgProjectile.getX();
			this.y = this.swrgProjectile.getY();
			this.z = this.swrgProjectile.getZ();
			float f = (float) this.swrgProjectile.getDeltaMovement().horizontalDistance();

			if (f >= 0.01F) {
				this.distance = Mth.clamp(this.distance + 0.0025F, 0.0F, 1.0F);
				this.volume = Mth.lerp(Mth.clamp(f, 0.0F, 0.5F), 0.0F, 0.7F);
			} else {
				this.distance = 0.0F;
				this.volume = 0.0F;
			}
		}
	}
}