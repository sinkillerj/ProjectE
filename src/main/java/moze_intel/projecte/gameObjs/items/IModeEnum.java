package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.world.item.ItemStack;

public interface IModeEnum<MODE extends Enum<MODE> & IModeEnum<MODE>> extends IHasTranslationKey {

	MODE next(ItemStack stack);
}