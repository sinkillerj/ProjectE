package moze_intel.projecte.network.packets.to_client;

import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

public record NovaExplosionSyncPKT(Vec3 explosionCenter, float explosionRadius, SoundEvent explosionSound, List<BlockPos> positions) implements IPEPacket<PlayPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("sync_nova");

	public NovaExplosionSyncPKT(FriendlyByteBuf buffer) {
		this(buffer.readVec3(), buffer.readFloat(), SoundEvent.readFromNetwork(buffer), buffer.readList(FriendlyByteBuf::readBlockPos));
	}

	@NotNull
	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void handle(PlayPayloadContext context) {
		context.level().ifPresent(level -> {
			level.playLocalSound(explosionCenter.x, explosionCenter.y, explosionCenter.z, explosionSound, SoundSource.BLOCKS, 4.0F,
					(1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F, false);
			for (BlockPos pos : positions) {
				Vec3 adjusted = new Vec3(
						pos.getX() + level.random.nextFloat(),
						pos.getY() + level.random.nextFloat(),
						pos.getZ() + level.random.nextFloat()
				);
				Vec3 difference = adjusted.subtract(explosionCenter);
				double d7 = 0.5D / (difference.length() / explosionRadius + 0.1D);
				d7 *= level.random.nextFloat() * level.random.nextFloat() + 0.3F;
				difference = difference.normalize().scale(d7);
				Vec3 adjustedPoof = adjusted.add(explosionCenter).scale(0.5);
				level.addParticle(ParticleTypes.POOF, adjustedPoof.x(), adjustedPoof.y(), adjustedPoof.z(), difference.x(), difference.y(), difference.z());
				level.addParticle(ParticleTypes.SMOKE, adjusted.x(), adjusted.y(), adjusted.z(), difference.x(), difference.y(), difference.z());
			}
		});
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeVec3(explosionCenter);
		buffer.writeFloat(explosionRadius);
		explosionSound.writeToNetwork(buffer);
		buffer.writeCollection(positions, FriendlyByteBuf::writeBlockPos);
	}
}