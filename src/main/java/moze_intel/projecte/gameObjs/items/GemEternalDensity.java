package moze_intel.projecte.gameObjs.items;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.components.GemData;
import moze_intel.projecte.gameObjs.container.EternalDensityContainer;
import moze_intel.projecte.gameObjs.container.PEHandContainer;
import moze_intel.projecte.gameObjs.items.GemEternalDensity.GemMode;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GemEternalDensity extends ItemPE implements IAlchBagItem, IAlchChestItem, IItemMode<GemMode>, ICapabilityAware {

	public GemEternalDensity(Properties props) {
		super(props.component(PEDataComponentTypes.ACTIVE, false)
				.component(PEDataComponentTypes.GEM_MODE, GemMode.IRON)
				.component(PEDataComponentTypes.GEM_DATA, GemData.EMPTY)
				.component(PEDataComponentTypes.STORED_EMC, 0L)
		);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
		super.inventoryTick(stack, level, entity, slot);
		if (!level.isClientSide() && entity instanceof Player player) {
			ResourceHandler<ItemResource> playerInv = player.getCapability(Capabilities.Item.ENTITY);
			if (playerInv != null) {
				condense(stack, playerInv);
			}
		}
	}

	/**
	 * @return Whether the inventory was changed
	 */
	private boolean condense(ItemStack gem, ResourceHandler<ItemResource> inv) {
		if (!gem.getOrDefault(PEDataComponentTypes.ACTIVE, false)) {
			return false;
		}
		final ItemLike target = getMode(gem).getTarget();
		final long targetEmc = IEMCProxy.INSTANCE.getValue(target);
		if (targetEmc == 0) {
			//Target doesn't have an EMC value set, just exit early
			return false;
		}
		long gemEmc = gem.getOrDefault(PEDataComponentTypes.STORED_EMC, 0L);
		if (gemEmc == Long.MAX_VALUE) {
			//If we have max stored, just try to condense whatever we currently have stored, and skip attempting to convert more items into stored emc
			return condenseFromStoredEmc(inv, gem, gemEmc, target, targetEmc);
		}
		long emcRoomFor = Long.MAX_VALUE - gemEmc;
		GemData gemData = gem.getOrDefault(PEDataComponentTypes.GEM_DATA, GemData.EMPTY);
		for (int i = 0, slots = PEItemStacksHandler.getSlotCount(inv); i < slots; i++) {
			ItemStack stack = PEItemStacksHandler.getStack(inv, i);
			if (stack.isEmpty()) {
				continue;
			}
			Boolean filtered = null;
			if (!stack.isStackable()) {
				//Only process unstackable items if they are explicitly whitelisted
				if (!gemData.isWhitelist() || !gemData.whitelistMatches(stack)) {
					continue;
				}
				filtered = true;
			}

			long emcValue = IEMCProxy.INSTANCE.getValue(stack);
			if (emcValue == 0 || emcValue >= targetEmc || emcValue > emcRoomFor) {
				//Continue if the item has no EMC, it is more valuable than our target, or to consume it would make our stored emc overflow
				continue;
			}
			//Note: We know emcValue is strictly smaller or equal to emcRoomFor, so this should always be at least one
			long maxToAdd = emcRoomFor / emcValue;
			int halfStack = stack.getCount() == 1 ? 1 : stack.getCount() / 2;
			//Try to extract half the stack, clamped at the amount we have room for the emc of
			ItemStack simulatedExtraction = PEItemStacksHandler.extractItem(inv, i, (int) Math.min(maxToAdd, halfStack), true);
			//If we couldn't extract anything, or for some reason the handler gave a different type of item than it says is stored in that slot
			// don't bother processing it
			if (!simulatedExtraction.isEmpty() || !ItemStack.isSameItemSameComponents(stack, simulatedExtraction)) {
				if (filtered == null) {
					filtered = gemData.whitelistMatches(stack);
				}
				if (gemData.isWhitelist() == filtered) {
					//Extract the item from the inventory
					ItemStack copy = PEItemStacksHandler.extractItem(inv, i, simulatedExtraction.getCount(), false);
					if (!copy.isEmpty()) {
						// and add how much emc we got from it to our stored emc
						gemEmc += emcValue * copy.getCount();
						gem.set(PEDataComponentTypes.STORED_EMC, gemEmc);
						gem.set(PEDataComponentTypes.GEM_DATA, gemData.addConsumed(copy));
						condenseFromStoredEmc(inv, gem, gemEmc, target, targetEmc);
						return true;
					}
				}
			}
		}
		return condenseFromStoredEmc(inv, gem, gemEmc, target, targetEmc);
	}

	private boolean condenseFromStoredEmc(ResourceHandler<ItemResource> inv, ItemStack gem, long originalGemEmc, ItemLike target, long targetEmc) {
		if (originalGemEmc >= targetEmc) {
			ItemStack targetStack = new ItemStack(target);
			int maxStackSize = targetStack.getMaxStackSize();
			long toInsert = originalGemEmc / targetEmc;
			long gemEmc = originalGemEmc;
			while (toInsert > 0) {
				//Note: We know that it can fit in an int as it is <= maxStackSize
				ItemStack stackToInsert = targetStack.copyWithCount((int) Math.min(toInsert, maxStackSize));
				ItemStack remaining = PEItemStacksHandler.insertStackedRemainder(inv, stackToInsert, false);
				if (remaining.getCount() == stackToInsert.getCount()) {
					//Nothing fit, we can't insert any of this item into the inventory
					break;
				}
				//We inserted the amount minus however much we couldn't insert
				int inserted = stackToInsert.getCount() - remaining.getCount();
				//Decrement toInsert as this will either go to zero and we are done inserting,
				// or it will decrease, and we can try to insert another stack of the material that we fit a full stack of
				toInsert -= inserted;
				//Reduce the amount of emc that we have stored in the gem
				gemEmc -= targetEmc * inserted;
			}
			if (gemEmc != originalGemEmc) {
				//Update the stored emc if it changed
				gem.set(PEDataComponentTypes.STORED_EMC, gemEmc);
				// and update the data to represent we no longer have any items that were consumed
				//TODO: Re-evaluate this, as if some of the stored emc can still be distributed between the items that were consumed,
				// then realistically we don't want to clear them from the consumed list
				gem.update(PEDataComponentTypes.GEM_DATA, GemData.EMPTY, GemData::clearConsumed);
				return true;
			}
		}
		return false;
	}

	@NotNull
	@Override
	public InteractionResult use(Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			if (player.isSecondaryUseActive()) {
				if (stack.getOrDefault(PEDataComponentTypes.ACTIVE, false)) {
					GemData oldData = stack.update(PEDataComponentTypes.GEM_DATA, GemData.EMPTY, GemData::clearConsumed);
					if (oldData != null && !oldData.consumed().isEmpty()) {
						WorldHelper.createLootDrop(oldData.consumed(), level, player.position());
						stack.set(PEDataComponentTypes.STORED_EMC, 0L);
					}
					stack.set(PEDataComponentTypes.ACTIVE, false);
				} else {
					stack.set(PEDataComponentTypes.ACTIVE, true);
				}
			} else {
				int selected = player.getInventory().getSelectedSlot();
				player.openMenu(new ContainerProvider(hand, selected), buf -> {
					buf.writeEnum(hand);
					buf.writeByte(selected);
				});
			}
		}
		return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
	}

	@Override
	public DataComponentType<GemMode> getDataComponentType() {
		return PEDataComponentTypes.GEM_MODE.get();
	}

	@Override
	public GemMode getDefaultMode() {
		return GemMode.IRON;
	}

	@Override
	public ILangEntry getModeSwitchEntry() {
		return PELang.DENSITY_MODE_TARGET;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, context, display, tooltip, flags);
		Component interact = Component.keybind("key.use");
		tooltip.accept(PELang.TOOLTIP_GEM_DENSITY_1.translate());
		tooltip.accept(PELang.TOOLTIP_GEM_DENSITY_2.translate(getMode(stack)));
		tooltip.accept(PELang.TOOLTIP_GEM_DENSITY_3.translate(ClientKeyHelper.getKeyName(PEKeybind.MODE)));
		tooltip.accept(PELang.TOOLTIP_GEM_DENSITY_4.translate(interact));
		tooltip.accept(PELang.TOOLTIP_GEM_DENSITY_5.translate(Component.keybind("key.sneak"), interact));
	}

	@Override
	public boolean updateInAlchChest(@NotNull Level level, @NotNull BlockPos pos, @NotNull ItemStack stack) {
		if (!level.isClientSide() && stack.getOrDefault(PEDataComponentTypes.ACTIVE, false)) {
			ResourceHandler<ItemResource> handler = WorldHelper.getItemHandler(level, pos, null);
			return handler != null && condense(stack, handler);
		}
		return false;
	}

	@Override
	public boolean updateInAlchBag(@NotNull PEItemStacksHandler inv, @NotNull Player player, @NotNull ItemStack stack) {
		return !player.level().isClientSide() && condense(stack, inv);
	}

	@Override
	public void attachCapabilities(RegisterCapabilitiesEvent event) {
		IntegrationHelper.registerCuriosCapability(event, this);
	}

	private record ContainerProvider(InteractionHand hand, int selected) implements MenuProvider {

		@Nullable
		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
			if (PEHandContainer.getStack(playerInventory, hand, selected).getItem() instanceof GemEternalDensity) {
				return new EternalDensityContainer(windowId, playerInventory, hand, playerInventory.getSelectedSlot());
			}
			return null;
		}

		@NotNull
		@Override
		public Component getDisplayName() {
			return TextComponentUtil.build(PEItems.GEM_OF_ETERNAL_DENSITY.get());
		}
	}

	public enum GemMode implements IModeEnum<GemMode> {
		IRON(Items.IRON_INGOT),
		GOLD(Items.GOLD_INGOT),
		DIAMOND(Items.DIAMOND),
		DARK_MATTER(PEItems.DARK_MATTER),
		RED_MATTER(PEItems.RED_MATTER);

		public static final Codec<GemMode> CODEC = StringRepresentable.fromEnum(GemMode::values);
		public static final IntFunction<GemMode> BY_ID = ByIdMap.continuous(GemMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
		public static final StreamCodec<ByteBuf, GemMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, GemMode::ordinal);

		private final String serializedName;
		private final ItemLike target;

		GemMode(ItemLike target) {
			this.serializedName = name().toLowerCase(Locale.ROOT);
			this.target = target;
		}

		@NotNull
		@Override
		public String getSerializedName() {
			return serializedName;
		}

		@Override
		public String getTranslationKey() {
			return target.asItem().getDescriptionId();
		}

		public ItemLike getTarget() {
			return target;
		}

		@Override
		public GemMode next(ItemStack stack) {
			return switch (this) {
				case IRON -> GOLD;
				case GOLD -> DIAMOND;
				case DIAMOND -> DARK_MATTER;
				case DARK_MATTER -> RED_MATTER;
				case RED_MATTER -> IRON;
			};
		}
	}
}
