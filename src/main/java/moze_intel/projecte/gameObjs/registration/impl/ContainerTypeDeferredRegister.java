package moze_intel.projecte.gameObjs.registration.impl;

import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.registration.INamedEntry;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.IContainerFactory;

public class ContainerTypeDeferredRegister extends PEDeferredRegister<MenuType<?>> {

	public ContainerTypeDeferredRegister(String modid) {
		super(Registries.MENU, modid, ContainerTypeRegistryObject::new);
	}

	public <CONTAINER extends AbstractContainerMenu, BE extends BlockEntity> ContainerTypeRegistryObject<CONTAINER> register(INamedEntry nameProvider,
			Class<BE> blockEntityClass, IBlockEntityContainerFactory<CONTAINER, BE> factory) {
		return register(nameProvider, (id, inv, buf) -> factory.create(id, inv, getBlockEntityFromBuf(buf, blockEntityClass)));
	}

	public <CONTAINER extends AbstractContainerMenu> ContainerTypeRegistryObject<CONTAINER> register(INamedEntry nameProvider, IContainerFactory<CONTAINER> factory) {
		return register(nameProvider.getName(), factory);
	}

	public <CONTAINER extends AbstractContainerMenu> ContainerTypeRegistryObject<CONTAINER> register(String name, IContainerFactory<CONTAINER> factory) {
		return registerMenu(name, () -> new MenuType<>(factory, FeatureFlags.VANILLA_SET));
	}

	public <CONTAINER extends AbstractContainerMenu> ContainerTypeRegistryObject<CONTAINER> registerMenu(String name, Supplier<MenuType<CONTAINER>> supplier) {
		return (ContainerTypeRegistryObject<CONTAINER>) super.register(name, supplier);
	}

	private static <BE extends BlockEntity> BE getBlockEntityFromBuf(FriendlyByteBuf buf, Class<BE> type) {
		if (buf == null) {
			throw new IllegalArgumentException("Null packet buffer");
		} else if (!FMLEnvironment.dist.isClient()) {
			throw new UnsupportedOperationException("This method is only supported on the client.");
		}
		BlockPos pos = buf.readBlockPos();
		BE blockEntity = WorldHelper.getBlockEntity(type, Minecraft.getInstance().level, pos);
		if (blockEntity == null) {
			throw new IllegalStateException("Client could not locate block entity at " + pos + " for block entity container. "
											+ "This is likely caused by a mod breaking client side block entity lookup");
		}
		return blockEntity;
	}

	@FunctionalInterface
	public interface IBlockEntityContainerFactory<CONTAINER extends AbstractContainerMenu, BE extends BlockEntity> {

		CONTAINER create(int id, Inventory inv, BE blockEntity);
	}
}