package moze_intel.utils;

import moze_intel.gameObjs.container.AlchBagContainer;
import moze_intel.gameObjs.container.AlchChestContainer;
import moze_intel.gameObjs.container.CollectorMK1Container;
import moze_intel.gameObjs.container.CollectorMK2Container;
import moze_intel.gameObjs.container.CollectorMK3Container;
import moze_intel.gameObjs.container.CondenserContainer;
import moze_intel.gameObjs.container.DMFurnaceContainer;
import moze_intel.gameObjs.container.MercurialEyeContainer;
import moze_intel.gameObjs.container.PhilosStoneContainer;
import moze_intel.gameObjs.container.RMFurnaceContainer;
import moze_intel.gameObjs.container.RelayMK1Container;
import moze_intel.gameObjs.container.RelayMK2Container;
import moze_intel.gameObjs.container.RelayMK3Container;
import moze_intel.gameObjs.container.TransmuteContainer;
import moze_intel.gameObjs.container.inventory.AlchBagInventory;
import moze_intel.gameObjs.container.inventory.MercurialEyeInventory;
import moze_intel.gameObjs.gui.GUIAlchChest;
import moze_intel.gameObjs.gui.GUICollectorMK1;
import moze_intel.gameObjs.gui.GUICollectorMK2;
import moze_intel.gameObjs.gui.GUICollectorMK3;
import moze_intel.gameObjs.gui.GUICondenser;
import moze_intel.gameObjs.gui.GUIDMFurnace;
import moze_intel.gameObjs.gui.GUIMercurialEye;
import moze_intel.gameObjs.gui.GUIRMFurnace;
import moze_intel.gameObjs.gui.GUIRelayMK1;
import moze_intel.gameObjs.gui.GUIRelayMK2;
import moze_intel.gameObjs.gui.GUIRelayMK3;
import moze_intel.gameObjs.gui.GUITransmute;
import moze_intel.gameObjs.tiles.AlchChestTile;
import moze_intel.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.gameObjs.tiles.CollectorMK2Tile;
import moze_intel.gameObjs.tiles.CollectorMK3Tile;
import moze_intel.gameObjs.tiles.CondenserTile;
import moze_intel.gameObjs.tiles.DMFurnaceTile;
import moze_intel.gameObjs.tiles.RMFurnaceTile;
import moze_intel.gameObjs.tiles.RelayMK1Tile;
import moze_intel.gameObjs.tiles.RelayMK2Tile;
import moze_intel.gameObjs.tiles.RelayMK3Tile;
import moze_intel.gameObjs.tiles.TransmuteTile;
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
		}
		
		return null;
	}
}
