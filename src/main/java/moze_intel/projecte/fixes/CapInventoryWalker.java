package moze_intel.projecte.fixes;

import com.google.common.collect.ImmutableSet;
import moze_intel.projecte.PECore;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.stream.Collectors;

/*
 Finds ItemStackHandler's under located at `handlerNames` and fixes the stacks inside them
 Very similar to ItemStackDataLists but with one additional layer of nesting */
public class CapInventoryWalker implements IDataWalker
{
    private final Set<ResourceLocation> ids;
    private final Set<String> handlerNames;

    public CapInventoryWalker(Class<? extends TileEntity> te, String... outerNames)
    {
        this(ImmutableSet.of(te), outerNames);
    }

    public CapInventoryWalker(Set<Class<? extends TileEntity>> teTypes, String... outerNames)
    {
        this.ids = teTypes.stream().map(TileEntity::getKey).collect(Collectors.toSet());
        this.handlerNames = ImmutableSet.copyOf(outerNames);
    }

    @Nonnull
    @Override
    public NBTTagCompound process(@Nonnull IDataFixer fixer, @Nonnull NBTTagCompound te, int version)
    {
        if (ids.contains(new ResourceLocation(te.getString("id"))))
        {
            for (String outerKey : handlerNames)
            {
                if (te.hasKey(outerKey, Constants.NBT.TAG_COMPOUND))
                {
                    NBTTagCompound itemstackHandler = te.getCompoundTag(outerKey);
                    DataFixesManager.processInventory(fixer, itemstackHandler, version, "Items");
                    PECore.LOGGER.debug("Walked inventory {} of TE {}, containing {} items", outerKey, te.getString("id"), itemstackHandler.getInteger("Size"));
                }
            }
        }

        return te;
    }
}
