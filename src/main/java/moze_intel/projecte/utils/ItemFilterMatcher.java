package moze_intel.projecte.utils;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemFilterMatcher {
    private String[][] filters;

    public ItemFilterMatcher(String[] filters) {
        this.filters = new String[filters.length][];
        for (int i = 0; i < filters.length; i++) {
            this.filters[i] = filters[i].split("\\*");
        }
    }

    public boolean matchesAll(String name) {
        for (String[] filterParts: this.filters) {
            if (!stringMatchesFilterParts(name, filterParts))
                return false;
        }
        return true;
    }
    public boolean matchesAll(ItemStack item) {
        if (item == null) return false;
        String name = uniqueOrUnlocalizedName(item);
        return matchesAll(name);
    }

    public boolean matchesAny(String name) {
        for (String[] filterParts: this.filters) {
            if (stringMatchesFilterParts(name, filterParts))
                return true;
        }
        return false;
    }

    public boolean matchesAny(ItemStack item) {
        if (item == null) return false;
        String name = uniqueOrUnlocalizedName(item);
        return matchesAny(name);
    }

    private boolean stringMatchesFilterParts(String text, String[] filter) {
        int lastIndex = 0;
        for (String filterPart: filter) {
            int index = text.indexOf(filterPart, lastIndex);
            if (index < 0) {
                System.out.println(text + " does not match" + filter);
                return false;
            }
            lastIndex = index + filterPart.length();
        }
        System.out.println(text + " does match" + filter);
        return true;
    }

    private String uniqueOrUnlocalizedName(ItemStack itemStack) {
        GameRegistry.UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(itemStack.getItem());
        String name;
        if (uid != null) {
            name = uid.toString();
        } else {
            name = itemStack.getUnlocalizedName();
        }
        return name;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ItemFilterMatcher: ");
        for (String[] filter: this.filters ) {
            for (int i = 0; i < filter.length; i++) {
                if (i > 0) builder.append('*');
                builder.append(filter[i]);
            }
            builder.append(' ');
        }
        return builder.toString();
    }
}
