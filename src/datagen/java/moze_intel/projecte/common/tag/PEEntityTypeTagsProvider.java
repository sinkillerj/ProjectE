package moze_intel.projecte.common.tag;

import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PEEntityTypeTagsProvider extends EntityTypeTagsProvider {

	public PEEntityTypeTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, PECore.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		//Note: Intentionally does not include Axolotls
		tag(PETags.Entities.RANDOMIZER_PEACEFUL).add(
				EntityType.SHEEP,
				EntityType.PIG,
				EntityType.COW,
				EntityType.MOOSHROOM,
				EntityType.CHICKEN,
				EntityType.BAT,
				EntityType.VILLAGER,
				EntityType.SQUID,
				EntityType.OCELOT,
				EntityType.WOLF,
				EntityType.HORSE,
				EntityType.RABBIT,
				EntityType.DONKEY,
				EntityType.MULE,
				EntityType.POLAR_BEAR,
				EntityType.LLAMA,
				EntityType.PARROT,
				EntityType.DOLPHIN,
				EntityType.COD,
				EntityType.SALMON,
				EntityType.PUFFERFISH,
				EntityType.TROPICAL_FISH,
				EntityType.TURTLE,
				EntityType.CAT,
				EntityType.FOX,
				EntityType.PANDA,
				EntityType.TRADER_LLAMA,
				EntityType.WANDERING_TRADER,
				EntityType.STRIDER,
				EntityType.GLOW_SQUID,
				EntityType.GOAT
		);
		tag(PETags.Entities.RANDOMIZER_HOSTILE).add(
				EntityType.ZOMBIE,
				EntityType.SKELETON,
				EntityType.CREEPER,
				EntityType.SPIDER,
				EntityType.ENDERMAN,
				EntityType.SILVERFISH,
				EntityType.ZOMBIFIED_PIGLIN,
				EntityType.PIGLIN,
				EntityType.PIGLIN_BRUTE,
				EntityType.HOGLIN,
				EntityType.ZOGLIN,
				EntityType.GHAST,
				EntityType.BLAZE,
				EntityType.SLIME,
				EntityType.WITCH,
				EntityType.RABBIT,
				EntityType.ENDERMITE,
				EntityType.STRAY,
				EntityType.WITHER_SKELETON,
				EntityType.SKELETON_HORSE,
				EntityType.ZOMBIE_HORSE,
				EntityType.ZOMBIE_VILLAGER,
				EntityType.HUSK,
				EntityType.GUARDIAN,
				EntityType.EVOKER,
				EntityType.VEX,
				EntityType.VINDICATOR,
				EntityType.SHULKER,
				EntityType.DROWNED,
				EntityType.PHANTOM,
				EntityType.PILLAGER
		);
		tag(PETags.Entities.BLACKLIST_SWRG);
		tag(PETags.Entities.BLACKLIST_INTERDICTION);
		//Vanilla tags
		tag(EntityTypeTags.ARROWS).add(PEEntityTypes.HOMING_ARROW.get());
		tag(EntityTypeTags.IMPACT_PROJECTILES).add(
				PEEntityTypes.FIRE_PROJECTILE.get(),
				PEEntityTypes.LAVA_PROJECTILE.get(),
				PEEntityTypes.LENS_PROJECTILE.get(),
				PEEntityTypes.SWRG_PROJECTILE.get(),
				PEEntityTypes.WATER_PROJECTILE.get()
		);
	}
}