package moze_intel.projecte.emc;

import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NSSItemWithNBT;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Objects;

import crafttweaker.mc1120.commands.NBTUtils;

public class SimpleStack
{
	public final ResourceLocation id;
	public final int damage;
	public final NBTTagCompound tag;

	public SimpleStack(ResourceLocation id, int damage)
	{
		this.id = id;
		this.damage = damage;
		this.tag = null;
	}
	
	public SimpleStack(ResourceLocation id, int damage, NBTTagCompound tag)
	{
		this.id = id;
		this.damage = damage;
		this.tag = tag;
	}
	
	public SimpleStack(ItemStack stack)
	{
		this(stack, NSSItemWithNBT.NO_IGNORES);
	}
	
	public SimpleStack(ItemStack stack, String[] ignores)
	{
		if (stack.isEmpty())
		{
			id = new ResourceLocation("minecraft", "air");
			damage = 0;
			tag = null;
			return;
		}
		NormalizedSimpleStack itm2 = NSSItemWithNBT.create(stack, ignores);
		if(itm2 instanceof NSSItem){
			NSSItem itm = (NSSItem) itm2;
			id = new ResourceLocation(itm.itemName);
			damage = itm.damage;
			tag = null;
		}else {
			NSSItemWithNBT itm = (NSSItemWithNBT) itm2;
			id = new ResourceLocation(itm.itemName);
			damage = itm.damage;
			tag = itm.nbt;
		}
	}

	public SimpleStack withMeta(int meta)
	{
		return new SimpleStack(id, meta);
	}
	
	public SimpleStack withNBT(NBTTagCompound tag)
	{
		return new SimpleStack(id, damage, tag);
	}
	
	public SimpleStack withDamageAndNBT(int meta, NBTTagCompound tag)
	{
		return new SimpleStack(id, meta, tag);
	}

	public boolean isValid()
	{
		return !id.equals(new ResourceLocation("minecraft", "air"));
	}

	public ItemStack toItemStack()
	{
		if (isValid())
		{
			Item item = Item.REGISTRY.getObject(id);

			if (item != null)
			{
				ItemStack ans = new ItemStack(item, 1, damage);
				if(tag != null){
					ans.setTagCompound(tag);
				}
				return ans;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public int hashCode() 
	{
		int hash = 31 * id.hashCode();
		if (this.damage == OreDictionary.WILDCARD_VALUE)
			hash = hash * 57 ^ this.damage;
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof SimpleStack)
		{
			SimpleStack other = (SimpleStack) obj;
			 
			if (this.damage == OreDictionary.WILDCARD_VALUE || other.damage == OreDictionary.WILDCARD_VALUE)
			{
				return Objects.equals(this.id, other.id);
			}

			return Objects.equals(this.id, other.id) && this.damage == other.damage && 
					(tag == null? other.tag == null: NBTUtil.areNBTEquals(tag, other.tag, true));
		}
		
		return false;
	}
	
	@Override
	public String toString() 
	{
		Item obj = Item.REGISTRY.getObject(id);
		
		if (obj != null)
		{
			return id + " " + damage +" " + (tag != null ? tag.toString():"");
		}
		
		return "id:" + id + " damage:" + damage;
	}
}
