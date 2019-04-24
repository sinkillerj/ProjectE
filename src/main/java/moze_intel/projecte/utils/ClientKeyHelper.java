package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableBiMap;
import moze_intel.projecte.PECore;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/**
 * Clientside key helper - because PEKeybind cannot touch client classes or it will crash dedicated servers
 */
@SideOnly(Side.CLIENT)
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
        int keyCode = k.getKeyCode();
        if (keyCode > Keyboard.getKeyCount() || keyCode < 0)
        {
            return k.getDisplayName();
        }
        return Keyboard.getKeyName(keyCode);
    }
}
