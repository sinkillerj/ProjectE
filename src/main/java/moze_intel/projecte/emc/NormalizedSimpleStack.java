package moze_intel.projecte.emc;

import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;
import org.omg.CORBA.ORB;
import scala.Int;
import scala.actors.threadpool.Arrays;

import java.util.*;

public class NormalizedSimpleStack {
    public int id;
    public int damage;
    public static Map<Integer, Set<Integer>> idWithUsedMetaData = new HashMap<Integer, Set<Integer>>();
    public static NormalizedSimpleStack getNormalizedSimpleStackFor(int id, int damage) {
        NormalizedSimpleStack normStack = new NormalizedSimpleStack(id,damage);
        Set<Integer> usedMetadata;
        if (!idWithUsedMetaData.containsKey(normStack.id)) {
            usedMetadata = new HashSet<Integer>();
            idWithUsedMetaData.put(normStack.id,usedMetadata);
        } else {
            usedMetadata = idWithUsedMetaData.get(normStack.id);
        }
        usedMetadata.add(normStack.damage);
        return normStack;
    }
    public static NormalizedSimpleStack getNormalizedSimpleStackFor(Block block) {
        return getNormalizedSimpleStackFor(new ItemStack(block));
    }

    public static NormalizedSimpleStack getNormalizedSimpleStackFor(Item item) {
        return getNormalizedSimpleStackFor(Item.itemRegistry.getIDForObject(item), 0);
    }

    public static NormalizedSimpleStack getNormalizedSimpleStackFor(ItemStack stack) {
        return getNormalizedSimpleStackFor(Item.itemRegistry.getIDForObject(stack.getItem()), stack.getItemDamage());
    }

    public static NormalizedSimpleStack getNormalizedSimpleStackFor(Fluid fluid) {
        return getNormalizedSimpleStackFor(fluid.getBlock());
    }

    public static void addMappings(IMappingCollector<NormalizedSimpleStack> mapper) {
        for(Map.Entry<Integer,Set<Integer>> entry: idWithUsedMetaData.entrySet()) {
            entry.getValue().remove(OreDictionary.WILDCARD_VALUE);
            entry.getValue().add(0);
            NormalizedSimpleStack stackWildcard = new NormalizedSimpleStack(entry.getKey(), OreDictionary.WILDCARD_VALUE);
            for (int metadata: entry.getValue()) {
                mapper.addConversion(1, stackWildcard, Arrays.asList(new Object[]{new NormalizedSimpleStack(entry.getKey(), metadata)}));
            }
        }
    }

    private NormalizedSimpleStack(int id, int damage)
    {
        this.id = id;
        if (this.id == -1) {
            throw new IllegalArgumentException("Invalid Item with getIDForObject() == -1");
        }
        this.damage = damage;
    }

    public ItemStack toItemStack()
    {
        Item item = Item.getItemById(id);

        if (item != null)
        {
            return new ItemStack(Item.getItemById(id), 1, damage);
        }
        return null;
    }

    public NormalizedSimpleStack copy()
    {
        return new NormalizedSimpleStack(id, damage);
    }

    @Override
    public int hashCode()
    {
        return id;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof NormalizedSimpleStack)
        {
            NormalizedSimpleStack other = (NormalizedSimpleStack) obj;

            return this.id == other.id && this.damage == other.damage;
        }

        return false;
    }

    @Override
    public String toString()
    {
        Object obj = Item.itemRegistry.getObjectById(id);

        if (obj != null)
        {
            return  "" + Item.itemRegistry.getNameForObject(obj) + " " + (damage == OreDictionary.WILDCARD_VALUE ? "*"  : damage);
        }

        return "id:" + id + " damage:" + (damage == OreDictionary.WILDCARD_VALUE ? "*"  : damage);
    }
}
