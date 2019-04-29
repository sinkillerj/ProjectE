package moze_intel.projecte.nbt_clean;

import static org.junit.Assert.*;
import moze_intel.projecte.api.item.IItemCharge;
import moze_intel.projecte.emc.nbt.ItemStackNBTManager;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;

import org.junit.Before;
import org.junit.Test;

public class ItemNBTCleaningTests {

	//@Before
	public void startup(){
		
	}
	
	//@Test
	public void noCleaningTest(){
		ItemStack toTest = new ItemStack(ObjHandler.dmAxe);
		toTest.addEnchantment(Enchantment.getEnchantmentByID(1), 3);
		toTest.addEnchantment(Enchantment.getEnchantmentByID(2), 2);
		
		ItemStack filtered = toTest.copy();
		filtered = ItemStackNBTManager.clean(filtered);
		assertEquals(ItemHelper.areItemStacksEqual(filtered, toTest), true);
	}
	
	//@Test
	public void cleaningChargeTest(){
		ItemStack toTest = new ItemStack(ObjHandler.philosStone);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.putInt(IItemCharge.KEY, 2);
		toTest.setTag(nbt);
		
		ItemStack filtered = toTest.copy();
		filtered = ItemStackNBTManager.clean(filtered);
		assertEquals(ItemHelper.areItemStacksEqual(filtered, toTest), false);
		assertEquals(filtered.getTag(), null);
	}
	
	//@Test
	public void cleaningEnergyTest(){
		ItemStack toTest = new ItemStack(ObjHandler.philosStone);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.putInt("Energy", 100000);
		toTest.setTag(nbt);
		
		ItemStack filtered = toTest.copy();
		filtered = ItemStackNBTManager.clean(filtered);
		assertEquals(ItemHelper.areItemStacksEqual(filtered, toTest), false);
		assertEquals(filtered.getTag(), null);
	}
	
	//@Test
	public void cleaningButWithRemainingTest(){
		ItemStack toTest = new ItemStack(ObjHandler.dmAxe);
		toTest.addEnchantment(Enchantments.FORTUNE, 3);
		ItemStack toCompare = toTest.copy();
		NBTTagCompound nbt = new NBTTagCompound();
		toTest.getTag().putInt("Charge", 1);
		
		ItemStack filtered = toTest.copy();
		filtered = ItemStackNBTManager.clean(filtered);
		assertEquals(ItemHelper.areItemStacksEqual(filtered, toTest), false);
		assertEquals(ItemHelper.areItemStacksEqual(filtered, toCompare), true);
	}
	
}
