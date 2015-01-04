package moze_intel.projecte.emc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class NormalizedSimpleStack {
    public int id;
    public int damage;

    public NormalizedSimpleStack(int id, int damage)
    {
        this.id = id;
        if (this.id == -1) {
            throw new IllegalArgumentException("Invalid Item with getIDForObject() == -1");
        }
        this.damage = damage;
    }

    public NormalizedSimpleStack(ItemStack stack)
    {
        this (Item.itemRegistry.getIDForObject(stack.getItem()), stack.getItemDamage());
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

            if (this.damage == OreDictionary.WILDCARD_VALUE || other.damage == OreDictionary.WILDCARD_VALUE)
            {
                return this.id == other.id;
            }

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
            return  "" + Item.itemRegistry.getNameForObject(obj) + " " + damage;
        }

        return "id:" + id + " damage:" + damage;
    }
}
