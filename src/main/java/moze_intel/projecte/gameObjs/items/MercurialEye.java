package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.utils.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class MercurialEye extends ItemMode implements IExtraFunction
{
	private static final int CREATION_MODE = 0;
	private static final int EXTENSION_MODE = 1;
	private static final int EXTENSION_MODE_CLASSIC = 2;
	private static final int TRANSMUTATION_MODE = 3;
	private static final int TRANSMUTATION_MODE_CLASSIC = 4;
	private static final int PILLAR_MODE = 5;

	public MercurialEye()
	{
		super("mercurial_eye", (byte) 4, new String[]{
				"pe.pe_mercurial_eye.mode1",
				"pe.pe_mercurial_eye.mode2",
				"pe.pe_mercurial_eye.mode3",
				"pe.pe_mercurial_eye.mode4",
				"pe.pe_mercurial_eye.mode5",
				"pe.pe_mercurial_eye.mode6"});
		this.setNoRepair();
	}

	@Nonnull
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound prevCapNBT)
	{
		return new ICapabilitySerializable<NBTTagCompound>()
		{
			private final IItemHandler inv = new ItemStackHandler(2);

			@Override
			public NBTTagCompound serializeNBT()
			{
				NBTTagCompound ret = new NBTTagCompound();
				NBTBase nbtBase = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inv, null);
				if (nbtBase != null)
				{
					ret.setTag("Items", nbtBase);
				}
				return ret;
			}

			@Override
			public void deserializeNBT(NBTTagCompound nbt)
			{
				CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inv, null, nbt.getTagList("Items", NBT.TAG_COMPOUND));
			}

			@Override
			public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing)
			{
				return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
			}

			@Override
			public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing)
			{
				if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				{
					return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv);
				}
				return null;
			}
		};
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, EnumHand hand)
	{
		player.openGui(PECore.instance, Constants.MERCURIAL_GUI, player.getEntityWorld(), hand == EnumHand.MAIN_HAND ? 0 : 1, -1, -1);
		return true;
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);
		return world.isRemote ? EnumActionResult.SUCCESS : formBlocks(stack, player, pos, facing);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (getMode(stack) == CREATION_MODE)
		{
			if (world.isRemote)
			{
				return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
			}
			Vec3d eyeVec = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
			Vec3d lookVec = player.getLookVec();
			//I'm not sure why there has to be a one point offset to the X coordinate here, but it's pretty consistent in testing.
			Vec3d targVec = eyeVec.add(lookVec.x * 2, lookVec.y * 2, lookVec.z * 2);
			return ActionResult.newResult(formBlocks(stack, player, new BlockPos(targVec), null), stack);
		}
		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	private EnumActionResult formBlocks(ItemStack eye, EntityPlayer player, BlockPos startingPos, @Nullable EnumFacing facing)
	{
		IItemHandler inventory = eye.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if (inventory == null)
		{
			return EnumActionResult.FAIL;
		}
		ItemStack klein = inventory.getStackInSlot(0);
		if (klein.isEmpty() || !(klein.getItem() instanceof IItemEmc))
		{
			return EnumActionResult.FAIL;
		}

		World world = player.getEntityWorld();
		IBlockState startingState = world.getBlockState(startingPos);
		long startingBlockEmc = EMCHelper.getEmcValue(ItemHelper.stateToStack(startingState, 1));
		ItemStack target = inventory.getStackInSlot(1);
		IBlockState newState;
		long newBlockEmc;
		byte mode = getMode(eye);

		if (!target.isEmpty())
		{
			newState = ItemHelper.stackToState(target);
			newBlockEmc = EMCHelper.getEmcValue(target);
		}
		else if (startingBlockEmc != 0 && (mode == EXTENSION_MODE || mode == EXTENSION_MODE_CLASSIC))
		{
			//If there is no item key, attempt to determine it for extension mode
			newState = startingState;
			newBlockEmc = startingBlockEmc;
		}
		else
		{
			return EnumActionResult.FAIL;
		}
		if (newState == null || newState.getBlock().isAir(newState, null, null))
		{
			return EnumActionResult.FAIL;
		}

		NonNullList<ItemStack> drops = NonNullList.create();
		int charge = getCharge(eye);
		int hitTargets = 0;
		if (mode == CREATION_MODE)
		{
			Block block = startingState.getBlock();
			if (facing != null && (!block.isReplaceable(world, startingPos) || player.isSneaking() && !block.isAir(startingState, world, startingPos)))
			{
				BlockPos offsetPos = startingPos.offset(facing);
				IBlockState offsetState = world.getBlockState(offsetPos);
				if (!offsetState.getBlock().isReplaceable(world, offsetPos))
				{
					return EnumActionResult.FAIL;
				}
				long offsetBlockEmc = EMCHelper.getEmcValue(ItemHelper.stateToStack(offsetState, 1));
				//Just in case it is not air but is a replaceable block like tall grass, get the proper EMC instead of just using 0
				if (doBlockPlace(player, offsetState, offsetPos, newState, eye, offsetBlockEmc, newBlockEmc, drops))
				{
					hitTargets++;
				}
			}
			else if (doBlockPlace(player, startingState, startingPos, newState, eye, startingBlockEmc, newBlockEmc, drops))
			{
				//Otherwise replace it (it may have been air), or it may have been something like tall grass
				hitTargets++;
			}
		}
		else if (mode == PILLAR_MODE)
		{
			//Fills in replaceable blocks in up to a 3x3x3/6/9/12/15 area
			hitTargets += fillGaps(eye, player, world, startingState, newState, newBlockEmc, getCorners(startingPos, facing, 1, 3 * charge + 2), drops);
		}
		else if (mode == EXTENSION_MODE_CLASSIC)
		{
			//if it is replaceable fill in the gaps in up to a 9x9x1 area
			hitTargets += fillGaps(eye, player, world, startingState, newState, newBlockEmc, getCorners(startingPos, facing, charge, 0), drops);
		}
		else if (mode == TRANSMUTATION_MODE_CLASSIC)
		{
			//if state is same as the start state replace it in an up to 9x9x1 area
			Pair<BlockPos, BlockPos> corners = getCorners(startingPos, facing, charge, 0);
			for (BlockPos pos : WorldHelper.getPositionsFromBox(new AxisAlignedBB(corners.getLeft(), corners.getRight())))
			{
				IBlockState placedState = world.getBlockState(pos);
				if (placedState == startingState && doBlockPlace(player, placedState, pos, newState, eye, startingBlockEmc, newBlockEmc, drops))
				{
					hitTargets++;
				}
			}
		}
		else
		{
			if (startingState.getBlock().isAir(startingState, world, startingPos) || facing == null)
			{
				return EnumActionResult.FAIL;
			}
			LinkedList<BlockPos> possibleBlocks = new LinkedList<>();
			Set<BlockPos> visited = new HashSet<>();
			possibleBlocks.add(startingPos);
			visited.add(startingPos);

			int side = 2 * charge + 1;
			int size = side * side;
			int totalTries = size * 4;
			for (int attemptedTargets = 0; attemptedTargets < totalTries && !possibleBlocks.isEmpty(); attemptedTargets++)
			{
				BlockPos pos = possibleBlocks.poll();
				IBlockState checkState = world.getBlockState(pos);
				if (startingState != checkState)
				{
					continue;
				}
				BlockPos offsetPos = pos.offset(facing);
				IBlockState offsetState = world.getBlockState(offsetPos);
				if (!offsetState.isSideSolid(world, offsetPos, facing))
				{
					boolean hit = false;
					if (mode == EXTENSION_MODE)
					{
						AxisAlignedBB cbBox = startingState.getCollisionBoundingBox(world, offsetPos);
						if (cbBox == null || world.checkNoEntityCollision(cbBox))
						{
							long offsetBlockEmc = EMCHelper.getEmcValue(ItemHelper.stateToStack(offsetState, 1));
							hit = doBlockPlace(player, offsetState, offsetPos, newState, eye, offsetBlockEmc, newBlockEmc, drops);
						}
					}
					else if (mode == TRANSMUTATION_MODE)
					{
						hit = doBlockPlace(player, checkState, pos, newState, eye, startingBlockEmc, newBlockEmc, drops);
					}

					if (hit)
					{
						hitTargets++;
						if (hitTargets >= size)
						{
							break;
						}
						for (EnumFacing e : EnumFacing.VALUES)
						{
							if (facing.getAxis() != e.getAxis())
							{
								BlockPos offset = pos.offset(e);
								if (visited.add(offset))
								{
									possibleBlocks.offer(offset);
								}
								BlockPos offsetOpposite = pos.offset(e.getOpposite());
								if (visited.add(offsetOpposite))
								{
									possibleBlocks.offer(offsetOpposite);
								}
							}
						}
					}
				}
			}
		}

		if (hitTargets > 0)
		{
			if (PESounds.POWER != null)
			{
				world.playSound(null, player.posX, player.posY, player.posZ, PESounds.POWER, SoundCategory.PLAYERS, 0.8F, 2F / ((float) charge / getNumCharges(eye) + 2F));
			}
			if (!drops.isEmpty())
			{
				//Make all the drops fall together
				WorldHelper.createLootDrop(drops, player.getEntityWorld(), startingPos);
			}
		}
		return EnumActionResult.SUCCESS;
	}

	private boolean doBlockPlace(EntityPlayer player, IBlockState oldState, BlockPos placePos, IBlockState newState, ItemStack eye, long oldEMC, long newEMC, NonNullList<ItemStack> drops)
	{
		IItemHandler capability = eye.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if (capability == null)
		{
			return false;
		}
		ItemStack klein = capability.getStackInSlot(0);

		if (klein.isEmpty() || oldState == newState || ItemPE.getEmc(klein) < newEMC - oldEMC || player.getEntityWorld().getTileEntity(placePos) != null)
		{
			return false;
		}

		if (oldEMC == 0 && oldState.getBlock().blockHardness == -1.0F)
		{
			//Don't allow replacing unbreakable blocks (unless they have an EMC value)
			return false;
		}

		if (PlayerHelper.checkedReplaceBlock((EntityPlayerMP) player, placePos, newState))
		{
			IItemEmc itemEMC = (IItemEmc) klein.getItem();
			if (oldEMC == 0)
			{
				//Drop the block because it doesn't have an emc value
				oldState.getBlock().getDrops(drops, player.getEntityWorld(), placePos, oldState, 0);
				itemEMC.extractEmc(klein, newEMC);
			}
			else if (oldEMC > newEMC)
			{
				itemEMC.addEmc(klein, oldEMC - newEMC);
			}
			else if (oldEMC < newEMC)
			{
				itemEMC.extractEmc(klein, newEMC - oldEMC);
			}
			return true;
		}
		return false;
	}

	private int fillGaps(ItemStack eye, EntityPlayer player, World world, IBlockState startingState, IBlockState newState, long newBlockEmc, Pair<BlockPos, BlockPos> corners, NonNullList<ItemStack> drops)
	{
		int hitTargets = 0;
		for (BlockPos pos : WorldHelper.getPositionsFromBox(new AxisAlignedBB(corners.getLeft(), corners.getRight())))
		{
			AxisAlignedBB bb = startingState.getCollisionBoundingBox(world, pos);
			if (bb == null || world.checkNoEntityCollision(bb))
			{
				IBlockState placeState = world.getBlockState(pos);
				if (placeState.getBlock().isReplaceable(world, pos))
				{
					//Only replace replaceable blocks
					long placeBlockEmc = EMCHelper.getEmcValue(ItemHelper.stateToStack(placeState, 1));
					if (doBlockPlace(player, placeState, pos, newState, eye, placeBlockEmc, newBlockEmc, drops))
					{
						hitTargets++;
					}
				}
			}
		}
		return hitTargets;
	}

	private Pair<BlockPos, BlockPos> getCorners(BlockPos startingPos, EnumFacing facing, int strength, int depth)
	{
		if (facing == null)
		{
			return new ImmutablePair<>(startingPos, startingPos);
		}
		BlockPos start = startingPos;
		BlockPos end = startingPos;
		switch (facing)
		{
			case UP:
				start = start.add(-strength, -depth, -strength);
				end = end.add(strength, 0, strength);
				break;
			case DOWN:
				start = start.add(-strength, 0, -strength);
				end = end.add(strength, depth, strength);
				break;
			case SOUTH:
				start = start.add(-strength, -strength, -depth);
				end = end.add(strength, strength, 0);
				break;
			case NORTH:
				start = start.add(-strength, -strength, 0);
				end = end.add(strength, strength, depth);
				break;
			case EAST:
				start = start.add(-depth, -strength, -strength);
				end = end.add(0, strength, strength);
				break;
			case WEST:
				start = start.add(0, -strength, -strength);
				end = end.add(depth, strength, strength);
				break;
		}
		return new ImmutablePair<>(start, end);
	}
}