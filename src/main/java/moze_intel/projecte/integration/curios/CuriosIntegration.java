package moze_intel.projecte.integration.curios;

import com.google.common.collect.ImmutableSet;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.CuriosCapability;
import top.theillusivec4.curios.api.capability.ICurio;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class CuriosIntegration
{
    @Nullable
    public static IItemHandler getAll(LivingEntity living) {
        return CuriosAPI.getCuriosHandler(living).map(handler -> {
            IItemHandlerModifiable[] invs = handler.getCurioMap().values().toArray(new IItemHandlerModifiable[0]);
            return new CombinedInvWrapper(invs);
        }).orElse(null);
    }

    @SubscribeEvent
    public static void enqueueImc(InterModEnqueueEvent evt) {
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("amulet"));
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("belt"));
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("ring"));
    }

    private static class Provider implements ICapabilityProvider {
        private final LazyOptional<ICurio> curio;

        public Provider(ICurio curio) {
            this.curio = LazyOptional.of(() -> curio);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return CuriosCapability.ITEM.orEmpty(cap, curio);
        }
    }

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<ItemStack> evt) {
        ItemStack stack = evt.getObject();
        ResourceLocation key = new ResourceLocation(PECore.MODID, "curio");
        Set<Item> invTicking = ImmutableSet.of(
                ObjHandler.zero, ObjHandler.everTide, ObjHandler.eternalDensity, ObjHandler.voidRing,
                ObjHandler.repairTalisman, ObjHandler.volcanite, ObjHandler.arcana, ObjHandler.bodyStone,
                ObjHandler.blackHole, ObjHandler.ignition, ObjHandler.lifeStone, ObjHandler.soulStone,
                ObjHandler.swrg
        );

        if(invTicking.contains(stack.getItem())) {
            evt.addCapability(key, new Provider(new DefaultCurio(stack)));
        }
    }
}
