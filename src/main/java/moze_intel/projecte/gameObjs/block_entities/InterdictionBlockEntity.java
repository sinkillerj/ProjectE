package moze_intel.projecte.gameObjs.block_entities;

import java.util.function.Predicate;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class InterdictionBlockEntity extends BlockEntity {

	private static final Predicate<Entity> INTERDICTION_REPEL_PREDICATE = entity -> WorldHelper.validRepelEntity(entity, PETags.Entities.BLACKLIST_INTERDICTION);
	//Note: We don't need to check if the projectile entity is on the ground here or not, as if it is we would not get past validRepelEntity
	private static final Predicate<Entity> INTERDICTION_REPEL_HOSTILE_PREDICATE = INTERDICTION_REPEL_PREDICATE.and(entity -> entity instanceof Enemy || entity instanceof Projectile);

	public InterdictionBlockEntity(BlockPos pos, BlockState state) {
		this(PEBlockEntityTypes.INTERDICTION_TORCH.get(), pos, state);
	}

	public InterdictionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, InterdictionBlockEntity torch) {
		//Note: The interdiction torch's ticker needs to be run on both sides to ensure it renders properly
		// when it deflects things like projectiles
		Vec3 point = pos.getCenter();
		Predicate<Entity> repelPredicate = ProjectEConfig.server.effects.interdictionMode.get() ? INTERDICTION_REPEL_HOSTILE_PREDICATE : INTERDICTION_REPEL_PREDICATE;
		for (Entity ent : level.getEntitiesOfClass(Entity.class, new AABB(pos).inflate(8), repelPredicate)) {
			WorldHelper.repelEntity(point, ent);
		}
	}
}