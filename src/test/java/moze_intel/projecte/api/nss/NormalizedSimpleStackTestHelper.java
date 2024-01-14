package moze_intel.projecte.api.nss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class NormalizedSimpleStackTestHelper {

	protected static NSSItem createItem(String location) {
		return NSSItem.createItem(new ResourceLocation(location));
	}

	protected static NSSItem createItem(String namespace, String path) {
		return NSSItem.createItem(new ResourceLocation(namespace, path));
	}

	protected static NSSItem createItem(String namespace, String path, CompoundTag nbt) {
		return NSSItem.createItem(new ResourceLocation(namespace, path), nbt);
	}

	protected static NSSItem createTag(String namespace, String path) {
		return NSSItem.createTag(new ResourceLocation(namespace, path));
	}

	protected static NSSFluid createFluid(String location) {
		return NSSFluid.createFluid(new ResourceLocation(location));
	}

	protected static NSSFluid createFluid(String namespace, String path) {
		return NSSFluid.createFluid(new ResourceLocation(namespace, path));
	}

	protected static NSSFluid createFluid(String namespace, String path, CompoundTag nbt) {
		return NSSFluid.createFluid(new ResourceLocation(namespace, path), nbt);
	}

	protected static NSSFluid createFluidTag(String namespace, String path) {
		return NSSFluid.createTag(new ResourceLocation(namespace, path));
	}
}