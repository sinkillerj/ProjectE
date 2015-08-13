package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.client.registry.RenderingRegistry;
import moze_intel.projecte.PECore;
import net.minecraft.util.ResourceLocation;

import java.text.DecimalFormat;

public final class Constants 
{
	public static final DecimalFormat EMC_FORMATTER = new DecimalFormat("##.##");
	public static final float PLAYER_WALK_SPEED = 0.1F;
	
	public static final int[] MAX_KLEIN_EMC = new int[] {50000, 200000, 800000, 3200000, 12800000, 51200000};
	public static final int[] RELAY_KLEIN_CHARGE_RATE = new int[] {16, 48, 160};
	public static final float[] COLLECTOR_LIGHT_VALS = new float[] {0.4375F, 0.6875F, 1.0F};
	
	public static final float[] EXPLOSIVE_LENS_RADIUS = new float[] {4.0F, 8.0F, 12.0F, 16.0F, 16.0F, 16.0F, 16.0F, 16.0F};
	public static final int[] EXPLOSIVE_LENS_COST = new int[] {384, 768, 1536, 2304, 2304, 2304, 2304, 2304};
	
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
	@Deprecated
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
	public static final int TRANSMUTATION_GUI = 14;
	public static final int ETERNAL_DENSITY_GUI = 15;
	public static final int CONDENSER_MK2_GUI = 16;
	public static final int PEDESTAL_GUI = 17;

	public static final int MAX_CONDENSER_PROGRESS = 102;
	
	public static final int CHEST_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static final int CONDENSER_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static final int CONDENSER_MK2_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static final int PEDESTAL_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static final ResourceLocation PEDESTAL_MODELTEX_LOCATION = new ResourceLocation(PECore.MODID.toLowerCase(), "textures/models/pedestaltexmap.png");

	public static final int MAX_VEIN_SIZE = 250;
	
	public static final int ENCH_EMC_BONUS = 5000;

	public static final ImmutableMap<String, String> SPACE_STRIP_NAME_MAP;

	static {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		builder.put("Alchemical Chest", "alchemical_chest");
		builder.put("Interdiction Torch", "interdiction_torch");
		builder.put("Transmutation Stone", "transmutation_table");
		builder.put("Condenser", "condenser_mk1");
		builder.put("Condenser MK2", "condenser_mk2");
		builder.put("RM Furnace", "rm_furnace");
		builder.put("RM Furnace Lit", "rm_furnace_lit");
		builder.put("DM Furnace", "dm_furnace");
		builder.put("DM Furnace Lit", "dm_furnace_lit");
		builder.put("DM Pedestal", "dm_pedestal");
		builder.put("Matter Block", "matter_block");
		builder.put("Fuel Block", "fuel_block");
		builder.put("Collector MK1", "collector_mk1");
		builder.put("Collector MK2", "collector_mk2");
		builder.put("Collector MK3", "collector_mk3");
		builder.put("Relay MK1", "relay_mk1");
		builder.put("Realy MK2", "relay_mk2");
		builder.put("Relay MK3", "relay_mk3");
		builder.put("Nova Catalyst", "nova_catalyst");
		builder.put("Nova Cataclysm", "nova_cataclysm");
		SPACE_STRIP_NAME_MAP = builder.build();
	}
}
