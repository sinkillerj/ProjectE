package team.chisel.api.carving;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import team.chisel.api.block.ICarvable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

public class CarvingUtils {

	/**
	 * A simple way to compare two {@link ICarvingVariation} objects based on the {@link ICarvingVariation#getOrder() getOrder()} method.
	 * 
	 * @param v1
	 *            The first {@link ICarvingVariation variation}.
	 * @param v2
	 *            The second {@link ICarvingVariation variation}.
	 * @return A positive integer if the first's order is greater, a negative integer if the second's is greater, and 0 if they are equal.
	 */
	public static int compare(ICarvingVariation v1, ICarvingVariation v2) {
		return v1.getOrder() - v2.getOrder();
	}

	/**
	 * @deprecated Use {@link ICarvingVariation#getStack()}
	 */
	@Deprecated
	public static ItemStack getStack(ICarvingVariation variation) {
		return variation.getStack();
	}

	public static ICarvingRegistry chisel;

	/**
	 * @return The instance of the chisel carving registry from the chisel mod.
	 *         <p>
	 *         If chisel is not installed this will return null.
	 */
	public static ICarvingRegistry getChiselRegistry() {
		return chisel;
	}

	/**
	 * Creates a standard {@link ICarvingVariation} for the given data. Use this if you do not need any custom behavior in your own variation.
	 * 
	 * @param block
	 *            The block of the variation
	 * @param metadata
	 *            The metadata of both the block and item
	 * @param order
	 *            The sorting order.
	 * @return A standard {@link ICarvingVariation} instance.
	 */
	public static ICarvingVariation getDefaultVariationFor(IBlockState state, int order) {
		return new SimpleCarvingVariation(state, order);
	}

	/**
	 * Creates a standard {@link ICarvingGroup} for the given name. Use this if you do not need any custom behavior in your own group.
	 * 
	 * @param name
	 *            The name of the group.
	 * @return A standard {@link ICarvingGroup} instance.
	 */
	public static ICarvingGroup getDefaultGroupFor(String name) {
		return new SimpleCarvingGroup(name);
	}

	public static class SimpleCarvingVariation implements ICarvingVariation {

		private int order;
		private IBlockState state;

		public SimpleCarvingVariation(IBlockState state, int order) {
			this.order = order;
			this.state = state;
		}

		@Override
		public Block getBlock() {
			return state.getBlock();
		}

		@Override
		public IBlockState getBlockState() {
			return state;
		}

		@Override
		public ItemStack getStack() {
			return new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
		}

		@Override
		public int getOrder() {
			return order;
		}
	}

	public static class SimpleCarvingGroup implements ICarvingGroup {

		private String name;
		private String sound;
		private String oreName;

		private List<ICarvingVariation> variations = Lists.newArrayList();

		public SimpleCarvingGroup(String name) {
			this.name = name;
		}

		@Override
		public List<ICarvingVariation> getVariations() {
			return Lists.newArrayList(variations);
		}

		@Override
		public void addVariation(ICarvingVariation variation) {
			variations.add(variation);
			Collections.sort(variations, new Comparator<ICarvingVariation>() {

				@Override
				public int compare(ICarvingVariation o1, ICarvingVariation o2) {
					return CarvingUtils.compare(o1, o2);
				}
			});
		}

		@Override
		public boolean removeVariation(ICarvingVariation variation) {
			ICarvingVariation toRemove = null;
			for (ICarvingVariation v : variations) {
				if (v.getBlockState().equals(variation.getBlockState())) {
					toRemove = v;
				}
			}
			return toRemove == null ? false : variations.remove(toRemove);
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getSound() {
			return sound;
		}

		@Override
		public void setSound(String sound) {
			this.sound = sound;
		}

		@Override
		public String getOreName() {
			return oreName;
		}

		@Override
		public void setOreName(String oreName) {
			this.oreName = oreName;
		}
	}
}
