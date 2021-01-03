package moze_intel.projecte.gameObjs;

import moze_intel.projecte.PECore;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag.INamedTag;

public class PETags {

	public static class Items {

	}

	public static class Blocks {

	}

	public static class Entities {

		public static final INamedTag<EntityType<?>> RANDOMIZER_PEACEFUL = tag("randomizer/peaceful");
		public static final INamedTag<EntityType<?>> RANDOMIZER_HOSTILE = tag("randomizer/hostile");

		private static INamedTag<EntityType<?>> tag(String name) {
			return EntityTypeTags.getTagById(PECore.rl(name).toString());
		}
	}
}