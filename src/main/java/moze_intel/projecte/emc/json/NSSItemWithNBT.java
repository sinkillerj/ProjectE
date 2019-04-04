package moze_intel.projecte.emc.json;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;

import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.SimpleStack;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.oredict.OreDictionary;

public class NSSItemWithNBT implements NormalizedSimpleStack {
	
	public static final String[] NO_IGNORES = new String[0];
	public static final String[] JUST_IGNORE_DAMAGE = new String[]{"$Damage"};
	public final String itemName;
	public final int damage;
	public final boolean ignoreDamage;
	public final NBTTagCompound nbt;

	public NSSItemWithNBT(String itemName, int damage) {
		this(itemName, damage, new NBTTagCompound());
	}
	
	public NSSItemWithNBT(String itemName, int damage, NBTTagCompound tag) {
		this(itemName, damage, tag, NO_IGNORES);
	}
	
	public NSSItemWithNBT(String itemName, int damage, NBTTagCompound tag, String[] nbtsToIgnore) {
		this.itemName = itemName;
		this.damage = damage;
		nbt = tag.copy();
		boolean dmgIgn = false;
		for(String s: nbtsToIgnore){
			if(s.equalsIgnoreCase("$Damage")){
				dmgIgn = true;
			}else{
				NBTTagCompound toChange = tag;
				String[] tree = s.split("::");
				for(int i = 0; i < tree.length-1; i++){
					tag.getTag(tree[i]);
				}
				tag.removeTag(tree[tree.length -1]);
			}
		}
		ignoreDamage = dmgIgn;
		if (tag.getKeySet().isEmpty())
			tag = null;
	}
	
	public static NormalizedSimpleStack create(ItemStack stack) {
		if (stack.isEmpty()) return null;
		return create(stack.getItem(), stack.getItemDamage(), stack.getTagCompound(), NO_IGNORES);
	}
	public static NormalizedSimpleStack create(ItemStack stack, String[] NBTToIgnore) {
		if (stack.isEmpty()) return null;
		return create(stack.getItem(), stack.getItemDamage(), stack.getTagCompound(), NBTToIgnore);
	}

	private static NormalizedSimpleStack create(Item item, int meta, NBTTagCompound tag, String[] NBTToIgnore) {

		return create(item.getRegistryName(), meta, tag, NBTToIgnore);
	}

	private static NormalizedSimpleStack create(ResourceLocation uniqueIdentifier, int damage, NBTTagCompound tag, String[] NBTtoIgnore) {
		if (uniqueIdentifier == null) return null;
		return create(uniqueIdentifier.toString(), damage, tag, NBTtoIgnore);
	}

	public static NormalizedSimpleStack create(String itemName, int damage, NBTTagCompound tag, String[] NBTtoIgnore) {
		NormalizedSimpleStack normStack;
		try {
			if(tag == null || tag.isEmpty())
				normStack = new NSSItem(itemName, damage);
			else
				normStack = new NSSItemWithNBT(itemName, damage, tag, NBTtoIgnore);
		} catch (Exception e) {
			PECore.LOGGER.fatal("Could not create NSSItemWithNBT: {}", e.getMessage());
			return null;
		}
		return normStack;
	}

	@Override
	public int hashCode() {
		return itemName.hashCode() ^ damage ^nbt.toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NSSItemWithNBT) {
			NSSItemWithNBT other = (NSSItemWithNBT) obj;
			if(this.itemName.equals(other.itemName) && this.damage == other.damage){
				if(this.nbt == null)
					return other.nbt == null;
				if(other.nbt == null)
					return false;
				return this.nbt.toString().equalsIgnoreCase(other.nbt.toString());
			}
			return false;
		}else if(obj instanceof NSSItem){
			NSSItem other = (NSSItem) obj;
			return this.itemName.equals(other.itemName) && this.damage == other.damage &&
				((this.nbt == null || this.nbt.isEmpty()));
		}

		return false;
	}

	@Override
	public String json() {
		String orgFormat = String.format("%s|%s", itemName, damage == OreDictionary.WILDCARD_VALUE ? "*" : damage);
		if(nbt == null || nbt.isEmpty())
			return orgFormat;
		String nbtFormat = NBTToParseableString(nbt);
		return orgFormat + "|"+nbtFormat;
	}

	public static String NBTToParseableString(NBTTagCompound nbt2) {
		return nbt2.toString();
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s", itemName, damage == OreDictionary.WILDCARD_VALUE ? "*" : damage) + "|" +nbt.toString();
	}

	public boolean equalsEvenPartially(Object obj) {
		if (obj instanceof NSSItemWithNBT) {
			NSSItemWithNBT other = (NSSItemWithNBT) obj;
			if(this.itemName.equals(other.itemName) && this.damage == other.damage){
				if(this.nbt == null)
					return other.nbt == null;
				if(other.nbt == null)
					return false;
				for(String key: this.nbt.getKeySet()){
					if(!other.nbt.hasKey(key))
						return false;
					if(other.nbt.getTag(key).getClass() != this.nbt.getTag(key).getClass())
						return false;
					if(this.nbt.getTag(key) instanceof NBTTagCompound){
						if(!NSSItemWithNBT.isNBTContained(this.nbt.getCompoundTag(key), other.nbt.getCompoundTag(key))){
							return false;
						}
					}else{
						if(!this.nbt.getTag(key).equals(other.nbt.getTag(key)))
							return false;
					}
					
				}
				return true;
			}
			return false;
		}

		return false;
	}

	public SimpleStack toSimpleStack() {
		int dam = damage;
		if(ignoreDamage)
			dam = 0;
		if(nbt == null){
			return new SimpleStack(new ResourceLocation(itemName),dam);
		}else{
			return new SimpleStack(new ResourceLocation(itemName),dam, nbt);
		}
	}
	
	public static boolean isNBTContained(NBTTagCompound containerTag,
			NBTTagCompound containeeTag) {
		if(containerTag == null && containeeTag == null)
			return true;
		if(containeeTag == null)
			return false;
		for(String key: containerTag.getKeySet()){
			if(!containeeTag.hasKey(key))
				return false;
			if(containerTag.getTag(key).getClass() != containeeTag.getTag(key).getClass())
				return false;
			if(containerTag.getTag(key) instanceof NBTTagCompound){
				if(!isNBTContained(containerTag.getCompoundTag(key), containeeTag.getCompoundTag(key))){
					return false;
				}
			}else{
				if(!containerTag.getTag(key).equals(containeeTag.getTag(key)))
					return false;
			}
		}
		return true;
	}

	/**Simple metric for measuring the similarity between two NBTTags.
	 * Similarity is determined as the sum of tag keys of the container tag present,
	 * in the containee tag, +1 if they are of same classes and +1 if they are equal,
	 * with a bonus of +similarity of every NBTTagCompund that are contained in both.*/
	public static int NBTSimilarity(NBTTagCompound containerTag,
			NBTTagCompound containeeTag) {
		int simi = 0;
		if(containerTag.isEmpty())
			return 1;
		for(String key: containerTag.getKeySet()){
			if(containeeTag.hasKey(key)){
				simi++;
			}else{
				return 0;
			}
			if(containerTag.getTag(key).getClass() == containeeTag.getTag(key).getClass()){
				simi++;
			}else{
				return 0;
			}
			if(containerTag.getTag(key) instanceof NBTTagCompound){
				int newSimi = NBTSimilarity(containerTag.getCompoundTag(key), containeeTag.getCompoundTag(key));
				if (newSimi <= 0)
					return 0;
				simi += newSimi;
			}else if(containerTag.getTag(key).equals(containeeTag.getTag(key))){
					simi++;
			}else{
				return 0;
			}
		}
		return simi;
	}
	
}
