package moze_intel.projecte.emc.nbt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.math3.fraction.BigFraction;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.proxy.IItemNBTEmcCalculator;
import moze_intel.projecte.api.proxy.IItemNBTEmcCalculator.Operation;
import moze_intel.projecte.api.proxy.IItemNBTFilter;
import moze_intel.projecte.emc.nbt.calculators.DurabilityCalculator;
import moze_intel.projecte.emc.nbt.calculators.EnchantmentCalculator;
import moze_intel.projecte.emc.nbt.calculators.StoredEMCCalculator;
import moze_intel.projecte.emc.nbt.cleaners.ItemNBTEnchantmentTagCleaner;
import moze_intel.projecte.emc.nbt.cleaners.ItemNBTNameTagCleaner;
import moze_intel.projecte.emc.nbt.cleaners.ItemNBTProjectEActiveModeCleaner;
import moze_intel.projecte.emc.nbt.cleaners.ItemNBTStoredEMCCleaner;

public class ItemStackNBTManager {

	public static final Map<String, List<IItemNBTFilter>> cleanerMap = new HashMap<>();
	public static final Map<String, List<IItemNBTEmcCalculator>> calcMap = new HashMap<>();
	
	public static final IItemNBTFilter enchantmentRemover = new ItemNBTEnchantmentTagCleaner();
	
	static{
		registerCleaner(new ItemNBTNameTagCleaner());
		registerCleaner(new ItemNBTStoredEMCCleaner());
		registerCleaner(new ItemNBTProjectEActiveModeCleaner());
		registerCalculator(new EnchantmentCalculator());
		registerCalculator(new DurabilityCalculator());
		registerCalculator(new StoredEMCCalculator());
	}
	
	public static ItemStack clean(ItemStack input){
		ItemStack filtered = input.copy();
		if(filtered.getTagCompound() == null)
			return filtered;
		if(filtered.getTagCompound().isEmpty()){
			filtered.setTagCompound(null);
			return filtered;
		}
			
		if(cleanerMap.containsKey("*")){
			for(IItemNBTFilter filter: cleanerMap.get("*")){
				if(filter.canFilterStack(filtered)){
					filtered = filter.getFilteredItemStack(filtered);
				}
			}
		}
		if(cleanerMap.containsKey(input.getItem().getRegistryName().getPath()+ ":*")){
			for(IItemNBTFilter filter: cleanerMap.get(input.getItem().getRegistryName().getPath()+ ":*")){
				if(filter.canFilterStack(filtered)){
					filtered = filter.getFilteredItemStack(filtered);
				}
			}
		}
		if(cleanerMap.containsKey(input.getItem().getRegistryName().toString())){
			for(IItemNBTFilter filter: cleanerMap.get(input.getItem().getRegistryName().toString())){
				if(filter.canFilterStack(filtered)){
					filtered = filter.getFilteredItemStack(filtered);
				}
			}
		}
		
		if(cleanerMap.containsKey(input.getItem().getRegistryName().toString() + "|"+input.getMetadata())){
			for(IItemNBTFilter filter: cleanerMap.get(input.getItem().getRegistryName().toString()+ "|"+input.getMetadata())){
				if(filter.canFilterStack(filtered)){
					filtered = filter.getFilteredItemStack(filtered);
				}
			}
		}
		for(int k: OreDictionary.getOreIDs(input)){
			if(cleanerMap.containsKey(OreDictionary.getOreName(k))){
				for(IItemNBTFilter filter: cleanerMap.get(OreDictionary.getOreName(k))){
					if(filter.canFilterStack(filtered)){
						filtered = filter.getFilteredItemStack(filtered);
					}
				}
			}
		}
		if(filtered.getTagCompound() == null || filtered.getTagCompound().isEmpty())
			filtered.setTagCompound(null);
		return filtered;
	}
	public static long getEMCValue(ItemStack input){
		return getEMCValue(input,0);
	}
	public static long getEMCValue(ItemStack input, long original){
		Set<Long> setValues = new TreeSet<Long>();
		BigFraction added = new BigFraction(0);
		BigFraction multiply = new BigFraction(1);
		setValues.add(original);
		
		if(calcMap.containsKey("*")){
			for(IItemNBTEmcCalculator calculator: calcMap.get("*")){
				if(calculator.canProcessItem(input)){
					BigFraction total = calculator.getEMC(input);
					Operation op = calculator.getOperation(input);
					switch(op){
					case OP_ADD:
					case OP_SUBTRACT:
						added= op.operate(added, total);
						break;
					case OP_MUlTIPLY:
					case OP_DIVIDE:
						multiply = op.operate(multiply, total);
						break;
					case OP_SET:
						setValues.add(total.longValue());
					default:
					}
				}
			}
		}
		if(calcMap.containsKey(input.getItem().getRegistryName().getPath() + ":*")){
			for(IItemNBTEmcCalculator calculator: calcMap.get(input.getItem().getRegistryName().getPath() + ":*")){
				if(calculator.canProcessItem(input)){
					BigFraction total = calculator.getEMC(input);
					Operation op = calculator.getOperation(input);
					switch(op){
					case OP_ADD:
					case OP_SUBTRACT:
						added= op.operate(added, total);
						break;
					case OP_MUlTIPLY:
					case OP_DIVIDE:
						multiply = op.operate(multiply, total);
						break;
					case OP_SET:
						setValues.add(total.longValue());
					default:
					}
				}
			}
		}
		if(calcMap.containsKey(input.getItem().getRegistryName().toString())){
			for(IItemNBTEmcCalculator calculator: calcMap.get(input.getItem().getRegistryName().toString())){
				if(calculator.canProcessItem(input)){
					BigFraction total = calculator.getEMC(input);
					Operation op = calculator.getOperation(input);
					switch(op){
					case OP_ADD:
					case OP_SUBTRACT:
						added= op.operate(added, total);
						break;
					case OP_MUlTIPLY:
					case OP_DIVIDE:
						multiply = op.operate(multiply, total);
						break;
					case OP_SET:
						setValues.add(total.longValue());
					default:
					}
				}
			}
		}
		if(calcMap.containsKey(input.getItem().getRegistryName().toString() + "|" + input.getMetadata())){
			for(IItemNBTEmcCalculator calculator: calcMap.get(input.getItem().getRegistryName().toString()+ "|" + input.getMetadata())){
				if(calculator.canProcessItem(input)){
					BigFraction total = calculator.getEMC(input);
					Operation op = calculator.getOperation(input);
					switch(op){
					case OP_ADD:
					case OP_SUBTRACT:
						added= op.operate(added, total);
						break;
					case OP_MUlTIPLY:
					case OP_DIVIDE:
						multiply = op.operate(multiply, total);
						break;
					case OP_SET:
						setValues.add(total.longValue());
					default:
					}
				}
			}
		}
		for(int k: OreDictionary.getOreIDs(input)){
			if(calcMap.containsKey(OreDictionary.getOreName(k))){
				for(IItemNBTEmcCalculator calculator: calcMap.get(OreDictionary.getOreName(k))){
					if(calculator.canProcessItem(input)){
						BigFraction total = calculator.getEMC(input);
						Operation op = calculator.getOperation(input);
						switch(op){
						case OP_ADD:
						case OP_SUBTRACT:
							added= op.operate(added, total);
							break;
						case OP_MUlTIPLY:
						case OP_DIVIDE:
							multiply = op.operate(multiply, total);
							break;
						case OP_SET:
							setValues.add(total.longValue());
						default:
						}
					}
				}
			}
		}
		BigFraction realEMC = BigFraction.ZERO;
		if(original > 0){
			for(long l : setValues){
				if(l < 0)
					return 0;
				if(l > 0)
					break;
			}
			realEMC = new BigFraction(original);
		}else{
			for(long l : setValues){
				if(l < 0)
					return 0;
				if(l > 0){
					realEMC = new BigFraction(l);
					break;
				}
			}
		}
		if(realEMC.compareTo(BigFraction.ZERO) > 0){
			realEMC = realEMC.add(added);
			realEMC = realEMC.multiply(multiply);
		}
		return realEMC.longValue();
	}
	
	public static void registerCalculator(IItemNBTEmcCalculator calc){
		Collection<String> applied = calc.allowedItems();
		List<String> ignoreMods = new ArrayList<String>();
		List<String> ignoreItems = new ArrayList<String>();
		Map<String, IItemNBTEmcCalculator> calcMap2 = new HashMap<>();
		for(String key: applied){
			if(key.equals("*")){
				addToCalcMap(key, calc);
				return;
			}
			String[] split = key.split(":");
			if(split.length == 1){
				calcMap2.put(split[0],calc);
			}else{
				if(!ignoreMods.contains(split[0])){
					String[] meta = split[1].split("|");
					if(meta.length == 1){
						if(split[1].equals("*")){
							ignoreMods.add(split[0]);
							ArrayList<String> toRemove = new ArrayList<String>();
							for(String s : calcMap2.keySet()){
								if(s.startsWith(split[0]+":")){
									toRemove.add(s);
								}
							}
							for(String s: toRemove){
								calcMap2.remove(s);
							}
						}
						calcMap2.put(key, calc);
						ignoreItems.add(key);
					}else{
						if(!ignoreItems.contains(split[0]+ ":"+ meta[0])){
							if(meta[1].equals("*")){
								ignoreItems.add(split[0]+ ":"+ meta[0]);
								ArrayList<String> toRemove = new ArrayList<String>();
								for(String s : calcMap2.keySet()){
									if(s.startsWith(split[0]+":"+ meta[0]+ "|")){
										toRemove.add(s);
									}
								}
								for(String s: toRemove){
									calcMap2.remove(s);
								}
								calcMap2.put(split[0]+ ":"+ meta[0],calc);
							}else{
								calcMap2.put(key, calc);
							}
						}
					}
				}
			}
		}
		for(String s: calcMap2.keySet()){
			addToCalcMap(s, calc);
		}
	}
	
	public static void registerCleaner(IItemNBTFilter cleaner){
		Collection<String> applied = cleaner.allowedItems();
		List<String> ignoreMods = new ArrayList<String>();
		List<String> ignoreItems = new ArrayList<String>();
		Map<String, IItemNBTFilter> cleanerMap2 = new HashMap<>();
		for(String key: applied){
			if(key.equals("*")){
				addToCleanerMap(key, cleaner);
				return;
			}
			String[] split = key.split(":");
			if(split.length == 1){
				cleanerMap2.put(split[0],cleaner);
			}else{
				if(!ignoreMods.contains(split[0])){
					String[] meta = split[1].split("|");
					if(meta.length == 1){
						if(split[1].equals("*")){
							ignoreMods.add(split[0]);
							ArrayList<String> toRemove = new ArrayList<String>();
							for(String s : cleanerMap2.keySet()){
								if(s.startsWith(split[0]+":")){
									toRemove.add(s);
								}
							}
							for(String s: toRemove){
								cleanerMap2.remove(s);
							}
						}
						cleanerMap2.put(key, cleaner);
						ignoreItems.add(key);
					}else{
						if(!ignoreItems.contains(split[0]+ ":"+ meta[0])){
							if(meta[1].equals("*")){
								ignoreItems.add(split[0]+ ":"+ meta[0]);
								ArrayList<String> toRemove = new ArrayList<String>();
								for(String s : cleanerMap2.keySet()){
									if(s.startsWith(split[0]+":"+ meta[0]+ "|")){
										toRemove.add(s);
									}
								}
								for(String s: toRemove){
									cleanerMap2.remove(s);
								}
								cleanerMap2.put(split[0]+ ":"+ meta[0], cleaner);
							}else{
								cleanerMap2.put(key, cleaner);
							}
						}
					}
				}
			}
		}
		for(String s: cleanerMap2.keySet()){
			addToCleanerMap(s, cleaner);
		}
	}
	

	private static void addToCalcMap(String key, IItemNBTEmcCalculator calc) {
		if(!calcMap.containsKey(key)){
			calcMap.put(key, new ArrayList<>());
		}
		calcMap.get(key).add(calc);
	}

	private static void addToCleanerMap(String s, IItemNBTFilter cleaner) {
		if(!cleanerMap.containsKey(s)){
			cleanerMap.put(s, new ArrayList<>());
		}
		cleanerMap.get(s).add(cleaner);
	}
	
	private static void addAllToCleanerMap(Map<String, IItemNBTFilter> map){
		for(String s: map.keySet()){
			addToCleanerMap(s, map.get(s));
		}
	}
	
}
