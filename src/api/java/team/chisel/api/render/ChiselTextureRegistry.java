//package team.chisel.api.render;
//
//import net.minecraft.util.ResourceLocation;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class ChiselTextureRegistry {
//
//    private static Map<ResourceLocation, IChiselTexture> map = new HashMap<ResourceLocation, IChiselTexture>();
//
//
//    public static void putTexture(ResourceLocation loc, IChiselTexture tex){
//        map.put(loc, tex);
//    }
//
//    public static IChiselTexture getTex(ResourceLocation loc){
//        return map.get(loc);
//    }
//
//    public static void removeTex(ResourceLocation loc){
//        map.remove(loc);
//    }
//
//    public static boolean isTex(ResourceLocation loc){
//        return map.containsKey(loc);
//    }
//}
