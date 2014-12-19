package moze_intel.projecte.utils;

import moze_intel.projecte.gameObjs.container.*;
import moze_intel.projecte.gameObjs.container.inventory.*;
import moze_intel.projecte.gameObjs.gui.*;
import moze_intel.projecte.gameObjs.tiles.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		
		switch (ID)
		{
			case Constants.ALCH_CHEST_GUI:
				if (tile != null && tile instanceof AlchChestTile)
					return new AlchChestContainer(player.inventory, (AlchChestTile) tile);
				break;
			case Constants.ALCH_BAG_GUI:
				return new AlchBagContainer(player.inventory, new AlchBagInventory(player, player.getHeldItem()));
			case Constants.TRANSMUTE_STONE_GUI:
				if (tile != null && tile instanceof TransmuteTile)
					return new TransmuteContainer(player.inventory, (TransmuteTile) tile);
				break;
			case Constants.CONDENSER_GUI:
				if (tile != null && tile instanceof CondenserTile)
					return new CondenserContainer(player.inventory, (CondenserTile) tile);
				break;
			case Constants.RM_FURNACE_GUI:
				if (tile != null && tile instanceof RMFurnaceTile)
					return new RMFurnaceContainer(player.inventory, (RMFurnaceTile) tile);
				break;
			case Constants.DM_FURNACE_GUI:
				if (tile != null && tile instanceof DMFurnaceTile)
					return new DMFurnaceContainer(player.inventory, (DMFurnaceTile) tile);
				break;
			case Constants.COLLECTOR1_GUI:
				if (tile != null && tile instanceof CollectorMK1Tile)
					return new CollectorMK1Container(player.inventory, (CollectorMK1Tile) tile);
				break;
			case Constants.COLLECTOR2_GUI:
				if (tile != null && tile instanceof CollectorMK2Tile)
					return new CollectorMK2Container(player.inventory, (CollectorMK2Tile) tile);
				break;
			case Constants.COLLECTOR3_GUI:
				if (tile != null && tile instanceof CollectorMK3Tile)
					return new CollectorMK3Container(player.inventory, (CollectorMK3Tile) tile);
				break;
			case Constants.RELAY1_GUI:
				if (tile != null && tile instanceof RelayMK1Tile)
					return new RelayMK1Container(player.inventory, (RelayMK1Tile) tile);
				break;
			case Constants.RELAY2_GUI:
				if (tile != null && tile instanceof RelayMK2Tile)
					return new RelayMK2Container(player.inventory, (RelayMK2Tile) tile);
				break;
			case Constants.RELAY3_GUI:
				if (tile != null && tile instanceof RelayMK3Tile)
					return new RelayMK3Container(player.inventory, (RelayMK3Tile) tile);
				break;
			case Constants.MERCURIAL_GUI:
				return new MercurialEyeContainer(player.inventory, new MercurialEyeInventory(player.getHeldItem()));
			case Constants.PHILOS_STONE_GUI:
				return new PhilosStoneContainer(player.inventory);
			case Constants.TRANSMUTE_TABLET_GUI:
				return new TransmuteTabletContainer(player.inventory, new TransmuteTabletInventory(player.getHeldItem(), player));
			case Constants.ETERNAL_DENSITY_GUI:
				return new EternalDensityContainer(player.inventory, new EternalDensityInventory(player.getHeldItem(), player));
			case Constants.CONDENSER_MK2_GUI:
				return new CondenserMK2Container(player.inventory, (CondenserMK2Tile) tile);
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		
		switch (ID)
		{
			case Constants.ALCH_CHEST_GUI:
				if (tile != null && tile instanceof AlchChestTile)
					return new GUIAlchChest(player.inventory, (AlchChestTile) tile);
				break;
			case Constants.ALCH_BAG_GUI:
				return new GUIAlchChest(player.inventory, new AlchBagInventory(player, player.getHeldItem()));
			case Constants.TRANSMUTE_STONE_GUI:
				if (tile != null && tile instanceof TransmuteTile)
					return new GUITransmute(player.inventory, (TransmuteTile) tile);
				break;
			case Constants.CONDENSER_GUI:
				if (tile != null && tile instanceof CondenserTile)
					return new GUICondenser(player.inventory, (CondenserTile) tile);
				break;
			case Constants.RM_FURNACE_GUI:
				if (tile != null && tile instanceof RMFurnaceTile)
					return new GUIRMFurnace(player.inventory, (RMFurnaceTile) tile);
				break;
			case Constants.DM_FURNACE_GUI:
				if (tile != null && tile instanceof DMFurnaceTile)
					return new GUIDMFurnace(player.inventory, (DMFurnaceTile) tile);
				break;
			case Constants.COLLECTOR1_GUI:
				if (tile != null && tile instanceof CollectorMK1Tile)
					return new GUICollectorMK1(player.inventory, (CollectorMK1Tile) tile);
				break;
			case Constants.COLLECTOR2_GUI:
				if (tile != null && tile instanceof CollectorMK2Tile)
					return new GUICollectorMK2(player.inventory, (CollectorMK2Tile) tile);
				break;
			case Constants.COLLECTOR3_GUI:
				if (tile != null && tile instanceof CollectorMK3Tile)
					return new GUICollectorMK3(player.inventory, (CollectorMK3Tile) tile);
				break;
			case Constants.RELAY1_GUI:
				if (tile != null && tile instanceof RelayMK1Tile)
					return new GUIRelayMK1(player.inventory, (RelayMK1Tile) tile);
				break;
			case Constants.RELAY2_GUI:
				if (tile != null && tile instanceof RelayMK2Tile)
					return new GUIRelayMK2(player.inventory, (RelayMK2Tile) tile);
				break;
			case Constants.RELAY3_GUI:
				if (tile != null && tile instanceof RelayMK3Tile)
					return new GUIRelayMK3(player.inventory, (RelayMK3Tile) tile);
				break;
			case Constants.MERCURIAL_GUI:
				return new GUIMercurialEye(player.inventory, new MercurialEyeInventory(player.getHeldItem()));
			case Constants.PHILOS_STONE_GUI:
				return new GUIPhilosStone(player.inventory);
			case Constants.TRANSMUTE_TABLET_GUI:
				return new GUITransmuteTablet(player.inventory, new TransmuteTabletInventory(player.getHeldItem(), player));
			case Constants.ETERNAL_DENSITY_GUI:
				player.getHeldItem();
				return new GUIEternalDensity(player.inventory, new EternalDensityInventory(player.getHeldItem(), player));
			case Constants.CONDENSER_MK2_GUI:
				return new GUICondenserMK2(player.inventory, (CondenserMK2Tile) tile);
		}
		
		return null;
	}
}
