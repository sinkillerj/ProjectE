package moze_intel.utils;

import moze_intel.EMC.ItemStackMap;
import moze_intel.gameObjs.ObjHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class Constants 
{
	public static ItemStackMap<ItemStack, Integer> fuelMap;
	
	public static final float PLAYER_WALK_SPEED = 0.1F;
	
	public static final int[] kleinStarsMaxEMC = new int[] {50000, 200000, 800000, 3200000, 12800000, 51200000};
	public static final int[] relayKleinChargeRate = new int[] {16, 48, 160};
	public static final float[] collectorLightVal = new float[] {0.4375F, 0.6875F, 1.0F};
	
	public static final float[] explosiveLensRadius = new float[] {4.0F, 8.0F, 12.0F, 16.0F};
	public static final int[] explosiveLensCots = new int[] {384, 768, 1536, 2304};
	
	public static final int tileEmcConsumerMaxEmc = 1073741824;
	
	public static final int collectorMK1Max = 10000;
	public static final int collectorMK2Max = 30000;
	public static final int collectorMK3Max = 60000;
	public static final int collectorMk1Gen = 4;
	public static final int collectorMK2Gen = 12;
	public static final int collectorMK3Gen = 40;
	
	public static final int RELAY_MK1_OUTPUT = 64;
	public static final int RELAY_MK2_OUTPUT = 192;
	public static final int RELAY_MK3_OUTPUT = 640;
	
	public static final int RELAY_MK1_MAX = 100000;
	public static final int RELAY_MK2_MAX = 1000000;
	public static final int RELAY_MK3_MAX = 10000000;
	
	public static final int alchemicalCoalEmc = 512;
	public static final int mobiusFuelEmc = 2048;
	public static final int aeternalisFuelEmc = 8192;
	
	public static final int coalBurnTime = 1600;
	public static final int alchemicalCoalBurnTime = coalBurnTime * 4;
	public static final int mobiusFuelBurnTime = alchemicalCoalBurnTime * 4;
	public static final int aeternalisFuelBurnTime = mobiusFuelBurnTime * 4;
	
	public static final int alchChestGUI = 0;
	public static final int alchBagGUI = 1;
	public static final int transmStoneGUI = 2;
	public static final int condenserGUI = 3;
	public static final int rmFurnaceGUI = 4;
	public static final int dmFurnaceGUI = 5;
	public static final int collectorMK1GUI = 6;
	public static final int collectorMK2GUI = 7;
	public static final int collectorMK3GUI = 8;
	public static final int relayMK1GUI = 9;
	public static final int relayMK2GUI = 10;
	public static final int relayMK3GUI = 11;
	public static final int mercurialEyeGUI = 12;
	
	public static final int maxCondenserProgress = 102;
	
	public static final int chestRenderID = RenderingRegistry.getNextAvailableRenderId();
	public static final int condenserRenderID = RenderingRegistry.getNextAvailableRenderId();
	
	public static final int MAX_VEIN_SIZE = 250;
	
	public static void init()
	{
		fuelMap = new ItemStackMap();
		fuelMap.put(new ItemStack(Items.coal, 1, 1), 32);
		fuelMap.put(new ItemStack(Items.redstone), 64);
		fuelMap.put(new ItemStack(Items.coal), 128);
		fuelMap.put(new ItemStack(Items.gunpowder), 192);
		fuelMap.put(new ItemStack(Items.glowstone_dust), 384);
		fuelMap.put(new ItemStack(ObjHandler.fuels, 1, 0), 512);
		fuelMap.put(new ItemStack(Items.blaze_powder), 768);
		//fuelMap.put(new ItemStack(Blocks.glowstone), 1536);
		fuelMap.put(new ItemStack(ObjHandler.fuels, 1, 1), 2048);
		fuelMap.put(new ItemStack(ObjHandler.fuels, 1, 2), 8192);
	}
}
