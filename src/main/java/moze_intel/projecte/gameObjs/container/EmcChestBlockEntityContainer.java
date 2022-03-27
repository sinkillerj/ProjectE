package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.block_entities.EmcChestBlockEntity;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public abstract class EmcChestBlockEntityContainer<BE extends EmcChestBlockEntity> extends PEContainer {

	protected final BE blockEntity;

	protected EmcChestBlockEntityContainer(ContainerTypeRegistryObject<? extends EmcChestBlockEntityContainer<BE>> typeRO, int windowId, Inventory playerInv, BE blockEntity) {
		super(typeRO, windowId, playerInv);
		this.blockEntity = blockEntity;
		this.blockEntity.startOpen(playerInv.player);
	}

	@Override
	public void removed(@NotNull Player player) {
		super.removed(player);
		blockEntity.stopOpen(player);
	}

	public boolean blockEntityMatches(EmcChestBlockEntity chest) {
		return chest == blockEntity;
	}
}