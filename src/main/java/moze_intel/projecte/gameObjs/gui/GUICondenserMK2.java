package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.CondenserMK2Container;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GUICondenserMK2 extends GUICondenser<CondenserMK2Container>
{
	public GUICondenserMK2(CondenserMK2Container container, PlayerInventory invPlayer, ITextComponent title)
	{
		super(container, invPlayer, title);
	}

	@Override
	protected ResourceLocation getTexture()
	{
		return new ResourceLocation(PECore.MODID.toLowerCase(), "textures/gui/condenser_mk2.png");
	}
}
