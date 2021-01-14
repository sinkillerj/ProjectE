package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityNovaCataclysmPrimed extends TNTEntity {

	public EntityNovaCataclysmPrimed(EntityType<EntityNovaCataclysmPrimed> type, World world) {
		super(type, world);
		setFuse(getFuse() / 4);
	}

	public EntityNovaCataclysmPrimed(World world, double x, double y, double z, LivingEntity placer) {
		super(world, x, y, z, placer);
		setFuse(getFuse() / 4);
		preventEntitySpawning = true;
	}

	@Nonnull
	@Override
	public EntityType<?> getType() {
		return PEEntityTypes.NOVA_CATACLYSM_PRIMED.get();
	}

	@Override
	protected void explode() {
		WorldHelper.createNovaExplosion(world, this, getPosX(), getPosY(), getPosZ(), 48.0F);
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(PEBlocks.NOVA_CATACLYSM);
	}
}