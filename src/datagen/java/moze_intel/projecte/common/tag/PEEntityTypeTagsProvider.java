package moze_intel.projecte.common.tag;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.PECore;
import moze_intel.projecte.common.datagen.RegistryKeyTagAppender;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import org.jetbrains.annotations.NotNull;

public class PEEntityTypeTagsProvider extends EntityTypeTagsProvider {

	public PEEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, PECore.MODID);
	}

	@Override
	protected RegistryKeyTagAppender<EntityType<?>> tag(TagKey<EntityType<?>> tagKey) {
		return RegistryKeyTagAppender.wrap(super.tag(tagKey), BuiltInRegistries.ENTITY_TYPE);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		//Note: Intentionally does not include Axolotls, Allays, or Sniffers
		tag(PETags.Entities.RANDOMIZER_PEACEFUL).add(
				EntityTypes.ARMADILLO,
				EntityTypes.BAT,
				EntityTypes.BEE,
				EntityTypes.CAMEL,
				EntityTypes.CAT,
				EntityTypes.CHICKEN,
				EntityTypes.COD,
				EntityTypes.COW,
				EntityTypes.DOLPHIN,
				EntityTypes.DONKEY,
				EntityTypes.FOX,
				EntityTypes.FROG,
				EntityTypes.GLOW_SQUID,
				EntityTypes.GOAT,
				EntityTypes.HORSE,
				EntityTypes.LLAMA,
				EntityTypes.MOOSHROOM,
				EntityTypes.MULE,
				EntityTypes.OCELOT,
				EntityTypes.PANDA,
				EntityTypes.PARROT,
				EntityTypes.PIG,
				EntityTypes.POLAR_BEAR,
				EntityTypes.PUFFERFISH,
				EntityTypes.RABBIT,
				EntityTypes.SALMON,
				EntityTypes.SHEEP,
				EntityTypes.SQUID,
				EntityTypes.STRIDER,
				EntityTypes.TADPOLE,
				EntityTypes.TRADER_LLAMA,
				EntityTypes.TROPICAL_FISH,
				EntityTypes.TURTLE,
				EntityTypes.VILLAGER,
				EntityTypes.WANDERING_TRADER,
				EntityTypes.WOLF
		);
		tag(PETags.Entities.RANDOMIZER_HOSTILE).add(
				EntityTypes.BLAZE,
				EntityTypes.BOGGED,
				EntityTypes.BREEZE,
				EntityTypes.CREEPER,
				EntityTypes.DROWNED,
				EntityTypes.ENDERMAN,
				EntityTypes.ENDERMITE,
				EntityTypes.EVOKER,
				EntityTypes.GHAST,
				EntityTypes.GUARDIAN,
				EntityTypes.HOGLIN,
				EntityTypes.HUSK,
				EntityTypes.PHANTOM,
				EntityTypes.PIGLIN,
				EntityTypes.PIGLIN_BRUTE,
				EntityTypes.PILLAGER,
				EntityTypes.RABBIT,
				EntityTypes.SHULKER,
				EntityTypes.SILVERFISH,
				EntityTypes.SKELETON,
				EntityTypes.SKELETON_HORSE,
				EntityTypes.SLIME,
				EntityTypes.SPIDER,
				EntityTypes.STRAY,
				EntityTypes.VEX,
				EntityTypes.VINDICATOR,
				EntityTypes.WITCH,
				EntityTypes.WITHER_SKELETON,
				EntityTypes.ZOGLIN,
				EntityTypes.ZOMBIE,
				EntityTypes.ZOMBIE_HORSE,
				EntityTypes.ZOMBIE_VILLAGER,
				EntityTypes.ZOMBIFIED_PIGLIN
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
