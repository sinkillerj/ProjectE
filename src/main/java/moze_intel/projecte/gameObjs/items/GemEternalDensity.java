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
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.EternalDensityContainer;
import moze_intel.projecte.gameObjs.container.inventory.EternalDensityInventory;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.WorldHelper;
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
import net.minecraft.util.ActionResultType;
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

public class GemEternalDensity extends ItemPE implements IAlchBagItem, IAlchChestItem, IItemMode
{
	private final String[] modes;

	public GemEternalDensity(Properties props)
	{
		super(props);
		this.addPropertyOverride(ACTIVE_NAME, ACTIVE_GETTER);
		modes = new String[] {
			"item.minecraft.iron_ingot",
			"item.minecraft.gold_ingot",
			"item.minecraft.diamond",
			"item.projecte.dark_matter",
			"item.projecte.red_matter"
		};
		addItemCapability(new AlchBagItemCapabilityWrapper());
		addItemCapability(new AlchChestItemCapabilityWrapper());
		addItemCapability(new ModeChangerItemCapabilityWrapper());
	}
	
	@Override
	public void inventoryTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int slot, boolean isHeld)
	{
		if (world.isRemote || !(entity instanceof PlayerEntity))
		{
			return;
		}

		entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP)
				.ifPresent(inv -> condense(stack, inv));
	}

	/**
	 * @return Whether the inventory was changed
	 */
	private static boolean condense(ItemStack gem, IItemHandler inv)
	{
        if (!gem.getOrCreateTag().getBoolean(TAG_ACTIVE) || ItemPE.getEmc(gem) >= Constants.TILE_MAX_EMC)
		{
			return false;
		}

		boolean hasChanged = false;
		boolean isWhitelist = isWhitelistMode(gem);
		List<ItemStack> whitelist = getWhitelist(gem);
		
		ItemStack target = getTarget(gem);
		
		for (int i = 0; i < inv.getSlots(); i++)
		{
			ItemStack s = inv.getStackInSlot(i);
			
			if (s.isEmpty()
					|| !EMCHelper.doesItemHaveEmc(s) || s.getMaxStackSize() == 1
					|| EMCHelper.getEmcValue(s) >= EMCHelper.getEmcValue(target)
					|| inv.extractItem(i, s.getCount() == 1 ? 1 : s.getCount() / 2, true).isEmpty())
			{
				continue;
			}

			if ((isWhitelist && listContains(whitelist, s)) || (!isWhitelist && !listContains(whitelist, s)))
			{
				ItemStack copy = inv.extractItem(i, s.getCount() == 1 ? 1 : s.getCount() / 2, false);

				addToList(gem, copy);
				
				ItemPE.addEmcToStack(gem, EMCHelper.getEmcValue(copy) * copy.getCount());
				hasChanged = true;
				break;
			}
		}
		
		long value = EMCHelper.getEmcValue(target);

		if (!EMCHelper.doesItemHaveEmc(target))
		{
			return hasChanged;
		}

		while (getEmc(gem) >= value)
		{
			ItemStack remain = ItemHandlerHelper.insertItemStacked(inv, target.copy(), false);

			if (!remain.isEmpty())
			{
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
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote)
		{
			if (player.isSneaking())
			{
                if (stack.getOrCreateTag().getBoolean(TAG_ACTIVE))
				{
					List<ItemStack> items = getItems(stack);
					
					if (!items.isEmpty())
					{
						WorldHelper.createLootDrop(items, world, player.posX, player.posY, player.posZ);

						setItems(stack, new ArrayList<>());
						ItemPE.setEmc(stack, 0);
					}

					stack.getTag().putBoolean(TAG_ACTIVE, false);
				}
				else
				{
					stack.getTag().putBoolean(TAG_ACTIVE, true);
				}
			}
			else
			{
				NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(stack), buf -> buf.writeBoolean(hand == Hand.MAIN_HAND));
			}
		}
		
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}
	
	private static ItemStack getTarget(ItemStack stack)
	{
		Item item = stack.getItem();
		if (!(item instanceof GemEternalDensity)) {
			PECore.LOGGER.fatal("Invalid gem of eternal density: {}", stack);
			return ItemStack.EMPTY;
		}
		byte target = ((GemEternalDensity) item).getMode(stack);
		switch (target)
		{
			case 0:
				return new ItemStack(Items.IRON_INGOT);
			case 1:
				return new ItemStack(Items.GOLD_INGOT);
			case 2:
				return new ItemStack(Items.DIAMOND);
			case 3:
				return new ItemStack(ObjHandler.darkMatter);
			case 4:
				return new ItemStack(ObjHandler.redMatter);
			default:
				PECore.LOGGER.fatal("Invalid target for gem of eternal density: {}", target);
				return ItemStack.EMPTY;
		}
	}
	
	private static void setItems(ItemStack stack, List<ItemStack> list)
	{
		ListNBT tList = new ListNBT();
		
		for (ItemStack s : list)
		{
			CompoundNBT nbt = new CompoundNBT();
			s.write(nbt);
			tList.add(nbt);
		}

        stack.getOrCreateTag().put("Consumed", tList);
	}
	
	private static List<ItemStack> getItems(ItemStack stack)
	{
		List<ItemStack> list = new ArrayList<>();
        ListNBT tList = stack.getOrCreateTag().getList("Consumed", NBT.TAG_COMPOUND);
		
		for (int i = 0; i < tList.size(); i++)
		{
			list.add(ItemStack.read(tList.getCompound(i)));
		}
		
		return list;
	}
	
	private static void addToList(ItemStack gem, ItemStack stack)
	{
		List<ItemStack> list = getItems(gem);
		
		addToList(list, stack);
		
		setItems(gem, list);
	}
	
	private static void addToList(List<ItemStack> list, ItemStack stack)
	{
		boolean hasFound = false;

		for (ItemStack s : list)
		{
			if (s.getCount() < s.getMaxStackSize() && ItemHelper.areItemStacksEqual(s, stack))
			{
				int remain = s.getMaxStackSize() - s.getCount();

				if (stack.getCount() <= remain)
				{
					s.grow(stack.getCount());
					hasFound = true;
					break;
				}
				else
				{
					s.grow(remain);
					stack.shrink(remain);
				}
			}
		}

		if (!hasFound)
		{
			list.add(stack);
		}
	}
	
	private static boolean isWhitelistMode(ItemStack stack)
	{
        return stack.getOrCreateTag().getBoolean("Whitelist");
	}
	
	private static List<ItemStack> getWhitelist(ItemStack stack)
	{
		List<ItemStack> result = new ArrayList<>();
        ListNBT list = stack.getOrCreateTag().getList("Items", NBT.TAG_COMPOUND);
		
		for (int i = 0; i < list.size(); i++)
		{
			result.add(ItemStack.read(list.getCompound(i)));
		}
		
		return result;
	}
	
	private static boolean listContains(List<ItemStack> list, ItemStack stack)
	{
		for (ItemStack s : list)
		{
			if (ItemHelper.areItemStacksEqual(s, stack))
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String[] getModeTranslationKeys() {
		return modes;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flags)
	{
		list.add(new TranslationTextComponent("pe.gemdensity.tooltip1"));
		
		if (stack.hasTag())
		{
			list.add(new TranslationTextComponent("pe.gemdensity.tooltip2").appendSibling(new TranslationTextComponent(getModeTranslationKey(stack))));
		}
		list.add(new TranslationTextComponent("pe.gemdensity.tooltip3", ClientKeyHelper.getKeyName(PEKeybind.MODE)));
		list.add(new TranslationTextComponent("pe.gemdensity.tooltip4"));
		list.add(new TranslationTextComponent("pe.gemdensity.tooltip5"));
	}

	@Override
	public void updateInAlchChest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack)
	{
        if (!world.isRemote && stack.getOrCreateTag().getBoolean(TAG_ACTIVE))
		{
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof AlchChestTile)
			{
				AlchChestTile tile = (AlchChestTile) te;
				tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
						.ifPresent(inv -> condense(stack, inv));
				tile.markDirty();
			}
		}
	}

	@Override
	public boolean updateInAlchBag(@Nonnull IItemHandler inv, @Nonnull PlayerEntity player, @Nonnull ItemStack stack)
	{
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
			return new EternalDensityContainer(windowId, playerInventory,
					new EternalDensityInventory(stack, player));
		}

		@Nonnull
		@Override
		public ITextComponent getDisplayName() {
			return new TranslationTextComponent(ObjHandler.eternalDensity.getTranslationKey());
		}
	}
}
