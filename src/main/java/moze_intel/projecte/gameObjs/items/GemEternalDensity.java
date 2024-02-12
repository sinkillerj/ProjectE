package moze_intel.projecte.gameObjs.items;

import com.mojang.logging.LogUtils;
import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.gameObjs.container.EternalDensityContainer;
import moze_intel.projecte.gameObjs.container.inventory.EternalDensityInventory;
import moze_intel.projecte.gameObjs.items.GemEternalDensity.GemMode;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.EntityHandsInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GemEternalDensity extends ItemPE implements IAlchBagItem, IAlchChestItem, IItemMode<GemMode>, ICapabilityAware {

	public GemEternalDensity(Properties props) {
		super(props);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isHeld) {
		super.inventoryTick(stack, level, entity, slot, isHeld);
		if (!level.isClientSide && entity instanceof Player player) {
			condense(stack, new EntityHandsInvWrapper(player));
		}
	}

	/**
	 * @return Whether the inventory was changed
	 */
	private static boolean condense(ItemStack gem, IItemHandler inv) {
		if (!gem.hasData(PEAttachmentTypes.ACTIVE) || !gem.getData(PEAttachmentTypes.ACTIVE) || ItemPE.getEmc(gem) == Constants.BLOCK_ENTITY_MAX_EMC) {
			return false;
		}
		ItemStack target = getTarget(gem);
		long targetEmc = EMCHelper.getEmcValue(target);
		if (targetEmc == 0) {
			//Target doesn't have an EMC value set, just exit early
			return false;
		}
		boolean hasChanged = false;
		boolean isWhitelist = gem.getData(PEAttachmentTypes.GEM_WHITELIST);
		List<ItemStack> whitelist = gem.getData(PEAttachmentTypes.GEM_TARGETS);
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) {
				continue;
			}
			Lazy<Boolean> filtered = Lazy.of(() -> whitelist.stream().anyMatch(s -> ItemHandlerHelper.canItemStacksStack(s, stack)));
			if (!stack.isStackable()) {
				//Only skip unstackable items if they are not explicitly whitelisted
				if (!isWhitelist || !filtered.get()) {
					continue;
				}
			}

			long emcValue = EMCHelper.getEmcValue(stack);
			if (emcValue == 0 || emcValue >= targetEmc || inv.extractItem(i, stack.getCount() == 1 ? 1 : stack.getCount() / 2, true).isEmpty()) {
				continue;
			}

			if (isWhitelist == filtered.get()) {
				ItemStack copy = inv.extractItem(i, stack.getCount() == 1 ? 1 : stack.getCount() / 2, false);
				addToList(gem, copy);
				ItemPE.addEmcToStack(gem, EMCHelper.getEmcValue(copy) * copy.getCount());
				hasChanged = true;
				break;
			}
		}

		long value = EMCHelper.getEmcValue(target);
		if (value == 0) {
			return hasChanged;
		}

		while (getEmc(gem) >= value) {
			ItemStack remain = ItemHandlerHelper.insertItemStacked(inv, target.copy(), false);
			if (!remain.isEmpty()) {
				return false;
			}
			ItemPE.removeEmc(gem, value);
			gem.removeData(PEAttachmentTypes.GEM_CONSUMED);
			hasChanged = true;
		}
		return hasChanged;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide) {
			if (player.isSecondaryUseActive()) {
				if (stack.getData(PEAttachmentTypes.ACTIVE)) {
					List<ItemStack> items = stack.removeData(PEAttachmentTypes.GEM_CONSUMED);
					if (items != null && !items.isEmpty()) {
						WorldHelper.createLootDrop(items, level, player.position());
						ItemPE.setEmc(stack, 0);
					}
					stack.removeData(PEAttachmentTypes.ACTIVE);
				} else {
					stack.setData(PEAttachmentTypes.ACTIVE, true);
				}
			} else {
				player.openMenu(new ContainerProvider(hand, stack), buf -> {
					buf.writeEnum(hand);
					buf.writeByte(player.getInventory().selected);
				});
			}
		}
		return InteractionResultHolder.success(stack);
	}

	private static ItemStack getTarget(ItemStack stack) {
		Item item = stack.getItem();
		if (!(item instanceof GemEternalDensity gem)) {
			PECore.LOGGER.error(LogUtils.FATAL_MARKER, "Invalid gem of eternal density: {}", stack);
			return ItemStack.EMPTY;
		}
		return gem.getMode(stack).getTarget();
	}

	private static void addToList(ItemStack gem, ItemStack stack) {
		List<ItemStack> list = gem.getData(PEAttachmentTypes.GEM_CONSUMED);
		boolean hasFound = false;
		for (ItemStack s : list) {
			if (s.getCount() < s.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(s, stack)) {
				int remain = s.getMaxStackSize() - s.getCount();
				if (stack.getCount() <= remain) {
					s.grow(stack.getCount());
					hasFound = true;
					break;
				} else {
					s.grow(remain);
					stack.shrink(remain);
				}
			}
		}
		if (!hasFound) {
			list.add(stack);
		}
	}

	//TODO - 1.20.4: Theoretically it will work as is because neo has builtin packet splitting for everything now
	// but we may want to evaluate moving this off to world save data (and also removing the ItemHelper method)
	/*@Nullable
	@Override
	public CompoundTag getShareTag(ItemStack stack) {
		if (stack.getItem() instanceof GemEternalDensity) {
			//Double check it is actually a stack of the correct type
			CompoundTag nbt = stack.getTag();
			if (nbt == null || !nbt.contains(Constants.NBT_KEY_GEM_CONSUMED, Tag.TAG_LIST)) {
				//If we don't have any NBT or already don't have the key just return the NBT as is
				return nbt;
			}
			//Don't sync the list of consumed stacks to the client to make sure it doesn't overflow the packet
			return ItemHelper.copyNBTSkipKey(nbt, Constants.NBT_KEY_GEM_CONSUMED);
		}
		return super.getShareTag(stack);
	}*/

	@Override
	public AttachmentType<GemMode> getAttachmentType() {
		return PEAttachmentTypes.GEM_MODE.get();
	}

	@Override
	public ILangEntry getModeSwitchEntry() {
		return PELang.DENSITY_MODE_TARGET;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(PELang.TOOLTIP_GEM_DENSITY_1.translate());
		if (stack.hasTag()) {
			tooltips.add(PELang.TOOLTIP_GEM_DENSITY_2.translate(getMode(stack)));
		}
		tooltips.add(PELang.TOOLTIP_GEM_DENSITY_3.translate(ClientKeyHelper.getKeyName(PEKeybind.MODE)));
		tooltips.add(PELang.TOOLTIP_GEM_DENSITY_4.translate());
		tooltips.add(PELang.TOOLTIP_GEM_DENSITY_5.translate());
	}

	@Override
	public boolean updateInAlchChest(@NotNull Level level, @NotNull BlockPos pos, @NotNull ItemStack stack) {
		if (!level.isClientSide && stack.getData(PEAttachmentTypes.ACTIVE)) {
			IItemHandler handler = WorldHelper.getCapability(level, ItemHandler.BLOCK, pos, null);
			return handler != null && condense(stack, handler);
		}
		return false;
	}

	@Override
	public boolean updateInAlchBag(@NotNull IItemHandler inv, @NotNull Player player, @NotNull ItemStack stack) {
		return !player.level().isClientSide && condense(stack, inv);
	}

	@Override
	public void attachCapabilities(RegisterCapabilitiesEvent event) {
		IntegrationHelper.registerCuriosCapability(event, this);
	}

	private record ContainerProvider(InteractionHand hand, ItemStack stack) implements MenuProvider {

		@NotNull
		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
			return new EternalDensityContainer(windowId, playerInventory, hand, playerInventory.selected, new EternalDensityInventory(stack));
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

		private final ItemLike target;

		GemMode(ItemLike target) {
			this.target = target;
		}

		@Override
		public String getTranslationKey() {
			return target.asItem().getDescriptionId();
		}

		public ItemStack getTarget() {
			return new ItemStack(target);
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