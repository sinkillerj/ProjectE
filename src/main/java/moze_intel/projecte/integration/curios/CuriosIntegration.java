package moze_intel.projecte.integration.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class CuriosIntegration {

	@Nullable
	public static IItemHandler getAll(LivingEntity living) {
		return CuriosApi.getCuriosInventory(living)
				.lazyMap(ICuriosItemHandler::getEquippedCurios)
				.resolve()
				.orElse(null);
	}
}