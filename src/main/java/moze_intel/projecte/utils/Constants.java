package moze_intel.projecte.utils;

import cpw.mods.fml.client.registry.RenderingRegistry;

public final class Constants 
{
	public static final float PLAYER_WALK_SPEED = 0.1F;
	
	public static final int[] MAX_KLEIN_EMC = new int[] {50000, 200000, 800000, 3200000, 12800000, 51200000};
	public static final int[] RELAY_KLEIN_CHARGE_RATE = new int[] {16, 48, 160};
	public static final float[] COLLECTOR_LIGHT_VALS = new float[] {0.4375F, 0.6875F, 1.0F};
	
	public static final float[] EXPLOSIVE_LENS_RADIUS = new float[] {4.0F, 8.0F, 12.0F, 16.0F};
	public static final int[] EXPLOSIVE_LENS_COST = new int[] {384, 768, 1536, 2304};
	
	public static final int TILE_MAX_EMC = 1073741824;
	
	public static final int COLLECTOR_MK1_MAX = 10000;
	public static final int COLLECTOR_MK2_MAX = 30000;
	public static final int COLLECTOR_MK3_MAX = 60000;
	public static final int COLLECTOR_MK1_GEN = 4;
	public static final int COLLECTOR_MK2_GEN = 12;
	public static final int COLLECTOR_MK3_GEN = 40;
	
	public static final int RELAY_MK1_OUTPUT = 64;
	public static final int RELAY_MK2_OUTPUT = 192;
	public static final int RELAY_MK3_OUTPUT = 640;
	
	public static final int RELAY_MK1_MAX = 100000;
	public static final int RELAY_MK2_MAX = 1000000;
	public static final int RELAY_MK3_MAX = 10000000;
	
	public static final int COAL_BURN_TIME = 1600;
	public static final int ALCH_BURN_TIME = COAL_BURN_TIME * 4;
	public static final int MOBIUS_BURN_TIME = ALCH_BURN_TIME * 4;
	public static final int AETERNALIS_BUR_TIME = MOBIUS_BURN_TIME * 4;
	
	public static final int ALCH_CHEST_GUI = 0;
	public static final int ALCH_BAG_GUI = 1;
	public static final int TRANSMUTE_STONE_GUI = 2;
	public static final int CONDENSER_GUI = 3;
	public static final int RM_FURNACE_GUI = 4;
	public static final int DM_FURNACE_GUI = 5;
	public static final int COLLECTOR1_GUI = 6;
	public static final int COLLECTOR2_GUI = 7;
	public static final int COLLECTOR3_GUI = 8;
	public static final int RELAY1_GUI = 9;
	public static final int RELAY2_GUI = 10;
	public static final int RELAY3_GUI = 11;
	public static final int MERCURIAL_GUI = 12;
	public static final int PHILOS_STONE_GUI = 13;
	public static final int TRANSMUTE_TABLET_GUI = 14;
	public static final int ETERNAL_DENSITY_GUI = 15;
	public static final int CONDENSER_MK2_GUI = 16;
	
	public static final int MAX_CONDENSER_PROGRESS = 102;
	
	public static final int CHEST_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static final int CONDENSER_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static final int CONDENSER_MK2_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	public static final int MAX_VEIN_SIZE = 250;
	
	public static final int ENCH_EMC_BONUS = 5000;
}
