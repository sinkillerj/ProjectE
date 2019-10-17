package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.container.MercurialEyeContainer;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.NetworkHooks;
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

	public MercurialEye(Properties props)
	{
		super(props, (byte) 4, new String[]{
				"pe.pe_mercurial_eye.mode1",
				"pe.pe_mercurial_eye.mode2",
				"pe.pe_mercurial_eye.mode3",
				"pe.pe_mercurial_eye.mode4",
				"pe.pe_mercurial_eye.mode5",
				"pe.pe_mercurial_eye.mode6"});
	}

	@Nonnull
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT prevCapNBT)
	{
		return new ICapabilitySerializable<CompoundNBT>()
		{
			private final IItemHandler inv = new ItemStackHandler(2);
			private final LazyOptional<IItemHandler> invInst = LazyOptional.of(() -> inv);

			@Override
			public CompoundNBT serializeNBT()
			{
				CompoundNBT ret = new CompoundNBT();
				INBT nbtBase = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inv, null);
				if (nbtBase != null)
				{
					ret.put("Items", nbtBase);
				}
				return ret;
			}

			@Override
			public void deserializeNBT(CompoundNBT nbt)
			{
				CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inv, null, nbt.getList("Items", NBT.TAG_COMPOUND));
			}

			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing)
			{
			    return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, invInst);
			}
		};
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand)
	{
		INamedContainerProvider provider = new SimpleNamedContainerProvider((id, inv, pl) -> new MercurialEyeContainer(id, inv, hand), stack.getDisplayName());
		NetworkHooks.openGui((ServerPlayerEntity) player, provider, b -> {
			b.writeBoolean(hand == Hand.MAIN_HAND);
		});
		return true;
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		ItemStack stack = ctx.getItem();
		return ctx.getWorld().isRemote ? ActionResultType.SUCCESS : formBlocks(stack, ctx.getPlayer(), ctx.getPos(), ctx.getFace());
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (getMode(stack) == CREATION_MODE)
		{
			if (world.isRemote)
			{
				return ActionResult.newResult(ActionResultType.SUCCESS, stack);
			}
			Vec3d eyeVec = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
			Vec3d lookVec = player.getLookVec();
			//I'm not sure why there has to be a one point offset to the X coordinate here, but it's pretty consistent in testing.
			Vec3d targVec = eyeVec.add(lookVec.x * 2, lookVec.y * 2, lookVec.z * 2);
			return ActionResult.newResult(formBlocks(stack, player, new BlockPos(targVec), null), stack);
		}
		return ActionResult.newResult(ActionResultType.PASS, stack);
	}

	private ActionResultType formBlocks(ItemStack eye, PlayerEntity player, BlockPos startingPos, @Nullable Direction facing)
	{
		IItemHandler inventory = eye.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
		if (inventory == null)
		{
			return ActionResultType.FAIL;
		}
		ItemStack klein = inventory.getStackInSlot(0);
		if (klein.isEmpty() || !(klein.getItem() instanceof IItemEmc))
		{
			return ActionResultType.FAIL;
		}

		World world = player.getEntityWorld();
		BlockState startingState = world.getBlockState(startingPos);
		long startingBlockEmc = EMCHelper.getEmcValue(new ItemStack(startingState.getBlock()));
		ItemStack target = inventory.getStackInSlot(1);
		BlockState newState;
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
			return ActionResultType.FAIL;
		}
		if (newState == null || newState.getBlock().isAir(newState, null, null))
		{
			return ActionResultType.FAIL;
		}

		NonNullList<ItemStack> drops = NonNullList.create();
		int charge = getCharge(eye);
		int hitTargets = 0;
		if (mode == CREATION_MODE)
		{
			Block block = startingState.getBlock();
			if (facing != null && (!startingState.getMaterial().isReplaceable() || player.isSneaking() && !block.isAir(startingState, world, startingPos)))
			{
				BlockPos offsetPos = startingPos.offset(facing);
				BlockState offsetState = world.getBlockState(offsetPos);
				if (!offsetState.getMaterial().isReplaceable())
				{
					return ActionResultType.FAIL;
				}
				long offsetBlockEmc = EMCHelper.getEmcValue(new ItemStack(offsetState.getBlock()));
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
				BlockState placedState = world.getBlockState(pos);
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
				return ActionResultType.FAIL;
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
				BlockState checkState = world.getBlockState(pos);
				if (startingState != checkState)
				{
					continue;
				}
				BlockPos offsetPos = pos.offset(facing);
				BlockState offsetState = world.getBlockState(offsetPos);
				if (!Block.hasSolidSide(offsetState, world, offsetPos, facing))
				{
					boolean hit = false;
					if (mode == EXTENSION_MODE)
					{
						VoxelShape cbBox = startingState.getCollisionShape(world, offsetPos);
						if (world.checkNoEntityCollision(null, cbBox))
						{
							long offsetBlockEmc = EMCHelper.getEmcValue(offsetState.getBlock());
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
						for (Direction e : Direction.values())
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
		return ActionResultType.SUCCESS;
	}

	private boolean doBlockPlace(PlayerEntity player, BlockState oldState, BlockPos placePos, BlockState newState, ItemStack eye, long oldEMC, long newEMC, NonNullList<ItemStack> drops)
	{
		IItemHandler capability = eye.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
		if (capability == null)
		{
			return false;
		}
		ItemStack klein = capability.getStackInSlot(0);

		if (klein.isEmpty() || oldState == newState || ItemPE.getEmc(klein) < newEMC - oldEMC || player.getEntityWorld().getTileEntity(placePos) != null)
		{
			return false;
		}

		if (oldEMC == 0 && oldState.getBlockHardness(player.world, placePos) == -1.0F)
		{
			//Don't allow replacing unbreakable blocks (unless they have an EMC value)
			return false;
		}

		if (PlayerHelper.checkedReplaceBlock((ServerPlayerEntity) player, placePos, newState))
		{
			IItemEmc itemEMC = (IItemEmc) klein.getItem();
			if (oldEMC == 0)
			{
				//Drop the block because it doesn't have an emc value
				drops.addAll(Block.getDrops(oldState, ((ServerPlayerEntity) player).getServerWorld(), placePos, null));
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

	private int fillGaps(ItemStack eye, PlayerEntity player, World world, BlockState startingState, BlockState newState, long newBlockEmc, Pair<BlockPos, BlockPos> corners, NonNullList<ItemStack> drops)
	{
		int hitTargets = 0;
		for (BlockPos pos : WorldHelper.getPositionsFromBox(new AxisAlignedBB(corners.getLeft(), corners.getRight())))
		{
			VoxelShape bb = startingState.getCollisionShape(world, pos);
			if (world.checkNoEntityCollision(null, bb))
			{
				BlockState placeState = world.getBlockState(pos);
				if (placeState.getMaterial().isReplaceable())
				{
					//Only replace replaceable blocks
					long placeBlockEmc = EMCHelper.getEmcValue(placeState.getBlock());
					if (doBlockPlace(player, placeState, pos, newState, eye, placeBlockEmc, newBlockEmc, drops))
					{
						hitTargets++;
					}
				}
			}
		}
		return hitTargets;
	}

	private Pair<BlockPos, BlockPos> getCorners(BlockPos startingPos, Direction facing, int strength, int depth)
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
