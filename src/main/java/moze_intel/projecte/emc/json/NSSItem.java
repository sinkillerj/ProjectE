package moze_intel.projecte.emc.json;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import moze_intel.projecte.PECore;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class NSSItem implements NormalizedSimpleStack {
	public final ResourceLocation itemName;

	public NBTTagCompound tag = null;
	
	public NSSItem(ItemStack stack)
	{
		this(stack.getItem().getRegistryName());
		if (stack.isEmpty())
		{
			throw new IllegalArgumentException("Can't make NSSItem with empty stack");
		}
		if(stack.hasTag()){
			tag = stack.getTag();
			if(tag.isEmpty())
				tag = null;
		}
	}

	public NSSItem(IForgeRegistryEntry e)
	{
		this(e.getRegistryName());
	}

	public NSSItem(ResourceLocation itemName) {
		this(itemName, null);
	}

	public NSSItem(ResourceLocation itemName, String tagJson) {
		this.itemName = itemName;
		if(tagJson != null){
			try {
				tag = JsonToNBT.getTagFromJson(tagJson);
			} catch (CommandSyntaxException e) {
				PECore.LOGGER.error("Invalid NBT tag for " + itemName.toString()+".");
			}
			if(tag.isEmpty())
				tag = null;
		}else{
			tag = null;
		}
	}
	
	public ResourceLocation getName(){
		return itemName;
	}
	
	@Override
	public int hashCode() {
		if(tag != null && !tag.isEmpty()){
			return itemName.hashCode() ^tag.toString().hashCode();
		}
		return itemName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NSSItem) {
			NSSItem other = (NSSItem) obj;
			if(this.itemName.equals(other.itemName)){
				if(this.tag == null || this.tag.isEmpty()){
					return other.tag == null || other.tag.isEmpty();
				}
				if(other.tag == null || other.tag.isEmpty()){
					return false;
				}
				return this.tag.toString().equalsIgnoreCase(other.tag.toString());
			}
		}
		return false;
	}

	@Override
	public String json() {
		return itemName.toString() + (tag == null? "" : ("|"+tag.toString()));
	}

	@Override
	public String toString() {
		return itemName.toString() + (tag == null? "" : ("|"+tag.toString()));
	}

	public ItemStack getItemStack() {
		Item itm = ForgeRegistries.ITEMS.getValue(itemName);
		if(itm == null)
			return null;
		
		ItemStack ans = new ItemStack(itm);
		ans.setTag(tag);
		return ans;
	}
}
