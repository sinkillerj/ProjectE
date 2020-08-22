package moze_intel.projecte.gameObjs.items;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.capability.AlchBagItemCapabilityWrapper;
import moze_intel.projecte.capability.AlchChestItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.container.EternalDensityContainer;
import moze_intel.projecte.gameObjs.container.inventory.EternalDensityInventory;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class GemEternalDensity extends ItemPE implements IAlchBagItem, IAlchChestItem, IItemMode {

	private static final ILangEntry[] modes = new ILangEntry[]{
			Items.IRON_INGOT::getTranslationKey,
			Items.GOLD_INGOT::getTranslationKey,
			Items.DIAMOND::getTranslationKey,
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
	public void inventoryTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int slot, boolean isHeld) {
		if (!world.isRemote && entity instanceof PlayerEntity) {
			entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).ifPresent(inv -> condense(stack, inv));
		}
	}

	/**
	 * @return Whether the inventory was changed
	 */
	private static boolean condense(ItemStack gem, IItemHandler inv) {
		if (!gem.getOrCreateTag().getBoolean(Constants.NBT_KEY_ACTIVE) || ItemPE.getEmc(gem) >= Constants.TILE_MAX_EMC) {
			return false;
		}

		boolean hasChanged = false;
		boolean isWhitelist = isWhitelistMode(gem);
		List<ItemStack> whitelist = getWhitelist(gem);
		ItemStack target = getTarget(gem);
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack s = inv.getStackInSlot(i);
			if (s.isEmpty() || s.getMaxStackSize() == 1) {
				continue;
			}

			long emcValue = EMCHelper.getEmcValue(s);
			if (emcValue <= 0 || emcValue >= EMCHelper.getEmcValue(target) || inv.extractItem(i, s.getCount() == 1 ? 1 : s.getCount() / 2, true).isEmpty()) {
				continue;
			}

			if ((isWhitelist && listContains(whitelist, s)) || (!isWhitelist && !listContains(whitelist, s))) {
				ItemStack copy = inv.extractItem(i, s.getCount() == 1 ? 1 : s.getCount() / 2, false);
				addToList(gem, copy);
				ItemPE.addEmcToStack(gem, EMCHelper.getEmcValue(copy) * copy.getCount());
				hasChanged = true;
				break;
			}
		}

		long value = EMCHelper.getEmcValue(target);
		if (!EMCHelper.doesItemHaveEmc(target)) {
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

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote) {
			if (player.isSneaking()) {
				CompoundNBT nbt = stack.getOrCreateTag();
				if (nbt.getBoolean(Constants.NBT_KEY_ACTIVE)) {
					List<ItemStack> items = getItems(stack);
					if (!items.isEmpty()) {
						WorldHelper.createLootDrop(items, world, player.getPosX(), player.getPosY(), player.getPosZ());
						setItems(stack, new ArrayList<>());
						ItemPE.setEmc(stack, 0);
					}
					nbt.putBoolean(Constants.NBT_KEY_ACTIVE, false);
				} else {
					nbt.putBoolean(Constants.NBT_KEY_ACTIVE, true);
				}
			} else {
				NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(stack), buf -> buf.writeBoolean(hand == Hand.MAIN_HAND));
			}
		}
		return ActionResult.resultSuccess(stack);
	}

	private static ItemStack getTarget(ItemStack stack) {
		Item item = stack.getItem();
		if (!(item instanceof GemEternalDensity)) {
			PECore.LOGGER.fatal("Invalid gem of eternal density: {}", stack);
			return ItemStack.EMPTY;
		}
		byte target = ((GemEternalDensity) item).getMode(stack);
		switch (target) {
			case 0:
				return new ItemStack(Items.IRON_INGOT);
			case 1:
				return new ItemStack(Items.GOLD_INGOT);
			case 2:
				return new ItemStack(Items.DIAMOND);
			case 3:
				return new ItemStack(PEItems.DARK_MATTER);
			case 4:
				return new ItemStack(PEItems.RED_MATTER);
			default:
				PECore.LOGGER.fatal("Invalid target for gem of eternal density: {}", target);
				return ItemStack.EMPTY;
		}
	}

	private static void setItems(ItemStack stack, List<ItemStack> list) {
		ListNBT tList = new ListNBT();
		for (ItemStack s : list) {
			CompoundNBT nbt = new CompoundNBT();
			s.write(nbt);
			tList.add(nbt);
		}
		stack.getOrCreateTag().put(Constants.NBT_KEY_GEM_CONSUMED, tList);
	}

	private static List<ItemStack> getItems(ItemStack stack) {
		List<ItemStack> list = new ArrayList<>();
		if (stack.hasTag()) {
			ListNBT tList = stack.getTag().getList(Constants.NBT_KEY_GEM_CONSUMED, NBT.TAG_COMPOUND);
			for (int i = 0; i < tList.size(); i++) {
				list.add(ItemStack.read(tList.getCompound(i)));
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
			if (s.getCount() < s.getMaxStackSize() && ItemHelper.areItemStacksEqual(s, stack)) {
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
	public CompoundNBT getShareTag(ItemStack stack) {
		if (stack.getItem() instanceof GemEternalDensity) {
			//Double check it is actually a stack of the correct type
			CompoundNBT nbt = stack.getTag();
			if (nbt == null || !nbt.contains(Constants.NBT_KEY_GEM_CONSUMED, NBT.TAG_LIST)) {
				//If we don't have any NBT or already don't have the key just return the NBT as is
				return nbt;
			}
			//Don't sync the list of consumed stacks to the client to make sure it doesn't overflow the packet
			return ItemHelper.copyNBTSkipKey(nbt, Constants.NBT_KEY_GEM_CONSUMED);
		}
		return super.getShareTag(stack);
	}

	private static boolean isWhitelistMode(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean(Constants.NBT_KEY_GEM_WHITELIST);
	}

	private static List<ItemStack> getWhitelist(ItemStack stack) {
		List<ItemStack> result = new ArrayList<>();
		if (stack.hasTag()) {
			ListNBT list = stack.getTag().getList(Constants.NBT_KEY_GEM_ITEMS, NBT.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				result.add(ItemStack.read(list.getCompound(i)));
			}
		}
		return result;
	}

	private static boolean listContains(List<ItemStack> list, ItemStack stack) {
		return list.stream().anyMatch(s -> ItemHelper.areItemStacksEqual(s, stack));
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
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flags) {
		list.add(PELang.TOOLTIP_GEM_DENSITY_1.translate());
		if (stack.hasTag()) {
			list.add(PELang.TOOLTIP_GEM_DENSITY_2.translate(getModeLangEntry(stack)));
		}
		list.add(PELang.TOOLTIP_GEM_DENSITY_3.translate(ClientKeyHelper.getKeyName(PEKeybind.MODE)));
		list.add(PELang.TOOLTIP_GEM_DENSITY_4.translate());
		list.add(PELang.TOOLTIP_GEM_DENSITY_5.translate());
	}

	@Override
	public void updateInAlchChest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack) {
		if (!world.isRemote && stack.hasTag() && stack.getTag().getBoolean(Constants.NBT_KEY_ACTIVE)) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof AlchChestTile) {
				AlchChestTile tile = (AlchChestTile) te;
				tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> condense(stack, inv));
				tile.markDirty();
			}
		}
	}

	@Override
	public boolean updateInAlchBag(@Nonnull IItemHandler inv, @Nonnull PlayerEntity player, @Nonnull ItemStack stack) {
		return !player.getEntityWorld().isRemote && condense(stack, inv);
	}

	private static class ContainerProvider implements INamedContainerProvider {

		private final ItemStack stack;

		private ContainerProvider(ItemStack stack) {
			this.stack = stack;
		}

		@Nonnull
		@Override
		public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
			return new EternalDensityContainer(windowId, playerInventory, new EternalDensityInventory(stack, player));
		}

		@Nonnull
		@Override
		public ITextComponent getDisplayName() {
			return new TranslationTextComponent(PEItems.GEM_OF_ETERNAL_DENSITY.get().getTranslationKey());
		}
	}
}