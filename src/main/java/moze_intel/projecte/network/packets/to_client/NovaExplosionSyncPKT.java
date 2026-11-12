package moze_intel.projecte.network.packets.to_client;

import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.network.PEStreamCodecs;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record NovaExplosionSyncPKT(Vec3 explosionCenter, float explosionRadius, Holder<SoundEvent> explosionSound, List<BlockPos> positions) implements IPEPacket {

	public static final CustomPacketPayload.Type<NovaExplosionSyncPKT> TYPE = new CustomPacketPayload.Type<>(PECore.rl("nova_explosion"));

	public static final StreamCodec<RegistryFriendlyByteBuf, NovaExplosionSyncPKT> STREAM_CODEC = StreamCodec.composite(
			PEStreamCodecs.VEC_3, NovaExplosionSyncPKT::explosionCenter,
			ByteBufCodecs.FLOAT, NovaExplosionSyncPKT::explosionRadius,
			ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT), NovaExplosionSyncPKT::explosionSound,
			BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()), NovaExplosionSyncPKT::positions,
			NovaExplosionSyncPKT::new
	);

	@NotNull
	@Override
	public CustomPacketPayload.Type<NovaExplosionSyncPKT> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		Level level = context.player().level();
		level.playLocalSound(explosionCenter.x, explosionCenter.y, explosionCenter.z, explosionSound.value(), SoundSource.BLOCKS, 4.0F,
				(1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F, false);
		for (BlockPos pos : positions) {
			Vec3 adjusted = Vec3.atLowerCornerWithOffset(pos, level.getRandom().nextFloat(), level.getRandom().nextFloat(), level.getRandom().nextFloat());
			Vec3 difference = adjusted.subtract(explosionCenter);
			double d7 = 0.5D / (difference.length() / explosionRadius + 0.1D);
			d7 *= level.getRandom().nextFloat() * level.getRandom().nextFloat() + 0.3F;
			difference = difference.normalize().scale(d7);
			Vec3 adjustedPoof = adjusted.add(explosionCenter).scale(0.5);
			level.addParticle(ParticleTypes.POOF, adjustedPoof.x(), adjustedPoof.y(), adjustedPoof.z(), difference.x(), difference.y(), difference.z());
			level.addParticle(ParticleTypes.SMOKE, adjusted.x(), adjusted.y(), adjusted.z(), difference.x(), difference.y(), difference.z());
		}
	}
}
