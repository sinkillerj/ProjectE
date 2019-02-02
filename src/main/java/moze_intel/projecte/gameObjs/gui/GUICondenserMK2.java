package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CondenserMK2Container;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GUICondenserMK2 extends GUICondenser
{
	public GUICondenserMK2(InventoryPlayer invPlayer, CondenserMK2Tile tile)
	{
		super(new CondenserMK2Container(invPlayer, tile), new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/condenser_mk2.png"));
	}
}
