package moze_intel.projecte.fixes;

import com.google.common.collect.ImmutableMap;
import moze_intel.projecte.PECore;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

import javax.annotation.Nonnull;
import java.util.Map;

public class TENameFix implements IFixableData
{
    private static final Map<String, String> NAME_MAP;
    
    static
    {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put("minecraft:alchchesttile", PECore.MODID + ":alchemical_chest");
        builder.put("minecraft:interdictiontile", PECore.MODID + ":interdiction_torch");
        builder.put("minecraft:condensertile", PECore.MODID + ":condenser");
        builder.put("minecraft:condensermk2tile", PECore.MODID + ":condenser_mk2");
        builder.put("minecraft:rmfurnacetile", PECore.MODID + ":rm_furnace");
        builder.put("minecraft:dmfurnacetile", PECore.MODID + ":dm_furnace");
        builder.put("minecraft:collectormk1tile", PECore.MODID + ":collector_mk1");
        builder.put("minecraft:collectormk2tile", PECore.MODID + ":collector_mk2");
        builder.put("minecraft:collectormk3tile", PECore.MODID + ":collector_mk3");
        builder.put("minecraft:relaymk1tile", PECore.MODID + ":relay_mk1");
        builder.put("minecraft:relaymk2tile", PECore.MODID + ":relay_mk2");
        builder.put("minecraft:relaymk3tile", PECore.MODID + ":relay_mk3");
        builder.put("minecraft:dmpedestaltile", PECore.MODID + ":dm_pedestal");
        NAME_MAP = builder.build();
    }
    
    @Override
    public int getFixVersion() {
        return 1;
    }

    @Nonnull
    @Override
    public NBTTagCompound fixTagCompound(@Nonnull NBTTagCompound compound)
    {
        String oldId = compound.getString("id");
        if (NAME_MAP.containsKey(oldId))
        {
            PECore.debugLog("Fixed TE from {} to {}", oldId, NAME_MAP.get(oldId));
            compound.setString("id", NAME_MAP.get(oldId));
        }
        return compound;
    }
}
