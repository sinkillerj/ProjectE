package moze_intel.projecte.utils;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class WorldTransmutations
{
	private static final List<Entry> ENTRIES = new ArrayList<>();

	static
	{
		registerDefault(Blocks.STONE, Blocks.COBBLESTONE, Blocks.GRASS);
		registerDefault(Blocks.COBBLESTONE, Blocks.STONE, Blocks.GRASS);
		registerDefault(Blocks.GRASS, Blocks.SAND, Blocks.COBBLESTONE);
		registerDefault(Blocks.DIRT, Blocks.SAND, Blocks.COBBLESTONE);
		registerDefault(Blocks.SAND, Blocks.GRASS, Blocks.COBBLESTONE);
		registerDefault(Blocks.GRAVEL, Blocks.SANDSTONE, null);
		registerDefault(Blocks.SANDSTONE, Blocks.GRAVEL, null);
		registerDefault(Blocks.WATER, Blocks.ICE, null);
		registerDefault(Blocks.ICE, Blocks.WATER, null);
		registerDefault(Blocks.LAVA, Blocks.OBSIDIAN, null);
		registerDefault(Blocks.OBSIDIAN, Blocks.LAVA, null);
		registerDefault(Blocks.MELON, Blocks.PUMPKIN, null);
		registerDefault(Blocks.PUMPKIN, Blocks.MELON, null);
		registerDefault(Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE);
		registerDefault(Blocks.DIORITE, Blocks.ANDESITE, Blocks.GRANITE);
		registerDefault(Blocks.ANDESITE, Blocks.GRANITE, Blocks.DIORITE);

		Block[] logs = { Blocks.OAK_LOG, Blocks.BIRCH_LOG, Blocks.SPRUCE_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG };
		registerConsecutivePairs(logs);

		Block[] leaves = { Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES };
		registerConsecutivePairs(leaves);

		Block[] saplings = { Blocks.OAK_SAPLING, Blocks.BIRCH_SAPLING, Blocks.SPRUCE_SAPLING, Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING, Blocks.DARK_OAK_SAPLING };
		registerConsecutivePairs(saplings);

		Block[] wools = {
				Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL, Blocks.MAGENTA_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.YELLOW_WOOL,
				Blocks.LIME_WOOL, Blocks.PINK_WOOL, Blocks.GRAY_WOOL, Blocks.LIGHT_GRAY_WOOL, Blocks.CYAN_WOOL, 
				Blocks.PURPLE_WOOL, Blocks.BLUE_WOOL, Blocks.BROWN_WOOL, Blocks.GREEN_WOOL, Blocks.RED_WOOL, Blocks.BLACK_WOOL
		};
		registerConsecutivePairs(wools);

		Block[] terracottas = {
				Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA,
				Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA,
				Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA
		};
		registerConsecutivePairs(terracottas);

		Block[] carpets = {
				Blocks.WHITE_CARPET, Blocks.ORANGE_CARPET, Blocks.MAGENTA_CARPET, Blocks.LIGHT_BLUE_CARPET, Blocks.YELLOW_CARPET,
				Blocks.LIME_CARPET, Blocks.PINK_CARPET, Blocks.GRAY_CARPET, Blocks.LIGHT_GRAY_CARPET, Blocks.CYAN_CARPET,
				Blocks.PURPLE_CARPET, Blocks.BLUE_CARPET, Blocks.BROWN_CARPET, Blocks.GREEN_CARPET, Blocks.RED_CARPET, Blocks.BLACK_CARPET
		};
		registerConsecutivePairs(carpets);
	}

	public static IBlockState getWorldTransmutation(World world, BlockPos pos, boolean isSneaking)
	{
		return getWorldTransmutation(world.getBlockState(pos), isSneaking);
	}

	public static IBlockState getWorldTransmutation(IBlockState current, boolean isSneaking)
	{
		for (Entry e : ENTRIES)
		{
			if (e.input == current)
			{
				Pair<IBlockState, IBlockState> result = e.outputs;
				return isSneaking ? (result.getRight() == null ? result.getLeft() : result.getRight()) : result.getLeft();
			}
		}

		return null;
	}

	public static List<Entry> getWorldTransmutations()
	{
		return ENTRIES;
	}

	public static void register(IBlockState from, IBlockState result, IBlockState altResult)
	{
		ENTRIES.add(new Entry(from, ImmutablePair.of(result, altResult)));
	}

	private static void registerDefault(Block from, Block result, Block altResult)
	{
		register(from.getDefaultState(), result.getDefaultState(), altResult == null ? null : altResult.getDefaultState());
	}

	private static void registerConsecutivePairs(Block[] blocks)
	{
		for (int i = 0; i < blocks.length; i++)
		{
			Block prev = i == 0 ? blocks[blocks.length - 1] : blocks[i - 1];
			Block cur = blocks[i];
			Block next = i == blocks.length - 1 ? blocks[0] : blocks[i + 1];
			registerDefault(cur, next, prev);
		}
	}

	public static class Entry /*implements IRecipeWrapper*/
	{
		public final IBlockState input;
		public final Pair<IBlockState, IBlockState> outputs;

		public Entry(IBlockState from, Pair<IBlockState, IBlockState> results)
		{
			this.input = from;
			this.outputs = results;
		}
		/* todo 1.13
		@Override
		public void getIngredients(IIngredients ingredients) {


			if((this.input.getProperties().containsKey(BlockHorizontal.FACING) && this.input.get(BlockHorizontal.FACING) != EnumFacing.NORTH) ||
                    this.input.getProperties().containsKey(BlockLog.LOG_AXIS) && this.input.get(BlockLog.LOG_AXIS) != BlockLog.EnumAxis.NONE)
				return;

			List<ItemStack> outputList = new ArrayList<>();

			if(this.input.getBlock() instanceof BlockStaticLiquid || this.outputs.getLeft().getBlock() instanceof BlockStaticLiquid){
				if(this.input.getBlock() == Blocks.WATER)
					ingredients.setInput(FluidStack.class, new FluidStack(FluidRegistry.WATER, 1000));
				else if (this.input.getBlock() == Blocks.LAVA)
					ingredients.setInput(FluidStack.class, new FluidStack(FluidRegistry.LAVA, 1000));
				else
					ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock()));

				if(this.outputs.getLeft().getBlock() == Blocks.WATER)
					ingredients.setOutput(FluidStack.class, new FluidStack(FluidRegistry.WATER, 1000));
				else if (this.outputs.getLeft().getBlock() == Blocks.LAVA)
					ingredients.setOutput(FluidStack.class, new FluidStack(FluidRegistry.LAVA, 1000));
				else
					ingredients.setOutput(ItemStack.class, new ItemStack(this.outputs.getLeft().getBlock()));
			}


			if(this.input.getProperties().containsKey(BlockColored.COLOR)){
				ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.get(BlockColored.COLOR).getMetadata()));
				outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().get(BlockColored.COLOR).getMetadata()));

				if(this.outputs.getRight() != null)
					outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().get(BlockColored.COLOR).getMetadata()));
			}
			else if(this.input.getProperties().containsKey(BlockStone.VARIANT)){


					ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.get(BlockStone.VARIANT).getMetadata()));

					if(this.outputs.getLeft().getBlock() == Blocks.COBBLESTONE || this.outputs.getLeft().getBlock() == Blocks.GRASS)
						outputList.add(new ItemStack(this.outputs.getLeft().getBlock()));
					else
						outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().get(BlockStone.VARIANT).getMetadata()));

					if(this.outputs.getRight() != null){
						if(this.outputs.getRight().getBlock() == Blocks.COBBLESTONE || this.outputs.getRight().getBlock() == Blocks.GRASS)
							outputList.add(new ItemStack(this.outputs.getRight().getBlock()));
						else
							outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().get(BlockStone.VARIANT).getMetadata()));
					}



			}
			else if(this.input.getProperties().containsKey(BlockOldLog.VARIANT)){
				ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.get(BlockOldLog.VARIANT).getMetadata()));

				if(this.outputs.getLeft().getProperties().containsKey(BlockOldLog.VARIANT))
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().get(BlockOldLog.VARIANT).getMetadata()));
				else
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().get(BlockNewLog.VARIANT).getMetadata() - 4));

				if(this.outputs.getRight() != null){
					if(this.outputs.getRight().getProperties().containsKey(BlockOldLog.VARIANT))
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().get(BlockOldLog.VARIANT).getMetadata()));
					else
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().get(BlockNewLog.VARIANT).getMetadata() - 4));
				}

			}
			else if(this.input.getProperties().containsKey(BlockNewLog.VARIANT)){
				ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.get(BlockNewLog.VARIANT).getMetadata() - 4));

				if(this.outputs.getLeft().getProperties().containsKey(BlockNewLog.VARIANT))
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().get(BlockNewLog.VARIANT).getMetadata() - 4));
				else
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().get(BlockOldLog.VARIANT).getMetadata()));

				if(this.outputs.getRight() != null){
					if(this.outputs.getRight().getProperties().containsKey(BlockNewLog.VARIANT))
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().get(BlockNewLog.VARIANT).getMetadata() - 4));
					else
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().get(BlockOldLog.VARIANT).getMetadata()));
				}

			}
			else if(this.input.getProperties().containsKey(BlockOldLeaf.VARIANT)){
				ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.get(BlockOldLeaf.VARIANT).getMetadata()));

				if(this.outputs.getLeft().getProperties().containsKey(BlockOldLeaf.VARIANT))
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().get(BlockOldLeaf.VARIANT).getMetadata()));
				else
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().get(BlockNewLeaf.VARIANT).getMetadata()));
				if(this.outputs.getRight() != null)
					if(this.outputs.getRight().getProperties().containsKey(BlockOldLeaf.VARIANT))
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().get(BlockOldLeaf.VARIANT).getMetadata()));
					else
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().get(BlockNewLeaf.VARIANT).getMetadata()));
			}
			else if(this.input.getProperties().containsKey(BlockNewLeaf.VARIANT)){
				ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.get(BlockNewLeaf.VARIANT).getMetadata()));

				if(this.outputs.getLeft().getProperties().containsKey(BlockNewLeaf.VARIANT))
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().get(BlockNewLeaf.VARIANT).getMetadata()));
				else
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().get(BlockOldLeaf.VARIANT).getMetadata()));
				if(this.outputs.getRight() != null){
					if(this.outputs.getRight().getProperties().containsKey(BlockNewLeaf.VARIANT))
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().get(BlockNewLeaf.VARIANT).getMetadata()));
					else
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().get(BlockNewLeaf.VARIANT).getMetadata()));
				}

			}
			else if(this.input.getProperties().containsKey(BlockSapling.TYPE)){
				ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.get(BlockSapling.TYPE).getMetadata()));
				outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().get(BlockSapling.TYPE).getMetadata()));

				if(this.outputs.getRight() != null)
					outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().get(BlockSapling.TYPE).getMetadata()));
			}
			else{
				ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock()));
				outputList.add(new ItemStack(this.outputs.getLeft().getBlock()));

				if(this.outputs.getRight() != null)
					outputList.add(new ItemStack(this.outputs.getRight().getBlock()));
			}

			ingredients.setOutputs(ItemStack.class, outputList);
		}

		@Override
		public java.util.List<String> getTooltipStrings(int mouseX, int mouseY) {

			if(mouseX > 67 && mouseX < 107)
				if(mouseY > 18 && mouseY < 38)
					return Collections.singletonList("Click in world, shift click for second output");

			return Collections.emptyList();
		}*/
	}
}
