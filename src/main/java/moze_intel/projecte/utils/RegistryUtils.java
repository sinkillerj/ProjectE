package moze_intel.projecte.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class RegistryUtils {

    private RegistryUtils() {
    }

    public static ResourceLocation getName(Item element) {
        return getName(ForgeRegistries.ITEMS, element);
    }

    public static String getPath(Item element) {
        return getName(element).getPath();
    }

    public static ResourceLocation getName(Block element) {
        return getName(ForgeRegistries.BLOCKS, element);
    }

    public static String getPath(Block element) {
        return getName(element).getPath();
    }

    public static ResourceLocation getName(EntityType<?> element) {
        return getName(ForgeRegistries.ENTITIES, element);
    }

    public static ResourceLocation getName(Potion element) {
        return getName(ForgeRegistries.POTIONS, element);
    }

    public static ResourceLocation getName(RecipeSerializer<?> element) {
        return getName(ForgeRegistries.RECIPE_SERIALIZERS, element);
    }

    private static <T> ResourceLocation getName(IForgeRegistry<T> registry, T element) {
        return registry.getKey(element);
    }
}