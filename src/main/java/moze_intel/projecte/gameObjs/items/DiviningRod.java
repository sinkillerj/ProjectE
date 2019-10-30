package moze_intel.projecte.gameObjs.items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PrimitiveIterator;
import javax.annotation.Nonnull;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DiviningRod extends ItemPE implements IItemMode {

	// Modes should be in the format pe.diving_rod.mode.range.depth
	private final String[] modes;

	public DiviningRod(Properties props, String[] modeDesc) {
		super(props);
		modes = modeDesc;
		addItemCapability(new ModeChangerItemCapabilityWrapper());
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		World world = ctx.getWorld();
		PlayerEntity player = ctx.getPlayer();

		if (world.isRemote) {
			return ActionResultType.SUCCESS;
		}

		List<Long> emcValues = new ArrayList<>();
		long totalEmc = 0;
		int numBlocks = 0;

		int depth = getDepthFromMode(ctx.getItem());
		AxisAlignedBB box = WorldHelper.getDeepBox(ctx.getPos(), ctx.getFace(), depth);

		for (BlockPos digPos : WorldHelper.getPositionsFromBox(box)) {
			BlockState state = world.getBlockState(digPos);

			if (world.isAirBlock(digPos)) {
				continue;
			}

			List<ItemStack> drops = Block.getDrops(state, (ServerWorld) world, digPos,
					world.getTileEntity(digPos), player, ctx.getItem());

			if (drops.isEmpty()) {
				continue;
			}

			ItemStack blockStack = drops.get(0);
			long blockEmc = EMCHelper.getEmcValue(blockStack);

			if (blockEmc == 0) {
				PrimitiveIterator.OfLong iter = world.getRecipeManager().getRecipes().stream()
						.filter(r -> r instanceof FurnaceRecipe && r.getIngredients().get(0).test(blockStack))
						.mapToLong(r -> EMCHelper.getEmcValue(r.getRecipeOutput()))
						.iterator();

				while (iter.hasNext()) {
					long currentValue = iter.nextLong();
					if (currentValue != 0) {
						if (!emcValues.contains(currentValue)) {
							emcValues.add(currentValue);
						}

						totalEmc += currentValue;
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

		long[] maxValues = new long[3];

		for (int i = 0; i < 3; i++) {
			maxValues[i] = 1;
		}

		emcValues.sort(Comparator.reverseOrder());

		int num = Math.min(emcValues.size(), 3);

		for (int i = 0; i < num; i++) {
			maxValues[i] = emcValues.get(i);
		}

		player.sendMessage(new TranslationTextComponent("pe.divining.avgemc", numBlocks, (totalEmc / numBlocks)));

		if (this == ObjHandler.dRod2 || this == ObjHandler.dRod3) {
			player.sendMessage(new TranslationTextComponent("pe.divining.maxemc", maxValues[0]));
		}

		if (this == ObjHandler.dRod3) {
			player.sendMessage(new TranslationTextComponent("pe.divining.secondmax", maxValues[1]));
			player.sendMessage(new TranslationTextComponent("pe.divining.thirdmax", maxValues[2]));
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