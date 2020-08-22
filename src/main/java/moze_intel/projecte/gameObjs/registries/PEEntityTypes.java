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
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;

public class PEEntityTypes {

	public static final EntityTypeDeferredRegister ENTITY_TYPES = new EntityTypeDeferredRegister();

	public static final EntityTypeRegistryObject<EntityFireProjectile> FIRE_PROJECTILE = ENTITY_TYPES.register("fire_projectile", EntityType.Builder.<EntityFireProjectile>create(EntityFireProjectile::new, EntityClassification.MISC).setTrackingRange(256).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntityHomingArrow> HOMING_ARROW = ENTITY_TYPES.register("homing_arrow", EntityType.Builder.<EntityHomingArrow>create(EntityHomingArrow::new, EntityClassification.MISC).setTrackingRange(5).setUpdateInterval(20).setShouldReceiveVelocityUpdates(true));
	public static final EntityTypeRegistryObject<EntityLavaProjectile> LAVA_PROJECTILE = ENTITY_TYPES.register("lava_projectile", EntityType.Builder.<EntityLavaProjectile>create(EntityLavaProjectile::new, EntityClassification.MISC).setTrackingRange(256).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntityLensProjectile> LENS_PROJECTILE = ENTITY_TYPES.register("lens_projectile", EntityType.Builder.<EntityLensProjectile>create(EntityLensProjectile::new, EntityClassification.MISC).setTrackingRange(256).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntityMobRandomizer> MOB_RANDOMIZER = ENTITY_TYPES.register("mob_randomizer", EntityType.Builder.<EntityMobRandomizer>create(EntityMobRandomizer::new, EntityClassification.MISC).setTrackingRange(256).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntityNovaCatalystPrimed> NOVA_CATALYST_PRIMED = ENTITY_TYPES.register("nova_catalyst_primed", EntityType.Builder.<EntityNovaCatalystPrimed>create(EntityNovaCatalystPrimed::new, EntityClassification.MISC).setTrackingRange(10).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntityNovaCataclysmPrimed> NOVA_CATACLYSM_PRIMED = ENTITY_TYPES.register("nova_cataclysm_primed", EntityType.Builder.<EntityNovaCataclysmPrimed>create(EntityNovaCataclysmPrimed::new, EntityClassification.MISC).setTrackingRange(10).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntitySWRGProjectile> SWRG_PROJECTILE = ENTITY_TYPES.register("swrg_projectile", EntityType.Builder.<EntitySWRGProjectile>create(EntitySWRGProjectile::new, EntityClassification.MISC).setTrackingRange(256).setUpdateInterval(10));
	public static final EntityTypeRegistryObject<EntityWaterProjectile> WATER_PROJECTILE = ENTITY_TYPES.register("water_projectile", EntityType.Builder.<EntityWaterProjectile>create(EntityWaterProjectile::new, EntityClassification.MISC).setTrackingRange(256).setUpdateInterval(10));
}