package moze_intel.projecte.impl;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KnowledgeSyncPKT;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class KnowledgeImpl {

    public static void init()
    {
        CapabilityManager.INSTANCE.register(IKnowledgeProvider.class, new Capability.IStorage<IKnowledgeProvider>() {
            @Override
            public NBTBase writeNBT(Capability<IKnowledgeProvider> capability, IKnowledgeProvider instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IKnowledgeProvider> capability, IKnowledgeProvider instance, EnumFacing side, NBTBase nbt) {

            }
        }, DefaultImpl.class);
    }

    private static class DefaultImpl implements IKnowledgeProvider
    {

        private final List<ItemStack> knowledge = new ArrayList<>();
        private IItemHandlerModifiable inputLocks = new ItemStackHandler(9);
        private double emc = 0;
        private boolean fullKnowledge = false;

        @Override
        public boolean hasFullKnowledge()
        {
            return fullKnowledge;
        }

        @Override
        public void setFullKnowledge(boolean fullKnowledge)
        {
            this.fullKnowledge = fullKnowledge;
        }

        @Override
        public void clearKnowledge()
        {
            knowledge.clear();
            fullKnowledge = false;
        }

        @Override
        public boolean hasKnowledge(@Nullable ItemStack stack) {
            if (stack == null)
            {
                return false;
            }

            if (fullKnowledge)
            {
                return true;
            }

            for (ItemStack s : knowledge)
            {
                if (ItemHelper.basicAreStacksEqual(s, stack))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean addKnowledge(@Nonnull ItemStack stack) {
            boolean ret = false;

            if (stack.getItem() == ObjHandler.tome)
            {
                fullKnowledge = true;
                ret = true;
            }

            if (!hasKnowledge(stack))
            {
                knowledge.add(stack);
                ret = true;
            }

            return ret;
        }

        @Override
        public boolean removeKnowledge(@Nonnull ItemStack stack) {
            boolean ret = false;

            if (stack.getItem() == ObjHandler.tome)
            {
                fullKnowledge = false;
                ret = true;
            }

            Iterator<ItemStack> iter = knowledge.iterator();

            while (iter.hasNext())
            {
                if (ItemStack.areItemStacksEqual(stack, iter.next()))
                {
                    iter.remove();
                    ret = true;
                }
            }

            return ret;
        }

        @Override
        public @Nonnull List<ItemStack> getKnowledge() {
            return new ArrayList<>(knowledge);
        }

        @Override
        public @Nonnull IItemHandlerModifiable getInputAndLocks() {
            return inputLocks;
        }

        @Override
        public double getEmc() {
            return emc;
        }

        @Override
        public void setEmc(double emc) {
            this.emc = emc;
        }

        @Override
        public void sync(@Nonnull EntityPlayerMP player)
        {
            PacketHandler.sendTo(new KnowledgeSyncPKT(serializeNBT()), player);
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound properties = new NBTTagCompound();
            properties.setDouble("transmutationEmc", emc);

            pruneStaleKnowledge();
            NBTTagList knowledgeWrite = new NBTTagList();
            for (ItemStack i : knowledge)
            {
                NBTTagCompound tag = i.writeToNBT(new NBTTagCompound());
                knowledgeWrite.appendTag(tag);
            }

            properties.setTag("knowledge", knowledgeWrite);
            properties.setTag("inputlock", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inputLocks, null));
            properties.setBoolean("fullknowledge", fullKnowledge);
            return properties;
        }

        @Override
        public void deserializeNBT(NBTTagCompound properties) {
            emc = properties.getDouble("transmutationEmc");

            NBTTagList list = properties.getTagList("knowledge", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++)
            {
                ItemStack item = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i));
                if (item != null)
                {
                    knowledge.add(item);
                }
            }

            pruneDuplicateKnowledge();

            for (int i = 0; i < inputLocks.getSlots(); i++)
            {
                inputLocks.setStackInSlot(i, null);
            }

            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inputLocks, null, properties.getTag("inputlock"));
            fullKnowledge = properties.getBoolean("fullknowledge");
        }

        private void pruneDuplicateKnowledge()
        {
            ItemHelper.compactItemListNoStacksize(knowledge);
            for (ItemStack s : knowledge)
            {
                if (s.stackSize > 1)
                {
                    s.stackSize = 1;
                }
            }
        }

        private void pruneStaleKnowledge()
        {
            Iterator<ItemStack> iter = knowledge.iterator();
            while (iter.hasNext())
            {
                if (!EMCHelper.doesItemHaveEmc(iter.next()))
                {
                    iter.remove();
                }
            }
        }

    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound>
    {

        public static final ResourceLocation NAME = new ResourceLocation("projecte", "knowledge");

        private final DefaultImpl knowledge = new DefaultImpl();

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == ProjectEAPI.KNOWLEDGE_CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == ProjectEAPI.KNOWLEDGE_CAPABILITY)
            {
                return ProjectEAPI.KNOWLEDGE_CAPABILITY.cast(knowledge);
            }
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            return knowledge.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            knowledge.deserializeNBT(nbt);
        }

    }

    private KnowledgeImpl() {}

}
