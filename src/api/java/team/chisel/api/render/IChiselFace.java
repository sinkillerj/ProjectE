package team.chisel.api.render;

import java.util.List;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumWorldBlockLayer;

public interface IChiselFace {

    List<IChiselTexture<?>> getTextureList();

    void addTexture(IChiselTexture<?> texture);

    void addChildFace(IChiselFace face);

    TextureAtlasSprite getParticle();

    void setLayer(EnumWorldBlockLayer layer);

    EnumWorldBlockLayer getLayer();
}
