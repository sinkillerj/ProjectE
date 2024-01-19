package moze_intel.projecte.gameObjs.block_entities;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class DMFurnaceBlockEntity extends EmcBlockEntity implements MenuProvider, RecipeCraftingHolder {

	public static final ICapabilityProvider<DMFurnaceBlockEntity, @Nullable Direction, IItemHandler> INVENTORY_PROVIDER = (furnace, side) -> {
		if (side == null) {
			return furnace.joined;
		} else if (side == Direction.UP) {
			return furnace.automationInput;
		} else if (side == Direction.DOWN) {
			return furnace.automationOutput;
		}
		return furnace.automationSides;
	};
	private static final long EMC_CONSUMPTION = 2;

	private final CompactableStackHandler inputInventory = new CompactableStackHandler(getInvSize()) {
		private ItemStack oldInput = ItemStack.EMPTY;

		@Override
		protected void onLoad() {
			oldInput = getStackInSlot(0).copy();
		}

		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			if (slot == 0) {
				ItemStack input = getStackInSlot(0);
				if (!ItemStack.isSameItemSameTags(oldInput, input)) {
					//Reset the cooking progress
					RecipeResult recipeResult = level == null ? RecipeResult.EMPTY : getSmeltingRecipe(level, input, getFuelItem());
					cookingTotalTime = getTotalCookTime(recipeResult);
					cookingProgress = 0;
					oldInput = input.copy();
				}
			}
		}
	};
	private final CompactableStackHandler outputInventory = new CompactableStackHandler(getInvSize());
	private final StackHandler fuelInv = new StackHandler(1);

	private final IItemHandler joined;
	private final IItemHandlerModifiable automationInput;
	private final IItemHandlerModifiable automationOutput;
	private final IItemHandler automationSides;

	protected final int ticksBeforeSmelt;
	private final int efficiencyBonus;
	private final RecipeWrapper dummyFurnace = new RecipeWrapper(new ItemStackHandler(3));
	private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
	private final RecipeManager.CachedCheck<Container, SmeltingRecipe> quickCheck;

	@Nullable
	private BlockCapabilityCache<IItemHandler, @Nullable Direction> pullTarget;
	@Nullable
	private BlockCapabilityCache<IItemHandler, @Nullable Direction> pushTarget;

	public int litTime;
	public int litDuration;
	public int cookingProgress;
	public int cookingTotalTime;

	public DMFurnaceBlockEntity(BlockPos pos, BlockState state) {
		this(PEBlockEntityTypes.DARK_MATTER_FURNACE, pos, state, 10, 3);
	}

	protected DMFurnaceBlockEntity(BlockEntityTypeRegistryObject<? extends DMFurnaceBlockEntity> type, BlockPos pos, BlockState state, int ticksBeforeSmelt, int efficiencyBonus) {
		super(type, pos, state, 64);
		this.ticksBeforeSmelt = ticksBeforeSmelt;
		this.efficiencyBonus = efficiencyBonus;
		this.quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);

		this.automationInput = new WrappedItemHandler(inputInventory, WrappedItemHandler.WriteMode.IN) {
			@NotNull
			@Override
			public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
				return hasSmeltingResult(stack) ? super.insertItem(slot, stack, simulate) : stack;
			}
		};
		this.automationOutput = new WrappedItemHandler(outputInventory, WrappedItemHandler.WriteMode.OUT);
		IItemHandlerModifiable automationFuel = new WrappedItemHandler(fuelInv, WrappedItemHandler.WriteMode.IN) {
			@NotNull
			@Override
			public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
				return SlotPredicates.FURNACE_FUEL.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
			}
		};
		this.automationSides = new CombinedInvWrapper(automationFuel, automationOutput);
		this.joined = new CombinedInvWrapper(automationInput, automationFuel, automationOutput);
	}

	@Override
	public void setLevel(@NotNull Level level) {
		super.setLevel(level);
		if (level instanceof ServerLevel serverLevel) {
			pullTarget = BlockCapabilityCache.create(ItemHandler.BLOCK, serverLevel, worldPosition.above(), Direction.DOWN);
			pushTarget = BlockCapabilityCache.create(ItemHandler.BLOCK, serverLevel, worldPosition.below(), Direction.UP);
		}
	}

	@Override
	protected boolean canProvideEmc() {
		return false;
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	protected long getEmcInsertLimit() {
		return EMC_CONSUMPTION;
	}

	protected int getInvSize() {
		return 9;
	}

	protected float getOreDoubleChance() {
		return 0.5F;
	}

	protected float getDoubleChance(ItemStack input) {
		if (input.is(Tags.Items.ORES)) {
			return getOreDoubleChance();
		} else if (input.is(Tags.Items.RAW_MATERIALS)) {
			//Base rate for raw ore doubling chance is: 1 -> 1.333 which means we multiply our ore double chance by 2/3
			return getOreDoubleChance() * 2 / 3;
		}
		return 0;
	}

	public float getBurnProgress() {
		if (cookingTotalTime == 0 || level == null) {
			return 0;
		}
		//TODO - 1.20.4: Re-evaluate this check that adjusts progress by one
		int progress = isLit() && canSmelt(getSmeltingRecipe(level, getItemToSmelt(), getFuelItem())) ? cookingProgress + 1 : cookingProgress;
		return Mth.clamp(progress / (float) cookingTotalTime, 0, 1);
	}

	@NotNull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInv, @NotNull Player playerIn) {
		return new DMFurnaceContainer(windowId, playerInv, this);
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return PELang.GUI_DARK_MATTER_FURNACE.translate();
	}

	public IItemHandler getFuel() {
		return fuelInv;
	}

	private ItemStack getItemToSmelt() {
		return inputInventory.getStackInSlot(0);
	}

	private ItemStack getFuelItem() {
		return fuelInv.getStackInSlot(0);
	}

	public IItemHandler getInput() {
		return inputInventory;
	}

	public IItemHandler getOutput() {
		return outputInventory;
	}

	public static void tickServer(Level level, BlockPos pos, BlockState state, DMFurnaceBlockEntity furnace) {
		boolean wasBurning = furnace.isLit();
		int lastLitTime = furnace.litTime;
		int lastCookingProgress = furnace.cookingProgress;
		if (furnace.isLit()) {
			--furnace.litTime;
		}
		furnace.inputInventory.compact();
		furnace.outputInventory.compact();
		furnace.pullFromInventories();
		ItemStack fuelItem = furnace.getFuelItem();

		RecipeResult recipeResult = furnace.getSmeltingRecipe(level, furnace.getItemToSmelt(), fuelItem);
		boolean canSmelt = furnace.canSmelt(recipeResult);
		if (canSmelt && !fuelItem.isEmpty()) {
			IItemEmcHolder emcHolder = fuelItem.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
			if (emcHolder != null) {
				long simulatedExtraction = emcHolder.extractEmc(fuelItem, EMC_CONSUMPTION, EmcAction.SIMULATE);
				if (simulatedExtraction == EMC_CONSUMPTION) {
					furnace.forceInsertEmc(emcHolder.extractEmc(fuelItem, simulatedExtraction, EmcAction.EXECUTE), EmcAction.EXECUTE);
				}
				furnace.markDirty(false);
			}
		}

		if (furnace.getStoredEmc() >= EMC_CONSUMPTION) {
			furnace.litTime = 1;
			furnace.forceExtractEmc(EMC_CONSUMPTION, EmcAction.EXECUTE);
		}

		if (canSmelt) {
			if (furnace.litTime == 0) {
				furnace.litDuration = furnace.litTime = furnace.getItemBurnTime(fuelItem);
				if (furnace.isLit() && !fuelItem.isEmpty()) {
					ItemStack copy = fuelItem.copy();
					fuelItem.shrink(1);
					furnace.fuelInv.onContentsChanged(0);
					if (fuelItem.isEmpty()) {
						furnace.fuelInv.setStackInSlot(0, copy.getItem().getCraftingRemainingItem(copy));
					}
					furnace.markDirty(false);
				}
			}
			if (furnace.isLit() && ++furnace.cookingProgress == furnace.cookingTotalTime) {
				furnace.cookingProgress = 0;
				furnace.cookingTotalTime = furnace.getTotalCookTime(recipeResult);
				furnace.smeltItem(level, recipeResult);
			}
		} else {
			furnace.cookingProgress = 0;
		}
		if (wasBurning != furnace.isLit()) {
			if (state.getBlock() instanceof MatterFurnace) {
				//Should always be true, but validate it just in case
				level.setBlockAndUpdate(pos, state.setValue(MatterFurnace.LIT, furnace.isLit()));
			}
			furnace.setChanged();
		}
		furnace.pushToInventories();
		if (lastLitTime != furnace.litTime || lastCookingProgress != furnace.cookingProgress) {
			furnace.markDirty(false);
		}
		furnace.updateComparators();
	}

	public boolean isLit() {
		return litTime > 0;
	}

	private boolean isHopper(BlockPos position) {
		//We let hoppers go at their normal rate
		return WorldHelper.getBlockEntity(level, position) instanceof Hopper;
	}

	private void pullFromInventories() {
		if (pullTarget == null || isHopper(worldPosition.above())) {
			return;
		}
		IItemHandler handler = pullTarget.getCapability();
		if (handler != null) {
			for (int i = 0, slots = handler.getSlots(); i < slots; i++) {
				ItemStack extractTest = handler.extractItem(i, Integer.MAX_VALUE, true);
				if (!extractTest.isEmpty()) {
					IItemHandler targetInv = SlotPredicates.FURNACE_FUEL.test(extractTest) ? fuelInv : inputInventory;
					transferItem(targetInv, i, extractTest, handler);
				}
			}
		}
	}

	private void pushToInventories() {
		if (pushTarget == null || outputInventory.isEmpty() || isHopper(worldPosition.below())) {
			return;
		}
		IItemHandler targetInv = pushTarget.getCapability();
		if (targetInv != null) {
			for (int i = 0, slots = outputInventory.getSlots(); i < slots; i++) {
				ItemStack extractTest = outputInventory.extractItem(i, Integer.MAX_VALUE, true);
				if (!extractTest.isEmpty()) {
					transferItem(targetInv, i, extractTest, outputInventory);
				}
			}
		}
	}

	private void transferItem(IItemHandler targetInv, int i, ItemStack extractTest, IItemHandler outputInventory) {
		ItemStack remainderTest = ItemHandlerHelper.insertItemStacked(targetInv, extractTest, true);
		int successfullyTransferred = extractTest.getCount() - remainderTest.getCount();
		if (successfullyTransferred > 0) {
			ItemStack toInsert = outputInventory.extractItem(i, successfullyTransferred, false);
			ItemStack result = ItemHandlerHelper.insertItemStacked(targetInv, toInsert, false);
			assert result.isEmpty();
		}
	}

	private RecipeResult getSmeltingRecipe(@NotNull Level level, ItemStack input, ItemStack fuel) {
		if (input.isEmpty()) {
			return RecipeResult.EMPTY;
		}
		//Note: We copy the input and fuel so that if anyone attempts to mutate the inventory from assemble then there is no side effects that occur
		dummyFurnace.setItem(0, input.copyWithCount(1));//AbstractFurnaceBlockEntity.SLOT_INPUT
		dummyFurnace.setItem(1, fuel.copyWithCount(1));//AbstractFurnaceBlockEntity.SLOT_FUEL
		dummyFurnace.setItem(2, ItemStack.EMPTY);//AbstractFurnaceBlockEntity.SLOT_RESULT
		RecipeResult recipeResult = quickCheck.getRecipeFor(dummyFurnace, level)
				.map(recipeHolder -> new RecipeResult(recipeHolder, recipeHolder.value().assemble(dummyFurnace, level.registryAccess())))
				.orElse(RecipeResult.EMPTY);
		dummyFurnace.clearContent();
		return recipeResult;
	}

	public boolean hasSmeltingResult(ItemStack input) {
		return level != null && !getSmeltingRecipe(level, input, getFuelItem()).result().isEmpty();
	}

	private void smeltItem(@NotNull Level level, @NotNull RecipeResult recipeResult) {
		ItemStack toSmelt = getItemToSmelt();
		ItemStack smeltResult = recipeResult.scaledResult(level.random, getDoubleChance(toSmelt));
		if (!smeltResult.isEmpty()) {//Double-check the result isn't somehow empty
			ItemHandlerHelper.insertItemStacked(outputInventory, smeltResult, false);

			if (toSmelt.is(Items.WET_SPONGE)) {
				//Hardcoded handling of wet sponge to filling a bucket with water
				ItemStack fuelItem = getFuelItem();
				if (!fuelItem.isEmpty() && fuelItem.is(Items.BUCKET)) {
					fuelInv.setStackInSlot(0, new ItemStack(Items.WATER_BUCKET));
				}
			}

			toSmelt.shrink(1);
			inputInventory.onContentsChanged(0);
			setRecipeUsed(recipeResult.recipeHolder());
		}
	}

	private boolean canSmelt(RecipeResult recipeResult) {
		ItemStack smeltResult = recipeResult.result();
		if (smeltResult.isEmpty()) {
			return false;
		}
		ItemStack currentSmelted = outputInventory.getStackInSlot(outputInventory.getSlots() - 1);
		if (currentSmelted.isEmpty()) {
			return true;
		} else if (!ItemHandlerHelper.canItemStacksStack(smeltResult, currentSmelted)) {
			return false;
		}
		int result = currentSmelted.getCount() + smeltResult.getCount();
		return result <= currentSmelted.getMaxStackSize();
	}

	private int getItemBurnTime(ItemStack stack) {
		return CommonHooks.getBurnTime(stack, RecipeType.SMELTING) * ticksBeforeSmelt / 200 * efficiencyBonus;
	}

	private int getTotalCookTime(RecipeResult recipeResult) {
		if (recipeResult.recipeHolder() == null) {
			return ticksBeforeSmelt;
		}
		int cookingTime = recipeResult.recipeHolder().value().getCookingTime();
		return Mth.ceil(ticksBeforeSmelt * cookingTime / 200F);
	}

	public float getLitProgress() {
		int litDuration = this.litDuration;
		if (litDuration == 0) {
			litDuration = ticksBeforeSmelt;
		}
		return Mth.clamp(litTime / (float) litDuration, 0, 1);
	}

	@Override
	public void load(@NotNull CompoundTag nbt) {
		super.load(nbt);
		litTime = nbt.getInt("BurnTime");
		cookingProgress = nbt.getInt("CookTime");
		cookingTotalTime = nbt.getInt("CookTimeTotal");
		fuelInv.deserializeNBT(nbt.getCompound("Fuel"));
		inputInventory.deserializeNBT(nbt.getCompound("Input"));
		outputInventory.deserializeNBT(nbt.getCompound("Output"));
		litDuration = getItemBurnTime(getFuelItem());
		//[VanillaCopy] AbstractFurnaceBlockEntity
		CompoundTag usedRecipes = nbt.getCompound("RecipesUsed");
		for (String recipeId : usedRecipes.getAllKeys()) {
			this.recipesUsed.put(new ResourceLocation(recipeId), usedRecipes.getInt(recipeId));
		}
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("BurnTime", litTime);
		tag.putInt("CookTime", cookingProgress);
		tag.putInt("CookTimeTotal", this.cookingTotalTime);
		tag.put("Input", inputInventory.serializeNBT());
		tag.put("Output", outputInventory.serializeNBT());
		tag.put("Fuel", fuelInv.serializeNBT());
		//[VanillaCopy] AbstractFurnaceBlockEntity
		CompoundTag usedRecipes = new CompoundTag();
		this.recipesUsed.forEach((recipeId, timesUsed) -> usedRecipes.putInt(recipeId.toString(), timesUsed));
		tag.put("RecipesUsed", usedRecipes);
	}

	@Override
	public void setRecipeUsed(@Nullable RecipeHolder<?> recipeHolder) {
		//[VanillaCopy] AbstractFurnaceBlockEntity
		if (recipeHolder != null) {
			this.recipesUsed.addTo(recipeHolder.id(), 1);
		}
	}

	@Nullable
	@Override
	public RecipeHolder<?> getRecipeUsed() {
		//[VanillaCopy] AbstractFurnaceBlockEntity, always return null
		return null;
	}

	@Override
	public void awardUsedRecipes(@NotNull Player player, @NotNull List<ItemStack> items) {
		//[VanillaCopy] AbstractFurnaceBlockEntity, no-op
	}

	//[VanillaCopy] AbstractFurnaceBlockEntity
	public void awardUsedRecipesAndPopExperience(ServerPlayer player) {
		List<RecipeHolder<?>> recipes = getRecipesToAwardAndPopExperience(player.serverLevel(), player.position());
		player.awardRecipes(recipes);

		for (RecipeHolder<?> recipeholder : recipes) {
			//Note: We don't have a good way to access the list of input items that were present, so we just skip it
			// and only support triggering recipe triggers that are based on the recipe id
			player.triggerRecipeCrafted(recipeholder, List.of());
		}

		this.recipesUsed.clear();
	}

	//[VanillaCopy] AbstractFurnaceBlockEntity
	public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 popVec) {
		RecipeManager recipeManager = level.getRecipeManager();
		List<RecipeHolder<?>> list = new ArrayList<>();
		for (Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
			recipeManager.byKey(entry.getKey()).ifPresent(recipeHolder -> {
				list.add(recipeHolder);
				//Validate it is actually a cooking recipe
				if (recipeHolder.value() instanceof SmeltingRecipe recipe) {
					createExperience(level, popVec, entry.getIntValue(), recipe.getExperience());
				}
			});
		}
		return list;
	}

	//[VanillaCopy] AbstractFurnaceBlockEntity
	private static void createExperience(ServerLevel level, Vec3 popVec, int recipeIndex, float experience) {
		float indexBasedExperience = recipeIndex * experience;
		int amount = Mth.floor(indexBasedExperience);
		float partial = indexBasedExperience - amount;
		if (partial != 0.0F && Math.random() < (double) partial) {
			++amount;
		}

		ExperienceOrb.award(level, popVec, amount);
	}

	private record RecipeResult(@Nullable RecipeHolder<SmeltingRecipe> recipeHolder, ItemStack result) {

		private static final RecipeResult EMPTY = new RecipeResult(null, ItemStack.EMPTY);

		public ItemStack scaledResult(RandomSource random, float doubleChance) {
			if (random.nextFloat() < doubleChance) {
				return result.copyWithCount(2 * result.getCount());
			}
			return result.copy();
		}
	}
}