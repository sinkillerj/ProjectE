package moze_intel.projecte.gameObjs.items;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongComparators;
import it.unimi.dsi.fastutil.longs.LongList;
import java.util.List;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.NonNullLazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiviningRod extends ItemPE implements IItemMode {

	private final ILangEntry[] modes;
	private final int maxModes;

	public DiviningRod(Properties props, ILangEntry... modeDesc) {
		super(props);
		modes = modeDesc;
		maxModes = modes.length;
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		if (player == null) {
			return InteractionResult.FAIL;
		}
		Level level = ctx.getLevel();
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		LongList emcValues = new LongArrayList();
		long totalEmc = 0;
		int numBlocks = 0;
		int depth = getDepthFromMode(ctx.getItemInHand());
		//Lazily retrieve the values for the furnace recipes
		NonNullLazy<List<SmeltingRecipe>> furnaceRecipes = NonNullLazy.of(() -> level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING));
		for (BlockPos digPos : WorldHelper.getPositionsFromBox(WorldHelper.getDeepBox(ctx.getClickedPos(), ctx.getClickedFace(), depth))) {
			if (level.isEmptyBlock(digPos)) {
				continue;
			}
			BlockState state = level.getBlockState(digPos);
			List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level, digPos, WorldHelper.getBlockEntity(level, digPos), player, ctx.getItemInHand());
			if (drops.isEmpty()) {
				continue;
			}
			ItemStack blockStack = drops.get(0);
			long blockEmc = EMCHelper.getEmcValue(blockStack);
			if (blockEmc == 0) {
				for (SmeltingRecipe furnaceRecipe : furnaceRecipes.get()) {
					if (furnaceRecipe.getIngredients().get(0).test(blockStack)) {
						long currentValue = EMCHelper.getEmcValue(furnaceRecipe.getResultItem());
						if (currentValue != 0) {
							if (!emcValues.contains(currentValue)) {
								emcValues.add(currentValue);
							}
							totalEmc += currentValue;
							break;
						}
					}
				}
			} else {
				if (!emcValues.contains(blockEmc)) {
					emcValues.add(blockEmc);
				}
				totalEmc += blockEmc;
			}
			numBlocks++;
		}

		if (numBlocks == 0) {
			return InteractionResult.FAIL;
		}
		player.sendSystemMessage(PELang.DIVINING_AVG_EMC.translate(numBlocks, totalEmc / numBlocks));
		if (this == PEItems.MEDIUM_DIVINING_ROD.get() || this == PEItems.HIGH_DIVINING_ROD.get()) {
			long[] maxValues = new long[3];
			for (int i = 0; i < 3; i++) {
				maxValues[i] = 1;
			}
			emcValues.sort(LongComparators.OPPOSITE_COMPARATOR);
			int num = Math.min(emcValues.size(), 3);
			for (int i = 0; i < num; i++) {
				maxValues[i] = emcValues.getLong(i);
			}
			player.sendSystemMessage(PELang.DIVINING_MAX_EMC.translate(maxValues[0]));
			if (this == PEItems.HIGH_DIVINING_ROD.get()) {
				player.sendSystemMessage(PELang.DIVINING_SECOND_MAX.translate(maxValues[1]));
				player.sendSystemMessage(PELang.DIVINING_THIRD_MAX.translate(maxValues[2]));
			}
		}
		return InteractionResult.CONSUME;
	}

	private int getDepthFromMode(ItemStack stack) {
		byte mode = getMode(stack);
		if (mode < 0 || mode >= maxModes) {
			//No range something went wrong
			return 0;
		} else if (mode == 0) {
			return 3;
		} else if (mode == 1) {
			return 16;
		}//mode == 2
		return 64;
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return modes;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(getToolTip(stack));
	}
}