package moze_intel.projecte.gameObjs.items;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.capability.AlchBagItemCapabilityWrapper;
import moze_intel.projecte.capability.AlchChestItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.block_entities.EmcBlockEntity;
import moze_intel.projecte.gameObjs.container.EternalDensityContainer;
import moze_intel.projecte.gameObjs.container.inventory.EternalDensityInventory;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GemEternalDensity extends ItemPE implements IAlchBagItem, IAlchChestItem, IItemMode {

	private static final ILangEntry[] modes = new ILangEntry[]{
			Items.IRON_INGOT::getDescriptionId,
			Items.GOLD_INGOT::getDescriptionId,
			Items.DIAMOND::getDescriptionId,
			PEItems.DARK_MATTER::getTranslationKey,
			PEItems.RED_MATTER::getTranslationKey
	};

	public GemEternalDensity(Properties props) {
		super(props);
		addItemCapability(AlchBagItemCapabilityWrapper::new);
		addItemCapability(AlchChestItemCapabilityWrapper::new);
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slot, boolean isHeld) {
		if (!level.isClientSide && entity instanceof Player) {
			entity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).ifPresent(inv -> condense(stack, inv));
		}
	}

	/**
	 * @return Whether the inventory was changed
	 */
	private static boolean condense(ItemStack gem, IItemHandler inv) {
		if (!gem.getOrCreateTag().getBoolean(Constants.NBT_KEY_ACTIVE) || ItemPE.getEmc(gem) >= Constants.BLOCK_ENTITY_MAX_EMC) {
			return false;
		}
		ItemStack target = getTarget(gem);
		long targetEmc = EMCHelper.getEmcValue(target);
		if (targetEmc == 0) {
			//Target doesn't have an EMC value set, just exit early
			return false;
		}
		boolean hasChanged = false;
		boolean isWhitelist = ItemHelper.checkItemNBT(gem, Constants.NBT_KEY_GEM_WHITELIST);
		List<ItemStack> whitelist = getWhitelist(gem);
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) {
				continue;
			}
			Lazy<Boolean> whiteListed = Lazy.of(() -> whitelist.stream().anyMatch(s -> ItemHandlerHelper.canItemStacksStack(s, stack)));
			if (!stack.isStackable()) {
				//Only skip unstackable items if they are not explicitly whitelisted
				if (!isWhitelist || !whiteListed.get()) {
					continue;
				}
			}

			long emcValue = EMCHelper.getEmcValue(stack);
			if (emcValue == 0 || emcValue >= targetEmc || inv.extractItem(i, stack.getCount() == 1 ? 1 : stack.getCount() / 2, true).isEmpty()) {
				continue;
			}

			if (isWhitelist == whiteListed.get()) {
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
			setItems(gem, new ArrayList<>());
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
				CompoundTag nbt = stack.getOrCreateTag();
				if (nbt.getBoolean(Constants.NBT_KEY_ACTIVE)) {
					List<ItemStack> items = getItems(stack);
					if (!items.isEmpty()) {
						WorldHelper.createLootDrop(items, level, player.getX(), player.getY(), player.getZ());
						setItems(stack, new ArrayList<>());
						ItemPE.setEmc(stack, 0);
					}
					nbt.putBoolean(Constants.NBT_KEY_ACTIVE, false);
				} else {
					nbt.putBoolean(Constants.NBT_KEY_ACTIVE, true);
				}
			} else {
				NetworkHooks.openScreen((ServerPlayer) player, new ContainerProvider(hand, stack), buf -> {
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
		byte target = gem.getMode(stack);
		return switch (target) {
			case 0 -> new ItemStack(Items.IRON_INGOT);
			case 1 -> new ItemStack(Items.GOLD_INGOT);
			case 2 -> new ItemStack(Items.DIAMOND);
			case 3 -> new ItemStack(PEItems.DARK_MATTER);
			case 4 -> new ItemStack(PEItems.RED_MATTER);
			default -> {
				PECore.LOGGER.error(LogUtils.FATAL_MARKER, "Invalid target for gem of eternal density: {}", target);
				yield ItemStack.EMPTY;
			}
		};
	}

	private static void setItems(ItemStack stack, List<ItemStack> list) {
		ListTag tList = new ListTag();
		for (ItemStack s : list) {
			CompoundTag nbt = new CompoundTag();
			s.save(nbt);
			tList.add(nbt);
		}
		stack.getOrCreateTag().put(Constants.NBT_KEY_GEM_CONSUMED, tList);
	}

	private static List<ItemStack> getItems(ItemStack stack) {
		List<ItemStack> list = new ArrayList<>();
		if (stack.hasTag()) {
			ListTag tList = stack.getOrCreateTag().getList(Constants.NBT_KEY_GEM_CONSUMED, Tag.TAG_COMPOUND);
			for (int i = 0; i < tList.size(); i++) {
				list.add(ItemStack.of(tList.getCompound(i)));
			}
		}
		return list;
	}

	private static void addToList(ItemStack gem, ItemStack stack) {
		List<ItemStack> list = getItems(gem);
		addToList(list, stack);
		setItems(gem, list);
	}

	private static void addToList(List<ItemStack> list, ItemStack stack) {
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

	@Nullable
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
	}

	private static List<ItemStack> getWhitelist(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag compound = stack.getOrCreateTag().getCompound(Constants.NBT_KEY_GEM_ITEMS);
			ListTag list = compound.getList(Constants.NBT_KEY_GEM_ITEMS, Tag.TAG_COMPOUND);
			List<ItemStack> result = new ArrayList<>(list.size());
			for (int i = 0; i < list.size(); i++) {
				ItemStack s = ItemStack.of(list.getCompound(i));
				if (!s.isEmpty() && result.stream().noneMatch(r -> ItemHandlerHelper.canItemStacksStack(r, s))) {
					//Only add unique whitelist entries
					result.add(s);
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public ILangEntry getModeSwitchEntry() {
		return PELang.DENSITY_MODE_TARGET;
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return modes;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(PELang.TOOLTIP_GEM_DENSITY_1.translate());
		if (stack.hasTag()) {
			tooltips.add(PELang.TOOLTIP_GEM_DENSITY_2.translate(getModeLangEntry(stack)));
		}
		tooltips.add(PELang.TOOLTIP_GEM_DENSITY_3.translate(ClientKeyHelper.getKeyName(PEKeybind.MODE)));
		tooltips.add(PELang.TOOLTIP_GEM_DENSITY_4.translate());
		tooltips.add(PELang.TOOLTIP_GEM_DENSITY_5.translate());
	}

	@Override
	public boolean updateInAlchChest(@NotNull Level level, @NotNull BlockPos pos, @NotNull ItemStack stack) {
		if (!level.isClientSide && ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			EmcBlockEntity chest = WorldHelper.getBlockEntity(EmcBlockEntity.class, level, pos, true);
			if (chest != null) {
				chest.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
					if (condense(stack, inv)) {
						chest.setChanged();
					}
				});
			}
		}
		return false;
	}

	@Override
	public boolean updateInAlchBag(@NotNull IItemHandler inv, @NotNull Player player, @NotNull ItemStack stack) {
		return !player.getCommandSenderWorld().isClientSide && condense(stack, inv);
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
}