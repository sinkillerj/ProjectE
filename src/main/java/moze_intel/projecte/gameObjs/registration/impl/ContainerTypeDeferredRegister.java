package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.gameObjs.registration.INamedEntry;
import moze_intel.projecte.gameObjs.registration.WrappedDeferredRegister;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerTypeDeferredRegister extends WrappedDeferredRegister<MenuType<?>> {

	public ContainerTypeDeferredRegister(String modid) {
		super(ForgeRegistries.CONTAINERS, modid);
	}

	public <CONTAINER extends AbstractContainerMenu, BE extends BlockEntity> ContainerTypeRegistryObject<CONTAINER> register(INamedEntry nameProvider,
			Class<BE> blockEntityClass, IBlockEntityContainerFactory<CONTAINER, BE> factory) {
		return register(nameProvider, (id, inv, buf) -> factory.create(id, inv, getBlockEntityFromBuf(buf, blockEntityClass)));
	}

	public <CONTAINER extends AbstractContainerMenu> ContainerTypeRegistryObject<CONTAINER> register(INamedEntry nameProvider, IContainerFactory<CONTAINER> factory) {
		return register(nameProvider.getInternalRegistryName(), factory);
	}

	public <CONTAINER extends AbstractContainerMenu> ContainerTypeRegistryObject<CONTAINER> register(String name, IContainerFactory<CONTAINER> factory) {
		return register(name, () -> new MenuType<>(factory), ContainerTypeRegistryObject::new);
	}

	private static <BE extends BlockEntity> BE getBlockEntityFromBuf(FriendlyByteBuf buf, Class<BE> type) {
		if (buf == null) {
			throw new IllegalArgumentException("Null packet buffer");
		}
		return DistExecutor.unsafeRunForDist(() -> () -> {
			BlockPos pos = buf.readBlockPos();
			BE blockEntity = WorldHelper.getBlockEntity(type, Minecraft.getInstance().level, pos);
			if (blockEntity == null) {
				throw new IllegalStateException("Client could not locate block entity at " + pos + " for block entity container. "
												+ "This is likely caused by a mod breaking client side block entity lookup");
			}
			return blockEntity;
		}, () -> () -> {
			throw new RuntimeException("Shouldn't be called on server!");
		});
	}

	@FunctionalInterface
	public interface IBlockEntityContainerFactory<CONTAINER extends AbstractContainerMenu, BE extends BlockEntity> {

		CONTAINER create(int id, Inventory inv, BE blockEntity);
	}
}