package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class EntityNovaCataclysmPrimed extends PrimedTnt {

	public EntityNovaCataclysmPrimed(EntityType<EntityNovaCataclysmPrimed> type, Level world) {
		super(type, world);
		setFuse(getFuse() / 4);
	}

	public EntityNovaCataclysmPrimed(Level world, double x, double y, double z, LivingEntity placer) {
		super(world, x, y, z, placer);
		setFuse(getFuse() / 4);
		blocksBuilding = true;
	}

	@Nonnull
	@Override
	public EntityType<?> getType() {
		return PEEntityTypes.NOVA_CATACLYSM_PRIMED.get();
	}

	@Override
	protected void explode() {
		WorldHelper.createNovaExplosion(level, this, getX(), getY(), getZ(), 48.0F);
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		return new ItemStack(PEBlocks.NOVA_CATACLYSM);
	}
}