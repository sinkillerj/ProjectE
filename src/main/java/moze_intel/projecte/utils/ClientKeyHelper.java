package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableBiMap;
import moze_intel.projecte.PECore;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

/**
 * Clientside key helper - because PEKeybind cannot touch client classes or it will crash dedicated servers
 */
@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class ClientKeyHelper
{
    public static ImmutableBiMap<KeyBinding, PEKeybind> mcToPe;
    private static ImmutableBiMap<PEKeybind, KeyBinding> peToMc;



    public static void registerMCBindings()
    {
        ImmutableBiMap.Builder<KeyBinding, PEKeybind> builder = ImmutableBiMap.builder();
        for (PEKeybind k : PEKeybind.values())
        {
            KeyBinding mcK = new KeyBinding(k.keyName, k.defaultKeyCode, PECore.MODID);
            builder.put(mcK, k);
            ClientRegistry.registerKeyBinding(mcK);
        }
        mcToPe = builder.build();
        peToMc = mcToPe.inverse();
    }

    /**
     * Get the key name this PEKeybind is bound to.
     */
    public static String getKeyName(PEKeybind k)
    {
        return getKeyName(peToMc.get(k));
    }

    public static String getKeyName(KeyBinding k)
    {
        // todo 1.13 recheck
        return k.getTranslationKey();
    }
}
