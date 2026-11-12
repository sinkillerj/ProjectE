package moze_intel.projecte.gameObjs.block_entities;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMaps;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import moze_intel.projecte.utils.PECombinedItemStacks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class DMFurnaceBlockEntity extends EmcBlockEntity implements MenuProvider, RecipeCraftingHolder {

	private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);

	public static final ICapabilityProvider<DMFurnaceBlockEntity, @Nullable Direction, ResourceHandler<ItemResource>> INVENTORY_PROVIDER = (furnace, side) -> {
		ResourceHandler<ItemResource> handler;
		if (side == null) {
			handler = furnace.joined;
		} else if (side == Direction.UP) {
			handler = furnace.automationInput;
		} else if (side == Direction.DOWN) {
			handler = furnace.automationOutput;
		} else {
			handler = furnace.automationSides;
		}
		return handler;
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
				if (!ItemStack.isSameItemSameComponents(oldInput, input)) {
					//Reset the cooking progress
					RecipeResult recipeResult = getSmeltingRecipe(input);
					cookingTotalTime = getTotalCookTime(recipeResult);
					cookingProgress = 0;
					oldInput = input.copy();
				}
			}
		}
	};
	private final CompactableStackHandler outputInventory = new CompactableStackHandler(getInvSize());
	private final StackHandler fuelInv = new StackHandler(1);

	private final ResourceHandler<ItemResource> joined;
	private final WrappedItemHandler automationInput;
	private final WrappedItemHandler automationOutput;
	private final ResourceHandler<ItemResource> automationSides;

	protected final int ticksBeforeSmelt;
	private final int efficiencyBonus;
	private final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Reference2IntOpenHashMap<>();
	private final RecipeManager.CachedCheck<SingleRecipeInput, SmeltingRecipe> quickCheck;

	@Nullable
	private BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction> pullTarget;
	@Nullable
	private BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction> pushTarget;

	public int litTime;
	public int litDuration;
	public int cookingProgress;
	public int cookingTotalTime;

	public DMFurnaceBlockEntity(BlockPos pos, BlockState state) {
		this(PEBlockEntityTypes.DARK_MATTER_FURNACE, pos, state, SharedConstants.TICKS_PER_SECOND / 2, 3);
	}

	protected DMFurnaceBlockEntity(BlockEntityTypeRegistryObject<? extends DMFurnaceBlockEntity> type, BlockPos pos, BlockState state, int ticksBeforeSmelt, int efficiencyBonus) {
		super(type, pos, state, 64);
		this.ticksBeforeSmelt = ticksBeforeSmelt;
		this.efficiencyBonus = efficiencyBonus;
		this.quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);

		this.automationInput = new WrappedItemHandler(inputInventory, WrappedItemHandler.WriteMode.IN) {
			@Override
			public int insert(int slot, ItemResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
                ItemStack stack = resource.toStack();
				return hasSmeltingResult(stack) ? super.insert(slot, resource, amount, transaction) : 0;
			}
		};
		this.automationOutput = new WrappedItemHandler(outputInventory, WrappedItemHandler.WriteMode.OUT);
		WrappedItemHandler automationFuel = new WrappedItemHandler(fuelInv, WrappedItemHandler.WriteMode.IN) {
			@Override
			public int insert(int slot, ItemResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
                ItemStack stack = resource.toStack();
				return SlotPredicates.FURNACE_FUEL.test(stack) ? super.insert(slot, resource, amount, transaction) : 0;
			}
		};
		this.automationSides = new PECombinedItemStacks(automationFuel, automationOutput);
		this.joined = new PECombinedItemStacks(automationInput, automationFuel, automationOutput);
	}

	@Override
	public void setLevel(@NotNull Level level) {
		super.setLevel(level);
		if (level instanceof ServerLevel serverLevel) {
			pullTarget = BlockCapabilityCache.create(Capabilities.Item.BLOCK, serverLevel, worldPosition.above(), Direction.DOWN);
			pushTarget = BlockCapabilityCache.create(Capabilities.Item.BLOCK, serverLevel, worldPosition.below(), Direction.UP);
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
		if (cookingTotalTime == 0) {
			return 0;
		}
		//Adjust by one so that it can look like it is actually reaching the end of the bar
		int progress = isLit() && canSmelt(getSmeltingRecipe(getItemToSmelt())) ? cookingProgress + 1 : cookingProgress;
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

	public ResourceHandler<ItemResource> getFuel() {
		return fuelInv;
	}

	private ItemStack getItemToSmelt() {
		return inputInventory.getStackInSlot(0);
	}

	private ItemStack getFuelItem() {
		return fuelInv.getStackInSlot(0);
	}

	public ResourceHandler<ItemResource> getInput() {
		return inputInventory;
	}

	public ResourceHandler<ItemResource> getOutput() {
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
		furnace.pullFromInventories(level, pos);

		RecipeResult recipeResult = furnace.getSmeltingRecipe(level, furnace.getItemToSmelt());
		boolean canSmelt = furnace.canSmelt(recipeResult);
		ItemStack fuelItem = furnace.getFuelItem();
		if (canSmelt) {
			IItemEmcHolder emcHolder = fuelItem.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
			if (emcHolder != null) {
				long simulatedExtraction = emcHolder.extractEmc(fuelItem, EMC_CONSUMPTION, EmcAction.SIMULATE);
				if (simulatedExtraction == EMC_CONSUMPTION) {
					furnace.forceInsertEmc(emcHolder.extractEmc(fuelItem, simulatedExtraction, EmcAction.EXECUTE), EmcAction.EXECUTE);
				}
				furnace.markDirty(level, pos, false);
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
						ItemStackTemplate remainder = copy.getCraftingRemainder();
						furnace.fuelInv.setStackInSlot(0, remainder != null ? remainder.create() : ItemStack.EMPTY);
					}
					furnace.markDirty(level, pos, false);
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
			furnace.markDirty(level, pos, true);
		}
		furnace.pushToInventories(level, pos);
		if (lastLitTime != furnace.litTime || lastCookingProgress != furnace.cookingProgress) {
			furnace.markDirty(level, pos, false);
		}
		furnace.updateComparators(level, pos);
	}

	public boolean isLit() {
		return litTime > 0;
	}

	private static boolean isHopper(@NotNull Level level, @NotNull BlockPos position) {
		//We let hoppers go at their normal rate
		return WorldHelper.getBlockEntity(level, position) instanceof Hopper;
	}

	private void pullFromInventories(@NotNull Level level, @NotNull BlockPos pos) {
		if (pullTarget == null || isHopper(level, pos.above())) {
			return;
		}
		ResourceHandler<ItemResource> resourceHandler = pullTarget.getCapability();
		ResourceHandler<ItemResource> handler = resourceHandler == null ? null : /*ResourceHandler<ItemResource>.of*/(resourceHandler);
		if (handler != null) {
			for (int i = 0, slots = PEItemStacksHandler.getSlotCount(handler); i < slots; i++) {
				ItemStack extractTest = PEItemStacksHandler.extractItem(handler, i, Integer.MAX_VALUE, true);
				if (!extractTest.isEmpty()) {
					ResourceHandler<ItemResource> targetInv = SlotPredicates.FURNACE_FUEL.test(extractTest) ? fuelInv : inputInventory;
					transferItem(targetInv, i, extractTest, handler);
				}
			}
		}
	}

	private void pushToInventories(@NotNull Level level, @NotNull BlockPos pos) {
		if (pushTarget == null || outputInventory.isEmpty() || isHopper(level, pos.below())) {
			return;
		}
		ResourceHandler<ItemResource> pushResourceHandler = pushTarget.getCapability();
		ResourceHandler<ItemResource> targetInv = pushResourceHandler == null ? null : /*ResourceHandler<ItemResource>.of*/(pushResourceHandler);
		if (targetInv != null) {
			for (int i = 0, slots = outputInventory.getSlots(); i < slots; i++) {
				ItemStack extractTest = outputInventory.extractItem(i, Integer.MAX_VALUE, true);
				if (!extractTest.isEmpty()) {
					transferItem(targetInv, i, extractTest, outputInventory);
				}
			}
		}
	}

	private void transferItem(ResourceHandler<ItemResource> targetInv, int i, ItemStack extractTest, ResourceHandler<ItemResource> outputInventory) {
		ItemStack remainderTest = PEItemStacksHandler.insertStackedRemainder(targetInv, extractTest, true);
		int successfullyTransferred = extractTest.getCount() - remainderTest.getCount();
		if (successfullyTransferred > 0) {
			ItemStack toInsert = PEItemStacksHandler.extractItem(outputInventory, i, successfullyTransferred, false);
			ItemStack result = PEItemStacksHandler.insertStackedRemainder(targetInv, toInsert, false);
			assert result.isEmpty();
		}
	}

	private RecipeResult getSmeltingRecipe(ItemStack input) {
		return getSmeltingRecipe(level, input);
	}

	private RecipeResult getSmeltingRecipe(@Nullable Level level, ItemStack input) {
		if (!(level instanceof ServerLevel serverLevel) || input.isEmpty()) {
			return RecipeResult.EMPTY;
		}
		//Note: We copy the input and fuel so that if anyone attempts to mutate the input from assemble then there is no side effects that occur
		SingleRecipeInput recipeInput = new SingleRecipeInput(input.copyWithCount(1));
		Optional<RecipeHolder<SmeltingRecipe>> optionalRecipe = quickCheck.getRecipeFor(recipeInput, serverLevel);
		if (optionalRecipe.isPresent()) {
			RecipeHolder<SmeltingRecipe> recipeHolder = optionalRecipe.get();
			return new RecipeResult(recipeHolder, recipeHolder.value().assemble(recipeInput));
		}
		return RecipeResult.EMPTY;
	}

	public boolean hasSmeltingResult(ItemStack input) {
		return getSmeltingRecipe(input).hasResult();
	}

	private void smeltItem(@NotNull Level level, @NotNull RecipeResult recipeResult) {
		ItemStack toSmelt = getItemToSmelt();
		ItemStack smeltResult = recipeResult.scaledResult(level.getRandom(), getDoubleChance(toSmelt));
		if (!smeltResult.isEmpty()) {//Double-check the result isn't somehow empty
			PEItemStacksHandler.insertStackedRemainder(outputInventory, smeltResult, false);

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
		} else if (!ItemStack.isSameItemSameComponents(smeltResult, currentSmelted)) {
			return false;
		}
		int result = currentSmelted.getCount() + smeltResult.getCount();
		return result <= currentSmelted.getMaxStackSize();
	}

	private int getItemBurnTime(ItemStack stack) {
		if (level == null) {
			return 0;
		}
		if (!(level instanceof ServerLevel serverLevel)) {
			return 0;
		}
		var lootParams = new net.minecraft.world.level.storage.loot.LootParams.Builder(serverLevel)
				.create(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.EMPTY);
		var lootContext = new net.minecraft.world.level.storage.loot.LootContext.Builder(lootParams).create(java.util.Optional.empty());
		int burn = net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.getFromItem(
				stack, net.minecraft.core.component.DataComponents.COOKING_FUEL, net.minecraft.world.item.component.CookingFuel::burnTime, lootContext, 0);
		return burn * ticksBeforeSmelt / AbstractFurnaceBlockEntity.BURN_TIME_STANDARD * efficiencyBonus;
	}

	private int getTotalCookTime(RecipeResult recipeResult) {
		if (recipeResult.recipeHolder() == null) {
			return ticksBeforeSmelt;
		}
		int cookingTime = recipeResult.recipeHolder().value().cookingTime();
		return Mth.ceil(ticksBeforeSmelt * cookingTime / (float) AbstractFurnaceBlockEntity.BURN_TIME_STANDARD);
	}

	public float getLitProgress() {
		int litDuration = this.litDuration;
		if (litDuration == 0) {
			litDuration = ticksBeforeSmelt;
		}
		return Mth.clamp(litTime / (float) litDuration, 0, 1);
	}

	@Override
	public void loadAdditional(@NotNull ValueInput input) {
		super.loadAdditional(input);
		litTime = input.getIntOr("burn_time", 0);
		cookingProgress = input.getIntOr("cook_time", 0);
		cookingTotalTime = input.getIntOr("cook_time_total", 0);
		fuelInv.deserialize(input.childOrEmpty("fuel"));
		inputInventory.deserialize(input.childOrEmpty("input"));
		outputInventory.deserialize(input.childOrEmpty("output"));
		litDuration = getItemBurnTime(getFuelItem());
		//[VanillaCopy] AbstractFurnaceBlockEntity
		recipesUsed.clear();
		recipesUsed.putAll(input.read("RecipesUsed", RECIPES_USED_CODEC).orElse(Map.of()));
	}

	@Override
	protected void saveAdditional(@NotNull ValueOutput output) {
		super.saveAdditional(output);
		output.putInt("burn_time", litTime);
		output.putInt("cook_time", cookingProgress);
		output.putInt("cook_time_total", this.cookingTotalTime);
		inputInventory.serialize(output.child("input"));
		outputInventory.serialize(output.child("output"));
		fuelInv.serialize(output.child("fuel"));
		//[VanillaCopy] AbstractFurnaceBlockEntity
		output.store("RecipesUsed", RECIPES_USED_CODEC, this.recipesUsed);
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
		List<RecipeHolder<?>> recipes = getRecipesToAwardAndPopExperience(player.level(), player.position());
		player.awardRecipes(recipes);

		for (RecipeHolder<?> recipeholder : recipes) {
			//Note: We don't have a good way to access the list of input items that were present, so we just skip it
			// and only support triggering recipe triggers that are based on the recipe id
			player.triggerRecipeCrafted(recipeholder, Collections.emptyList());
		}

		this.recipesUsed.clear();
	}

	//[VanillaCopy] AbstractFurnaceBlockEntity
	public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 popVec) {
		List<RecipeHolder<?>> list = new ArrayList<>();
		for (Iterator<Reference2IntMap.Entry<ResourceKey<Recipe<?>>>> iterator = Reference2IntMaps.fastIterator(recipesUsed); iterator.hasNext(); ) {
			Reference2IntMap.Entry<ResourceKey<Recipe<?>>> entry = iterator.next();
			level.recipeAccess().byKey(entry.getKey()).ifPresent(recipeHolder -> {
				list.add(recipeHolder);
				if (recipeHolder.value() instanceof SmeltingRecipe recipe) {
					createExperience(level, popVec, entry.getIntValue(), recipe.experience());
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

		public boolean hasResult() {
			return !result.isEmpty();
		}
	}
}
