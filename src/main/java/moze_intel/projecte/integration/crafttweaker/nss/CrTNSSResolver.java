package moze_intel.projecte.integration.crafttweaker.nss;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.tag.MCTag;
import com.google.gson.JsonParseException;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.emc.json.NSSSerializer;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.projecte.NSSResolver")
public class CrTNSSResolver {

	@ZenCodeType.Method
	public static NSSCrT deserialize(String representation) {
		if (checkNonNull(representation)) {
			try {
				return new NSSCrT(NSSSerializer.INSTANCE.deserialize(representation));
			} catch (JsonParseException e) {
				CraftTweakerAPI.logError("Error deserializing NSS string representation: " + e.getMessage());
			}
		}
		return null;
	}

	@ZenCodeType.Method
	public static NSSCrT fromItem(IItemStack stack) {
		if (checkNonNull(stack)) {
			return new NSSCrT(NSSItem.createItem(stack.getInternal()));
		}
		return null;
	}

	@ZenCodeType.Method
	public static NSSCrT fromItemTag(MCTag tag) {
		if (checkNonNull(tag)) {
			ITag<Item> itemTag = tag.getItemTag();
			if (checkNonNull(itemTag)) {
				return new NSSCrT(NSSItem.createTag(itemTag));
			}
		}
		return null;
	}

	private static boolean checkNonNull(Object object) {
		if (object == null) {
			CraftTweakerAPI.logError("The NSS Representation cannot be null.");
			return false;
		}
		return true;
	}
}