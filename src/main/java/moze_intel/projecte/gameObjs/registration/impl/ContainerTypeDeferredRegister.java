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

	public ContainerTypeDeferredRegister() {
		super(ForgeRegistries.CONTAINERS);
	}

	public <CONTAINER extends AbstractContainerMenu, TILE extends BlockEntity> ContainerTypeRegistryObject<CONTAINER> register(INamedEntry nameProvider, Class<TILE> tileClass,
			ITileContainerFactory<CONTAINER, TILE> factory) {
		return register(nameProvider, (id, inv, buf) -> factory.create(id, inv, getTeFromBuf(buf, tileClass)));
	}

	public <CONTAINER extends AbstractContainerMenu> ContainerTypeRegistryObject<CONTAINER> register(INamedEntry nameProvider, IContainerFactory<CONTAINER> factory) {
		return register(nameProvider.getInternalRegistryName(), factory);
	}

	public <CONTAINER extends AbstractContainerMenu> ContainerTypeRegistryObject<CONTAINER> register(String name, IContainerFactory<CONTAINER> factory) {
		return register(name, () -> new MenuType<>(factory), ContainerTypeRegistryObject::new);
	}

	private static <TILE extends BlockEntity> TILE getTeFromBuf(FriendlyByteBuf buf, Class<TILE> type) {
		if (buf == null) {
			throw new IllegalArgumentException("Null packet buffer");
		}
		return DistExecutor.unsafeRunForDist(() -> () -> {
			BlockPos pos = buf.readBlockPos();
			TILE tile = WorldHelper.getTileEntity(type, Minecraft.getInstance().level, pos);
			if (tile == null) {
				throw new IllegalStateException("Client could not locate tile at " + pos + " for tile container. "
												+ "This is likely caused by a mod breaking client side tile lookup");
			}
			return tile;
		}, () -> () -> {
			throw new RuntimeException("Shouldn't be called on server!");
		});
	}

	public interface ITileContainerFactory<CONTAINER extends AbstractContainerMenu, TILE extends BlockEntity> {

		CONTAINER create(int id, Inventory inv, TILE tile);
	}
}