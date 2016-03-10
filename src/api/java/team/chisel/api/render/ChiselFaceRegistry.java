//package team.chisel.api.render;
//
//import net.minecraft.util.ResourceLocation;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ChiselFaceRegistry {
//
//    private static Map<ResourceLocation, IChiselFace> map = new HashMap<ResourceLocation, IChiselFace>();
//
//
//    public static void putFace(ResourceLocation loc, IChiselFace tex){
//        map.put(loc, tex);
//    }
//
//    public static IChiselFace getFace(ResourceLocation loc){
//        return map.get(loc);
//    }
//
//    public static void removeFace(ResourceLocation loc){
//        map.remove(loc);
//    }
//
//    public static boolean isFace(ResourceLocation loc){
//        return map.containsKey(loc);
//    }
//
//    public static Map<ResourceLocation, IChiselFace> getFaceMap(){
//        return map;
//    }
//}
