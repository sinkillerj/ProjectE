package moze_intel.projecte.api.proxy;

import java.util.Collection;

import org.apache.commons.math3.fraction.BigFraction;

import moze_intel.projecte.emc.arithmetics.IValueArithmetic;
import net.minecraft.item.ItemStack;

/**Interface that allows for calculating custom EMC values for non-defined entries at request time.
 * 
 * This interface allows for both creating new EMC values for existing items based on their NBT tags,
 * as well as provide modifiers to the value if certain NBT Tags are present (such as enchantments)
 * */

public interface IItemNBTEmcCalculator{
	
	public static enum Operation{
		OP_ADD{
			public BigFraction operate(BigFraction originalEMC, BigFraction modifier){
				return originalEMC.add(modifier);
			}
		},
		OP_SUBTRACT{
			public BigFraction operate(BigFraction modifier, BigFraction originalEMC){
				return originalEMC.subtract(modifier);
			}
		},
		OP_MUlTIPLY{
			public BigFraction operate(BigFraction modifier, BigFraction originalEMC){
				return originalEMC.multiply(modifier);
			}
		},
		OP_DIVIDE{
			public BigFraction operate(BigFraction modifier, BigFraction originalEMC){
				return originalEMC.divide(modifier);
			}
		},
		OP_SET{
			public BigFraction operate(BigFraction modifier, BigFraction originalEMC){
				return modifier;
			}
		},
		OP_NONE;
		
		public BigFraction operate(long originalEMC, BigFraction modifier){
			return operate(new BigFraction(originalEMC), modifier);
		}
		public BigFraction operate(BigFraction originalEMC, BigFraction modifier){
			return BigFraction.ZERO;
		}
	}
	
	
	/**Method used to determine if an ItemStack will be passed on to the plugin
	 * String should be a either:
	 * - A ResourceLocator for an item, optionally followed by "|" and a meta value or "*" for wildcard;
	 * - A mod name followed by ":*" to filter any item from that mod ("minecraft:*" for only vanilla items);
	 * - "*" to filter any item from any mod
	 * @return A colection of the above-formatted strings indicating where the plugin operates.*/
	public Collection<String> allowedItems();
	
	/**If the itemStack provided is one of the items this plugin operates on.
	 * @param input The itemstack to be calculated
	 * @return true if the item EMC can be modified by this plugin. */
	public boolean canProcessItem(ItemStack input);
	
	/**If this plugin provides an override to the Item's EMC value, or modifies it with one of 
	 * 4 operations.
	 * @param input The item to be analyzed
	 * @return the operation to be used, as defined in Operations.*/
	public Operation getOperation(ItemStack input); 
	
	/**Calculates either the new EMC for this item (if setsEMC == true) or calculates a modifier to the
	 * item's value based on its NBT tags.
	 * @param input The itemstack to be calculated
	 * @return long value of EMC */
	public BigFraction getEMC(final ItemStack input);
	
}
