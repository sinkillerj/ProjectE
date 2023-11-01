package moze_intel.projecte.emc.nbt.processor;

import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.nbt.INBTProcessor;
import moze_intel.projecte.api.nbt.NBTProcessor;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NBTProcessor
public class ArmorTrimProcessor implements INBTProcessor {

	@Override
	public String getName() {
		return "ArmorTrimProcessor";
	}

	@Override
	public String getDescription() {
		return "Calculates EMC value of trimmed armor.";
	}

	@Override
	public boolean hasPersistentNBT() {
		return true;
	}

	@Override
	public long recalculateEMC(@NotNull ItemInfo info, long currentEMC) throws ArithmeticException {
		if (info.is(ItemTags.TRIMMABLE_ARMOR)) {
			CompoundTag tag = info.getNBT();
			if (tag != null && tag.contains("Trim", Tag.TAG_COMPOUND)) {
				CompoundTag compoundtag = tag.getCompound("Trim");
				RegistryAccess registryAccess;
				if (FMLEnvironment.dist.isClient()) {
					//Note: Do not set a variable for the level, or it will cause class loading issues on the server
					registryAccess = Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.registryAccess();
				} else {
					registryAccess = ServerLifecycleHooks.getCurrentServer() == null ? null : ServerLifecycleHooks.getCurrentServer().registryAccess();
				}
				if (registryAccess == null) {
					//Error out, return zero so that it doesn't adjust the EMC
					return 0;
				}
				ArmorTrim armortrim = ArmorTrim.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), compoundtag)
						.result()
						.orElse(null);
				if (armortrim != null) {
					Item material = armortrim.material().value().ingredient().value();
					Item template = armortrim.pattern().value().templateItem().value();
					return Math.addExact(
							Math.addExact(currentEMC, EMCHelper.getEmcValue(material)),
							EMCHelper.getEmcValue(template)
					);
				}
			}
		}
		return currentEMC;
	}

	@Nullable
	@Override
	public CompoundTag getPersistentNBT(@NotNull ItemInfo info) {
		if (info.is(ItemTags.TRIMMABLE_ARMOR)) {
			CompoundTag tag = info.getNBT();
			if (tag != null && tag.contains("Trim", Tag.TAG_COMPOUND)) {
				//Note: We don't bother verifying the trim is valid here as that seems like it is probably unnecessary processing
				CompoundTag toReturn = new CompoundTag();
				toReturn.put("Trim", tag.getCompound("Trim"));
				return toReturn;
			}
		}
		return null;
	}
}
