package moze_intel.projecte.impl;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.event.PlayerKnowledgeChangeEvent;
import moze_intel.projecte.emc.nbt.ItemStackNBTManager;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KnowledgeSyncPKT;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class KnowledgeImpl {

    public static void init()
    {
        CapabilityManager.INSTANCE.register(IKnowledgeProvider.class, new Capability.IStorage<IKnowledgeProvider>() {
            @Override
            public NBTTagCompound writeNBT(Capability<IKnowledgeProvider> capability, IKnowledgeProvider instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IKnowledgeProvider> capability, IKnowledgeProvider instance, EnumFacing side, INBTBase nbt) {
                if (nbt instanceof NBTTagCompound) {
                    instance.deserializeNBT((NBTTagCompound) nbt);
                }
            }
        }, () -> new DefaultImpl(null));
    }

    private static class DefaultImpl implements IKnowledgeProvider
    {
        @Nullable
        private final EntityPlayer player;
        private final List<ItemStack> knowledge = new ArrayList<>();
        private final IItemHandlerModifiable inputLocks = new ItemStackHandler(9);
        private double emc = 0;
        private boolean fullKnowledge = false;

        private DefaultImpl(EntityPlayer player) {
            this.player = player;
        }

        private void fireChangedEvent()
        {
            if (player != null && !player.world.isRemote)
            {
                MinecraftForge.EVENT_BUS.post(new PlayerKnowledgeChangeEvent(player));
            }
        }

        @Override
        public boolean hasFullKnowledge()
        {
            return fullKnowledge;
        }

        @Override
        public void setFullKnowledge(boolean fullKnowledge)
        {
            boolean changed = this.fullKnowledge != fullKnowledge;
            this.fullKnowledge = fullKnowledge;
            if (changed)
            {
                fireChangedEvent();
            }
        }

        @Override
        public void clearKnowledge()
        {
            knowledge.clear();
            fullKnowledge = false;
            fireChangedEvent();
        }

        @Override
        public boolean hasKnowledge(@Nonnull ItemStack stack) {
            if (stack.isEmpty())
            {
                return false;
            }

            if (fullKnowledge)
            {
                return true;
            }

            ItemStack filtered = stack.copy();
            filtered = ItemStackNBTManager.clean(filtered);
            
            for (ItemStack s : knowledge)
            {
            	if(s.getTag() != null && s.getTag().isEmpty())
            		s.setTag(null);
            	
                if (ItemHelper.areItemStacksEqual(s,filtered))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean addKnowledge(@Nonnull ItemStack stack) {
            if (fullKnowledge)
            {
                return false;
            }

            if (stack.getItem() == ObjHandler.tome)
            {
                if (!hasKnowledge(stack))
                {
                    knowledge.add(stack);
                }
                fullKnowledge = true;
                fireChangedEvent();
                return true;
            }
            ItemStack filtered = stack.copy();
            filtered = ItemStackNBTManager.clean(filtered);
            if (!hasKnowledge(filtered))
            {
                knowledge.add(filtered);
                fireChangedEvent();
                return true;
            }

            return false;
        }

        @Override
        public boolean removeKnowledge(@Nonnull ItemStack stack) {
            boolean removed = false;

            if (stack.getItem() == ObjHandler.tome)
            {
                fullKnowledge = false;
                removed = true;
            }

            if (fullKnowledge)
            {
                return false;
            }

            Iterator<ItemStack> iter = knowledge.iterator();

            ItemStack filtered = stack.copy();
            filtered = ItemStackNBTManager.clean(filtered);
            
            while (iter.hasNext())
            {
                if (ItemStack.areItemStacksEqual(filtered, iter.next()))
                {
                    iter.remove();
                    removed = true;
                }
            }

            if (removed)
            {
                fireChangedEvent();
            }
            return removed;
        }

        @Override
        public @Nonnull List<ItemStack> getKnowledge() {
            return fullKnowledge ? Transmutation.getCachedTomeKnowledge() : Collections.unmodifiableList(knowledge);
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
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound properties = new NBTTagCompound();
            properties.putDouble("transmutationEmc", emc);

            NBTTagList knowledgeWrite = new NBTTagList();
            for (ItemStack i : knowledge)
            {
                NBTTagCompound tag = i.write(new NBTTagCompound());
                knowledgeWrite.add(tag);
            }

            properties.put("knowledge", knowledgeWrite);
            properties.put("inputlock", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inputLocks, null));
            properties.putBoolean("fullknowledge", fullKnowledge);
            return properties;
        }

        @Override
        public void deserializeNBT(NBTTagCompound properties)
        {
            emc = properties.getDouble("transmutationEmc");

            NBTTagList list = properties.getList("knowledge", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++)
            {
                ItemStack item = ItemStack.read(list.getCompound(i));
                if (!item.isEmpty())
                {
                    knowledge.add(item);
                }
            }

            pruneStaleKnowledge();
            pruneDuplicateKnowledge();

            for (int i = 0; i < inputLocks.getSlots(); i++)
            {
                inputLocks.setStackInSlot(i, ItemStack.EMPTY);
            }

            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inputLocks, null, properties.getList("inputlock", Constants.NBT.TAG_COMPOUND));
            fullKnowledge = properties.getBoolean("fullknowledge");
        }

        private void pruneDuplicateKnowledge()
        {
            ItemHelper.compactItemListNoStacksize(knowledge);
            for (ItemStack s : knowledge)
            {
                if (s.getCount() > 1)
                {
                    s.setCount(1);
                }
            }
        }

        private void pruneStaleKnowledge()
        {
            knowledge.removeIf(stack -> !EMCHelper.doesItemHaveEmc(stack));
        }

    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound>
    {
        public static final ResourceLocation NAME = new ResourceLocation(PECore.MODID, "knowledge");

        private final DefaultImpl impl;
        private final LazyOptional<IKnowledgeProvider> cap;

        public Provider(EntityPlayer player)
        {
            impl = new DefaultImpl(player);
            cap = LazyOptional.of(() -> impl);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
            if (capability == ProjectEAPI.KNOWLEDGE_CAPABILITY)
            {
                return cap.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            return impl.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            impl.deserializeNBT(nbt);
        }

    }

    private KnowledgeImpl() {}

}
