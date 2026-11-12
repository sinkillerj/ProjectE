package moze_intel.projecte.emc.components.processor;

import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.components.DataComponentProcessor;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.config.PEConfigTranslations;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@DataComponentProcessor
public class ArmorTrimProcessor extends PersistentComponentProcessor<ArmorTrim> {

	@Override
	public String getName() {
		return PEConfigTranslations.DCP_ARMOR_TRIM.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.DCP_ARMOR_TRIM.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.DCP_ARMOR_TRIM.tooltip();
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC, @NotNull ArmorTrim trim) throws ArithmeticException {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return 0;
        long cheapest = Long.MAX_VALUE;
        var base = info.createStack();
        base.remove(DataComponents.TRIM);
        for (var material : BuiltInRegistries.ITEM.listElements().toList()) {
            if (!trim.material().equals(material.value().components().get(DataComponents.PROVIDES_TRIM_MATERIAL))) continue;
            long materialEmc = IEMCProxy.INSTANCE.getValue(material);
            if (materialEmc == 0) continue;
            var materialStack = new ItemStack(material);
            for (var holder : server.getRecipeManager().recipeMap().byType(RecipeType.SMITHING)) {
                if (!(holder.value() instanceof SmithingTrimRecipe recipe)) continue;
                for (var template : recipe.template.items().toList()) {
                    long templateEmc = IEMCProxy.INSTANCE.getValue(template);
                    if (templateEmc == 0) continue;
                    var input = new SmithingRecipeInput(
                            new ItemStack(template), base, materialStack);
                    var result = recipe.assemble(input);
                    if (trim.equals(result.get(DataComponents.TRIM))) {
                        cheapest = Math.min(cheapest, Math.addExact(materialEmc, templateEmc));
                    }
                }
            }
        }
        return cheapest == Long.MAX_VALUE ? 0 : Math.addExact(currentEMC, cheapest);
	}

	@Override
	protected boolean validItem(@NotNull ItemInfo info) {
		return info.getItem().is(ItemTags.TRIMMABLE_ARMOR);
	}

	@Override
	protected boolean shouldPersist(@NotNull ItemInfo info, @NotNull ArmorTrim component) {
		return true;
	}

	@Override
	protected DataComponentType<ArmorTrim> getComponentType(@NotNull ItemInfo info) {
		return DataComponents.TRIM;
	}
}
