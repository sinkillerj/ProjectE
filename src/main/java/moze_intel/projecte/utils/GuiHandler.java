package moze_intel.projecte.utils;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.container.inventory.EternalDensityInventory;
import moze_intel.projecte.gameObjs.container.inventory.MercurialEyeInventory;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.gui.GUIAlchChest;
import moze_intel.projecte.gameObjs.gui.GUICollectorMK1;
import moze_intel.projecte.gameObjs.gui.GUICollectorMK2;
import moze_intel.projecte.gameObjs.gui.GUICollectorMK3;
import moze_intel.projecte.gameObjs.gui.GUICondenser;
import moze_intel.projecte.gameObjs.gui.GUICondenserMK2;
import moze_intel.projecte.gameObjs.gui.GUIDMFurnace;
import moze_intel.projecte.gameObjs.gui.GUIEternalDensity;
import moze_intel.projecte.gameObjs.gui.GUIMercurialEye;
import moze_intel.projecte.gameObjs.gui.GUIPhilosStone;
import moze_intel.projecte.gameObjs.gui.GUIRMFurnace;
import moze_intel.projecte.gameObjs.gui.GUIRelayMK1;
import moze_intel.projecte.gameObjs.gui.GUIRelayMK2;
import moze_intel.projecte.gameObjs.gui.GUIRelayMK3;
import moze_intel.projecte.gameObjs.gui.GUITransmutation;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK3Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK2Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK3Tile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.items.IItemHandlerModifiable;

public class GuiHandler
{
	public static GuiScreen openGui(FMLPlayMessages.OpenContainer msg) {
		EntityPlayer player = Minecraft.getInstance().player;
		switch (msg.getId().getPath()) {
			case "alchemical_chest": {
				BlockPos pos = msg.getAdditionalData().readBlockPos();
				TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
				if (te instanceof AlchChestTile) {
					return new GUIAlchChest(player.inventory, (AlchChestTile) te);
				}
				break;
			}
			case "alch_bag": {
				EnumHand hand = msg.getAdditionalData().readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
				EnumDyeColor color = ((AlchemicalBag) player.getHeldItem(hand).getItem()).color;
				IItemHandlerModifiable inventory = (IItemHandlerModifiable) player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY)
						.orElseThrow(NullPointerException::new)
						.getBag(color);
				return new GUIAlchChest(player.inventory, hand, inventory);
			}
			case "condenser": {
				BlockPos pos = msg.getAdditionalData().readBlockPos();
				TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
				if (te instanceof CondenserTile) {
					return new GUICondenser(player.inventory, (CondenserTile) te);
				}
				break;
			}
			case "condenser_mk2": {
				BlockPos pos = msg.getAdditionalData().readBlockPos();
				TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
				if (te instanceof CondenserMK2Tile) {
					return new GUICondenserMK2(player.inventory, (CondenserMK2Tile) te);
				}
				break;
			}
			case "transmutation_table": {
				return new GUITransmutation(player.inventory, new TransmutationInventory(player), null);
			}
			case "transmutation_tablet": {
				EnumHand hand = msg.getAdditionalData().readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
				return new GUITransmutation(player.inventory, new TransmutationInventory(player), hand);
			}
			case "philosophers_stone": {
				return new GUIPhilosStone(player.inventory);
			}
			case "mercurial_eye": {
				EnumHand hand = msg.getAdditionalData().readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
				return new GUIMercurialEye(player.inventory, new MercurialEyeInventory(player.getHeldItem(hand)));
			}
			case "eternal_density": {
				EnumHand hand = msg.getAdditionalData().readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
				return new GUIEternalDensity(player.inventory, new EternalDensityInventory(player.getHeldItem(hand), player));
			}
			case "rm_furnace": {
				BlockPos pos = msg.getAdditionalData().readBlockPos();
				TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
				if (te instanceof RMFurnaceTile) {
					return new GUIRMFurnace(player.inventory, (RMFurnaceTile) te);
				}
				break;
			}
			case "dm_furnace": {
				BlockPos pos = msg.getAdditionalData().readBlockPos();
				TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
				if (te instanceof DMFurnaceTile) {
					return new GUIDMFurnace(player.inventory, (DMFurnaceTile) te);
				}
				break;
			}
			case "collector_mk1": {
				BlockPos pos = msg.getAdditionalData().readBlockPos();
				TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
				if (te instanceof CollectorMK1Tile) {
					return new GUICollectorMK1(player.inventory, (CollectorMK1Tile) te);
				}
				break;
			}
			case "collector_mk2": {
				BlockPos pos = msg.getAdditionalData().readBlockPos();
				TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
				if (te instanceof CollectorMK2Tile) {
					return new GUICollectorMK2(player.inventory, (CollectorMK2Tile) te);
				}
				break;
			}
			case "collector_mk3": {
				BlockPos pos = msg.getAdditionalData().readBlockPos();
				TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
				if (te instanceof CollectorMK3Tile) {
					return new GUICollectorMK3(player.inventory, (CollectorMK3Tile) te);
				}
				break;
			}
			case "relay_mk1": {
				BlockPos pos = msg.getAdditionalData().readBlockPos();
				TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
				if (te instanceof RelayMK1Tile) {
					return new GUIRelayMK1(player.inventory, (RelayMK1Tile) te);
				}
				break;
			}
			case "relay_mk2": {
				BlockPos pos = msg.getAdditionalData().readBlockPos();
				TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
				if (te instanceof RelayMK2Tile) {
					return new GUIRelayMK2(player.inventory, (RelayMK2Tile) te);
				}
				break;
			}
			case "relay_mk3": {
				BlockPos pos = msg.getAdditionalData().readBlockPos();
				TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
				if (te instanceof RelayMK3Tile) {
					return new GUIRelayMK3(player.inventory, (RelayMK3Tile) te);
				}
				break;
			}
			default: {
				PECore.LOGGER.error("Unknown gui ID {}", msg.getId());
				break;
			}
		}

		return null;
	}
}
