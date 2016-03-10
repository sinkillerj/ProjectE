package team.chisel.api.render;


import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import team.chisel.Chisel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * List of IBlockRenderContext's
 */
public class RenderContextList {

    private Map<IBlockRenderType, IBlockRenderContext> contextMap;

    public RenderContextList(List<IBlockRenderType> types, IBlockAccess world, BlockPos pos){
        contextMap = new HashMap<IBlockRenderType, IBlockRenderContext>();
        for (IBlockRenderType type : types){
            IBlockRenderContext ctx = type.getBlockRenderContext(world, pos);
            if (ctx != null) {
                contextMap.put(type, ctx);
            }
        }
    }

    public IBlockRenderContext getRenderContext(IBlockRenderType type){
        try {
            return this.contextMap.get(type);
        } catch (ClassCastException exception){
            Chisel.debug("Contextmap had a bad type context pair");
            throw exception;
        }
    }

    public boolean contains(IBlockRenderType type){
        return getRenderContext(type) != null;
    }

    public RenderContextList(){

    }
}
