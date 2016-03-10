package team.chisel.api.carving;

import team.chisel.api.render.IBlockRenderType;

public interface IVariationInfo {

	ICarvingVariation getVariation();

	String getDescription();

	IBlockRenderType getType();
}
