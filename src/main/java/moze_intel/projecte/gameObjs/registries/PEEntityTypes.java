package moze_intel.projecte.gameObjs.registries;

import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.gameObjs.registration.impl.EntityTypeDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.EntityTypeRegistryObject;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;

public class PEEntityTypes {

	public static final EntityTypeDeferredRegister ENTITY_TYPES = new EntityTypeDeferredRegister();

	public static final EntityTypeRegistryObject<EntityFireProjectile> FIRE_PROJECTILE = ENTITY_TYPES.register("fire_projectile", EntityType.Builder.<EntityFireProjectile>of(EntityFireProjectile::new, MobCategory.MISC).setTrackingRange(256).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntityHomingArrow> HOMING_ARROW = ENTITY_TYPES.register("homing_arrow", EntityType.Builder.<EntityHomingArrow>of(EntityHomingArrow::new, MobCategory.MISC).setTrackingRange(5).setUpdateInterval(20).setShouldReceiveVelocityUpdates(true));
	public static final EntityTypeRegistryObject<EntityLavaProjectile> LAVA_PROJECTILE = ENTITY_TYPES.register("lava_projectile", EntityType.Builder.<EntityLavaProjectile>of(EntityLavaProjectile::new, MobCategory.MISC).setTrackingRange(256).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntityLensProjectile> LENS_PROJECTILE = ENTITY_TYPES.register("lens_projectile", EntityType.Builder.<EntityLensProjectile>of(EntityLensProjectile::new, MobCategory.MISC).setTrackingRange(256).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntityMobRandomizer> MOB_RANDOMIZER = ENTITY_TYPES.register("mob_randomizer", EntityType.Builder.<EntityMobRandomizer>of(EntityMobRandomizer::new, MobCategory.MISC).setTrackingRange(256).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntityNovaCatalystPrimed> NOVA_CATALYST_PRIMED = ENTITY_TYPES.register("nova_catalyst_primed", EntityType.Builder.<EntityNovaCatalystPrimed>of(EntityNovaCatalystPrimed::new, MobCategory.MISC).setTrackingRange(10).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntityNovaCataclysmPrimed> NOVA_CATACLYSM_PRIMED = ENTITY_TYPES.register("nova_cataclysm_primed", EntityType.Builder.<EntityNovaCataclysmPrimed>of(EntityNovaCataclysmPrimed::new, MobCategory.MISC).setTrackingRange(10).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntitySWRGProjectile> SWRG_PROJECTILE = ENTITY_TYPES.register("swrg_projectile", EntityType.Builder.<EntitySWRGProjectile>of(EntitySWRGProjectile::new, MobCategory.MISC).setTrackingRange(256).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntityWaterProjectile> WATER_PROJECTILE = ENTITY_TYPES.register("water_projectile", EntityType.Builder.<EntityWaterProjectile>of(EntityWaterProjectile::new, MobCategory.MISC).setTrackingRange(256).setUpdateInterval(10));
}