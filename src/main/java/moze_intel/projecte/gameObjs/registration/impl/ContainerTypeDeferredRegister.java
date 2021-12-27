package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.gameObjs.registration.INamedEntry;
import moze_intel.projecte.gameObjs.registration.WrappedDeferredRegister;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerTypeDeferredRegister extends WrappedDeferredRegister<ContainerType<?>> {

	public ContainerTypeDeferredRegister() {
		super(ForgeRegistries.CONTAINERS);
	}

	public <CONTAINER extends Container, TILE extends TileEntity> ContainerTypeRegistryObject<CONTAINER> register(INamedEntry nameProvider, Class<TILE> tileClass,
			ITileContainerFactory<CONTAINER, TILE> factory) {
		return register(nameProvider, (id, inv, buf) -> factory.create(id, inv, getTeFromBuf(buf, tileClass)));
	}

	public <CONTAINER extends Container> ContainerTypeRegistryObject<CONTAINER> register(INamedEntry nameProvider, IContainerFactory<CONTAINER> factory) {
		return register(nameProvider.getInternalRegistryName(), factory);
	}

	public <CONTAINER extends Container> ContainerTypeRegistryObject<CONTAINER> register(String name, IContainerFactory<CONTAINER> factory) {
		return register(name, () -> IForgeContainerType.create(factory), ContainerTypeRegistryObject::new);
	}

	private static <TILE extends TileEntity> TILE getTeFromBuf(PacketBuffer buf, Class<TILE> type) {
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

	public interface ITileContainerFactory<CONTAINER extends Container, TILE extends TileEntity> {

		CONTAINER create(int id, PlayerInventory inv, TILE tile);
	}
}