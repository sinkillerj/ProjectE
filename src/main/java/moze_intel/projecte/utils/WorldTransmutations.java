package moze_intel.projecte.utils;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
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
		register(Blocks.MELON_BLOCK.getDefaultState(), Blocks.PUMPKIN.getDefaultState().withProperty(BlockPumpkin.FACING, EnumFacing.SOUTH), null);

		for (EnumFacing e : EnumFacing.HORIZONTALS)
		{
			register(Blocks.PUMPKIN.getDefaultState().withProperty(BlockPumpkin.FACING, e), Blocks.MELON_BLOCK.getDefaultState(), null);
		}

		for (IBlockState s : Blocks.LOG.getBlockState().getValidStates())
		{
			if (s.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.OAK)
			{
				// Oak must loop backward to dark oak
				register(s, s.cycleProperty(BlockOldLog.VARIANT),
						Blocks.LOG2.getDefaultState()
								.withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK)
								.withProperty(BlockNewLog.LOG_AXIS, s.getValue(BlockOldLog.LOG_AXIS)));
			} else if (s.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.JUNGLE)
			{
				// Jungle must loop forward to acacia
				register(s,
						Blocks.LOG2.getDefaultState()
								.withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA)
								.withProperty(BlockNewLog.LOG_AXIS, s.getValue(BlockOldLog.LOG_AXIS)),
						cyclePropertyBackwards(s, BlockOldLog.VARIANT));
			} else
			{
				register(s, s.cycleProperty(BlockOldLog.VARIANT), cyclePropertyBackwards(s, BlockOldLog.VARIANT));
			}
		}

		for (IBlockState s : Blocks.LEAVES.getBlockState().getValidStates())
		{
			if (s.getValue(BlockOldLeaf.VARIANT) == BlockPlanks.EnumType.OAK)
			{
				// Oak must loop backward to dark oak
				register(s, s.cycleProperty(BlockOldLeaf.VARIANT),
						Blocks.LEAVES2.getDefaultState()
								.withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK)
								.withProperty(BlockNewLeaf.CHECK_DECAY, s.getValue(BlockOldLeaf.CHECK_DECAY))
								.withProperty(BlockNewLeaf.DECAYABLE, s.getValue(BlockOldLeaf.DECAYABLE)));
			} else if (s.getValue(BlockOldLeaf.VARIANT) == BlockPlanks.EnumType.JUNGLE)
			{
				// Jungle must loop forward to acacia
				register(s,
						Blocks.LEAVES2.getDefaultState()
								.withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA)
								.withProperty(BlockNewLeaf.CHECK_DECAY, s.getValue(BlockNewLeaf.CHECK_DECAY))
								.withProperty(BlockNewLeaf.DECAYABLE, s.getValue(BlockOldLeaf.DECAYABLE)),
						cyclePropertyBackwards(s, BlockOldLeaf.VARIANT));
			} else
			{
				register(s, s.cycleProperty(BlockOldLeaf.VARIANT), cyclePropertyBackwards(s, BlockOldLeaf.VARIANT));
			}
		}

		for (IBlockState s : Blocks.LOG2.getBlockState().getValidStates())
		{
			if (s.getValue(BlockNewLog.VARIANT) == BlockPlanks.EnumType.ACACIA)
			{
				// Acacia must loop backward to jungle
				register(s, s.cycleProperty(BlockNewLog.VARIANT),
						Blocks.LOG.getDefaultState()
								.withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE)
								.withProperty(BlockOldLog.LOG_AXIS, s.getValue(BlockNewLog.LOG_AXIS)));
			} else if (s.getValue(BlockNewLog.VARIANT) == BlockPlanks.EnumType.DARK_OAK)
			{
				// Dark oak must loop forward to oak
				register(s,
						Blocks.LOG.getDefaultState()
								.withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK)
								.withProperty(BlockOldLog.LOG_AXIS, s.getValue(BlockNewLog.LOG_AXIS)),
						cyclePropertyBackwards(s, BlockNewLog.VARIANT));
			} else
			{
				register(s, s.cycleProperty(BlockNewLog.VARIANT), cyclePropertyBackwards(s, BlockNewLog.VARIANT));
			}
		}

		for (IBlockState s : Blocks.LEAVES2.getBlockState().getValidStates())
		{
			if (s.getValue(BlockNewLeaf.VARIANT) == BlockPlanks.EnumType.ACACIA)
			{
				// Acacia must loop backward to jungle
				register(s, s.cycleProperty(BlockNewLeaf.VARIANT),
						Blocks.LEAVES.getDefaultState()
								.withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)
								.withProperty(BlockOldLeaf.CHECK_DECAY, s.getValue(BlockNewLeaf.CHECK_DECAY))
								.withProperty(BlockOldLeaf.DECAYABLE, s.getValue(BlockNewLeaf.DECAYABLE)));
			} else if (s.getValue(BlockNewLeaf.VARIANT) == BlockPlanks.EnumType.DARK_OAK)
			{
				// Dark oak must loop forward to oak
				register(s,
						Blocks.LEAVES.getDefaultState()
								.withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK)
								.withProperty(BlockOldLeaf.CHECK_DECAY, s.getValue(BlockNewLeaf.CHECK_DECAY))
								.withProperty(BlockOldLeaf.DECAYABLE, s.getValue(BlockNewLeaf.DECAYABLE)),
						cyclePropertyBackwards(s, BlockNewLeaf.VARIANT));
			} else
			{
				register(s, s.cycleProperty(BlockNewLeaf.VARIANT), cyclePropertyBackwards(s, BlockNewLeaf.VARIANT));
			}
		}

		for (BlockPlanks.EnumType e : BlockPlanks.EnumType.values())
		{
			IBlockState state = Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, e);
			register(state, state.cycleProperty(BlockSapling.TYPE), cyclePropertyBackwards(state, BlockSapling.TYPE));
		}

		for (EnumDyeColor e : EnumDyeColor.values())
		{
			IBlockState state = Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, e);
			register(state, state.cycleProperty(BlockColored.COLOR), cyclePropertyBackwards(state, BlockColored.COLOR));

			state = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, e);
			register(state, state.cycleProperty(BlockColored.COLOR), cyclePropertyBackwards(state, BlockColored.COLOR));

			state = Blocks.CARPET.getDefaultState().withProperty(BlockCarpet.COLOR, e);
			register(state, state.cycleProperty(BlockCarpet.COLOR), cyclePropertyBackwards(state, BlockCarpet.COLOR));
		}

		IBlockState granite = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE);
		IBlockState diorite = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE);
		IBlockState andesite = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE);

		register(granite, diorite, andesite);
		register(diorite, andesite, granite);
		register(andesite, granite, diorite);
	}

	private static IBlockState cyclePropertyBackwards(IBlockState state, IProperty<?> property)
	{
		IBlockState result = state;
		for (int i = 0; i < property.getAllowedValues().size() - 1; i++)
		{
			result = result.cycleProperty(property);
		}
		return result;
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

	public static class Entry implements IRecipeWrapper
	{
		public final IBlockState input;
		public final Pair<IBlockState, IBlockState> outputs;

		public Entry(IBlockState from, Pair<IBlockState, IBlockState> results)
		{
			this.input = from;
			this.outputs = results;
		}
		//TODO: Fix logs, remove duplicate Pumpkin -> Melon (it has one for each facing)
		@Override
		public void getIngredients(IIngredients ingredients) {


			if((this.input.getProperties().containsKey(BlockHorizontal.FACING) && this.input.getValue(BlockHorizontal.FACING) != EnumFacing.NORTH) ||
                    this.input.getProperties().containsKey(BlockLog.LOG_AXIS) && this.input.getValue(BlockLog.LOG_AXIS) != BlockLog.EnumAxis.NONE)
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
				ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.getValue(BlockColored.COLOR).getMetadata()));
				outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().getValue(BlockColored.COLOR).getMetadata()));

				if(this.outputs.getRight() != null)
					outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().getValue(BlockColored.COLOR).getMetadata()));
			}
			else if(this.input.getProperties().containsKey(BlockStone.VARIANT)){


					ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.getValue(BlockStone.VARIANT).getMetadata()));

					if(this.outputs.getLeft().getBlock() == Blocks.COBBLESTONE || this.outputs.getLeft().getBlock() == Blocks.GRASS)
						outputList.add(new ItemStack(this.outputs.getLeft().getBlock()));
					else
						outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().getValue(BlockStone.VARIANT).getMetadata()));

					if(this.outputs.getRight() != null){
						if(this.outputs.getRight().getBlock() == Blocks.COBBLESTONE || this.outputs.getRight().getBlock() == Blocks.GRASS)
							outputList.add(new ItemStack(this.outputs.getRight().getBlock()));
						else
							outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().getValue(BlockStone.VARIANT).getMetadata()));
					}



			}
			else if(this.input.getProperties().containsKey(BlockOldLog.VARIANT)){
				ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.getValue(BlockOldLog.VARIANT).getMetadata()));

				if(this.outputs.getLeft().getProperties().containsKey(BlockOldLog.VARIANT))
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().getValue(BlockOldLog.VARIANT).getMetadata()));
				else
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().getValue(BlockNewLog.VARIANT).getMetadata() - 4));

				if(this.outputs.getRight() != null){
					if(this.outputs.getRight().getProperties().containsKey(BlockOldLog.VARIANT))
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().getValue(BlockOldLog.VARIANT).getMetadata()));
					else
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().getValue(BlockNewLog.VARIANT).getMetadata() - 4));
				}

			}
			else if(this.input.getProperties().containsKey(BlockNewLog.VARIANT)){
				ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.getValue(BlockNewLog.VARIANT).getMetadata() - 4));

				if(this.outputs.getLeft().getProperties().containsKey(BlockNewLog.VARIANT))
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().getValue(BlockNewLog.VARIANT).getMetadata() - 4));
				else
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().getValue(BlockOldLog.VARIANT).getMetadata()));

				if(this.outputs.getRight() != null){
					if(this.outputs.getRight().getProperties().containsKey(BlockNewLog.VARIANT))
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().getValue(BlockNewLog.VARIANT).getMetadata() - 4));
					else
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().getValue(BlockOldLog.VARIANT).getMetadata()));
				}

			}
			else if(this.input.getProperties().containsKey(BlockOldLeaf.VARIANT)){
				ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.getValue(BlockOldLeaf.VARIANT).getMetadata()));

				if(this.outputs.getLeft().getProperties().containsKey(BlockOldLeaf.VARIANT))
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().getValue(BlockOldLeaf.VARIANT).getMetadata()));
				else
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().getValue(BlockNewLeaf.VARIANT).getMetadata()));
				if(this.outputs.getRight() != null)
					if(this.outputs.getRight().getProperties().containsKey(BlockOldLeaf.VARIANT))
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().getValue(BlockOldLeaf.VARIANT).getMetadata()));
					else
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().getValue(BlockNewLeaf.VARIANT).getMetadata()));
			}
			else if(this.input.getProperties().containsKey(BlockNewLeaf.VARIANT)){
				ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.getValue(BlockNewLeaf.VARIANT).getMetadata()));

				if(this.outputs.getLeft().getProperties().containsKey(BlockNewLeaf.VARIANT))
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().getValue(BlockNewLeaf.VARIANT).getMetadata()));
				else
					outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().getValue(BlockOldLeaf.VARIANT).getMetadata()));
				if(this.outputs.getRight() != null){
					if(this.outputs.getRight().getProperties().containsKey(BlockNewLeaf.VARIANT))
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().getValue(BlockNewLeaf.VARIANT).getMetadata()));
					else
						outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().getValue(BlockNewLeaf.VARIANT).getMetadata()));
				}

			}
			else if(this.input.getProperties().containsKey(BlockSapling.TYPE)){
				ingredients.setInput(ItemStack.class, new ItemStack(this.input.getBlock(),1, this.input.getValue(BlockSapling.TYPE).getMetadata()));
				outputList.add(new ItemStack(this.outputs.getLeft().getBlock(), 1, this.outputs.getLeft().getValue(BlockSapling.TYPE).getMetadata()));

				if(this.outputs.getRight() != null)
					outputList.add(new ItemStack(this.outputs.getRight().getBlock(), 1, this.outputs.getRight().getValue(BlockSapling.TYPE).getMetadata()));
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
		}
	}
}
