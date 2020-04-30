package moze_intel.projecte.gameObjs.items;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.NonNullLazy;

public class DiviningRod extends ItemPE implements IItemMode {

	// Modes should be in the format pe.diving_rod.mode.range.depth
	private final String[] modes;

	public DiviningRod(Properties props, String[] modeDesc) {
		super(props);
		modes = modeDesc;
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		World world = ctx.getWorld();
		if (world.isRemote) {
			return ActionResultType.SUCCESS;
		}
		PlayerEntity player = ctx.getPlayer();
		LongList emcValues = new LongArrayList();
		long totalEmc = 0;
		int numBlocks = 0;
		int depth = getDepthFromMode(ctx.getItem());
		//Lazily retrieve the values for the furnace recipes
		NonNullLazy<Collection<IRecipe<IInventory>>> furnaceRecipes = NonNullLazy.of(() -> world.getRecipeManager().getRecipes(IRecipeType.SMELTING).values());
		for (BlockPos digPos : WorldHelper.getPositionsFromBox(WorldHelper.getDeepBox(ctx.getPos(), ctx.getFace(), depth))) {
			if (world.isAirBlock(digPos)) {
				continue;
			}
			BlockState state = world.getBlockState(digPos);
			List<ItemStack> drops = Block.getDrops(state, (ServerWorld) world, digPos, world.getTileEntity(digPos), player, ctx.getItem());
			if (drops.isEmpty()) {
				continue;
			}
			ItemStack blockStack = drops.get(0);
			long blockEmc = EMCHelper.getEmcValue(blockStack);
			if (blockEmc == 0) {
				for (IRecipe<IInventory> furnaceRecipe : furnaceRecipes.get()) {
					if (furnaceRecipe.getIngredients().get(0).test(blockStack)) {
						long currentValue = EMCHelper.getEmcValue(furnaceRecipe.getRecipeOutput());
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
			return ActionResultType.FAIL;
		}
		player.sendMessage(new TranslationTextComponent("pe.divining.avgemc", numBlocks, totalEmc / numBlocks));
		if (this == ObjHandler.dRod2 || this == ObjHandler.dRod3) {
			long[] maxValues = new long[3];
			for (int i = 0; i < 3; i++) {
				maxValues[i] = 1;
			}
			emcValues.sort(Comparator.reverseOrder());
			int num = Math.min(emcValues.size(), 3);
			for (int i = 0; i < num; i++) {
				maxValues[i] = emcValues.getLong(i);
			}
			player.sendMessage(new TranslationTextComponent("pe.divining.maxemc", maxValues[0]));
			if (this == ObjHandler.dRod3) {
				player.sendMessage(new TranslationTextComponent("pe.divining.secondmax", maxValues[1]));
				player.sendMessage(new TranslationTextComponent("pe.divining.thirdmax", maxValues[2]));
			}
		}
		return ActionResultType.SUCCESS;
	}

	/**
	 * Gets the range from the translation keys string
	 *
	 * Format is "pe.diving_rod.mode.range.RANGE"
	 */
	private int getDepthFromMode(ItemStack stack) {
		return Integer.parseInt(getModeTranslationKey(stack).substring(25));
	}

	@Override
	public String[] getModeTranslationKeys() {
		return modes;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flags) {
		list.add(getToolTip(stack));
	}
}