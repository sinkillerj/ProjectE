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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MercurialEye extends ItemMode implements IExtraFunction
{
	public MercurialEye()
	{
		super("mercurial_eye", (byte)4, new String[] {"Extension", "Transmutation", "Pillar"});
		this.setNoRepair();
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private static final int EXTENSION_MODE = 0;
	private static final int TRANSMUTATION_MODE = 1;
	private static final int PILLAR_MODE = 2;
	private static final int PILLAR_STEP_RANGE = 3;

	@SubscribeEvent
	public void onLeftClick(PlayerInteractEvent.LeftClickBlock evt)
	{
		if (!evt.getWorld().isRemote && evt.getItemStack() != null && evt.getItemStack().getItem() == this)
		{
			byte mode = getMode(evt.getItemStack());
			formBlocks(evt.getItemStack(), evt.getEntityPlayer(), evt.getPos(), evt.getFace(), 1, mode);
			evt.setCanceled(true);
		}
	}

	@Nonnull
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound prevCapNBT)
	{
		return new ICapabilitySerializable<NBTTagCompound>() {
			private final IItemHandler inv = new ItemStackHandler(2);

			@Override
			public NBTTagCompound serializeNBT()
			{
				NBTTagCompound ret = new NBTTagCompound();
				ret.setTag("Items", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inv, null));
				return ret;
			}

			@Override
			public void deserializeNBT(NBTTagCompound nbt)
			{
				CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inv, null, nbt.getTagList("Items", NBT.TAG_COMPOUND));
			}

			@Override
			public boolean hasCapability(Capability<?> capability, EnumFacing facing)
			{
				return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
			}

			@Override
			public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
				if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				{
					return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv);
				} else
				{
					return null;
				}
			}
		};
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		byte charge = this.getCharge(stack);
		byte mode = this.getMode(stack);

		int magnitude = mode == PILLAR_MODE
				? (charge+1) * PILLAR_STEP_RANGE
				: (charge+1) * (charge+1) * (charge+1);

		PELogger.logInfo("mag %d mode %d", magnitude, mode);

		return world.isRemote ? EnumActionResult.SUCCESS : formBlocks(stack, player, pos, facing, magnitude, mode);
	}

	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		byte mode = this.getMode(stack);

		if (mode == PILLAR_MODE)
		{
			if (world.isRemote)
			{
				return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
			} else
			{
				Vec3d eyeVec = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
				Vec3d lookVec = player.getLookVec();
				//I'm not sure why there has to be a one point offset to the X coordinate here, but it's pretty consistent in testing.
				Vec3d targVec = eyeVec.addVector(lookVec.xCoord * 2, lookVec.yCoord * 2, lookVec.zCoord * 2);

				return ActionResult.newResult(formBlocks(stack, player, new BlockPos(targVec), null, 0, mode), stack);
			}
		}

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	private EnumActionResult formBlocks(ItemStack eye, EntityPlayer player, BlockPos startingPos, @Nullable EnumFacing facing, int magnitude, byte mode)
	{
		World world = player.getEntityWorld();
		IItemHandler inventory = eye.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		if (inventory.getStackInSlot(0) == null || inventory.getStackInSlot(1) == null)
		{
			return EnumActionResult.FAIL;
		}

		ItemStack target = inventory.getStackInSlot(1);

		IBlockState newState = ItemHelper.stackToState(target);
		int newBlockEmc = EMCHelper.getEmcValue(target);

		if (newState == null)
		{
			return EnumActionResult.FAIL;
		}

		IBlockState startingState = world.getBlockState(startingPos);
		int startingBlockEmc = EMCHelper.getEmcValue(ItemHelper.stateToStack(startingState, 1));

		if (startingState.getBlock() == Blocks.BEDROCK)
		{
			return EnumActionResult.FAIL;
		}

		int hitTargets = 0;

		if ((mode == EXTENSION_MODE || mode == PILLAR_MODE)
				&& startingBlockEmc != 0)
		{
			newState = startingState;
			newBlockEmc = startingBlockEmc;
		}

		if (mode == PILLAR_MODE)
		{
			facing = facing != null ? facing.getOpposite() : null;
			BlockPos start = startingPos;
			BlockPos end = startingPos;

			if (magnitude > 0)
			{
				PELogger.logInfo("mag %d", magnitude);
				magnitude--;
				if (facing != null)
				{
					switch (facing)
					{
						case DOWN:
							start = start.add(-1, -magnitude, -1);
							end = end.add(1, 0, 1);
							break;
						case UP:
							start = start.add(-1, 0, -1);
							end = end.add(1, magnitude, 1);
							break;
						case NORTH:
							start = start.add(-1, -1, -magnitude);
							end = end.add(1, 1, 0);
							break;
						case SOUTH:
							start = start.add(-1, -1, 0);
							end = end.add(1, 1, magnitude);
							break;
						case WEST:
							start = start.add(-magnitude, -1, -1);
							end = end.add(0, 1, 1);
							break;
						case EAST:
							start = start.add(0, -1, -1);
							end = end.add(magnitude, 1, 1);
							break;
					}
				}
			}

			for (BlockPos pos : WorldHelper.getPositionsFromBox(new AxisAlignedBB(start, end)))
			{
				AxisAlignedBB bb = startingState.getCollisionBoundingBox(world, pos);
				if (bb == null || world.checkNoEntityCollision(bb))
				{
					IBlockState placeState = world.getBlockState(pos);
					int placeBlockEmc = EMCHelper.getEmcValue(ItemHelper.stateToStack(placeState, 1));

					if (doBlockPlace(player, placeState, pos, newState, eye, placeBlockEmc, newBlockEmc))
					{
						hitTargets++;
					}
				}
			}
		} else
		{
			if (startingState.getBlock().isAir(startingState, world, startingPos))
			{
				return EnumActionResult.FAIL;
			}

			Set<BlockPos> visited = new HashSet<>();
			visited.add(startingPos);

			LinkedList<BlockPos> possibleBlocks = new LinkedList<>();
			possibleBlocks.add(startingPos);

			int attemptedTargets = 0;
			int maxTargets = magnitude;

			while (!possibleBlocks.isEmpty() && hitTargets < maxTargets && attemptedTargets < maxTargets * 4)
			{
				boolean hit = false;
				attemptedTargets++;
				BlockPos pos = possibleBlocks.poll();
				PELogger.logInfo("deque: " + pos);

				IBlockState checkState = world.getBlockState(pos);

				if (checkState != startingState)
					continue;

				BlockPos offsetPos = pos.offset(facing); // todo check null face
				IBlockState offsetState = world.getBlockState(offsetPos);
				int offsetBlockEmc = EMCHelper.getEmcValue(ItemHelper.stateToStack(offsetState, 1));

				if (!offsetState.isSideSolid(world, offsetPos, facing))
				{
					if (mode == EXTENSION_MODE)
					{
						if (world.checkNoEntityCollision(startingState.getCollisionBoundingBox(world, offsetPos))) // todo null
						{
							hit = doBlockPlace(player, offsetState, offsetPos, newState, eye, offsetBlockEmc, newBlockEmc);
						}
					} else if (mode == TRANSMUTATION_MODE)
					{
						hit = doBlockPlace(player, checkState, pos, newState, eye, startingBlockEmc, newBlockEmc);
					}
				}

				if (hit)
				{
					hitTargets++;

					for (EnumFacing e : EnumFacing.VALUES)
					{
						if (facing.getAxis() != e.getAxis())
						{
							if (visited.add(pos.offset(e)))
								possibleBlocks.offer(pos.offset(e));
							if (visited.add(pos.offset(e.getOpposite())))
								possibleBlocks.offer(pos.offset(e.getOpposite()));
						}
					}
				}
			}
		}

		if (hitTargets > 0)
		{

		}

		return EnumActionResult.SUCCESS;
	}

	private boolean doBlockPlace(EntityPlayer player, IBlockState oldState, BlockPos placePos, IBlockState newState, ItemStack eye, int oldEMC, int newEMC)
	{
		ItemStack klein = eye.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0);

		if (klein == null
				|| ItemPE.getEmc(klein) - (newEMC - oldEMC) < 0
				|| oldState == newState
				|| player.getEntityWorld().getTileEntity(placePos) != null)
		{
			return false;
		}

		List<ItemStack> drops = oldState.getBlock().getDrops(player.getEntityWorld(), placePos, oldState, 0);

		if (PlayerHelper.checkedReplaceBlock((EntityPlayerMP) player, placePos, newState))
		{
			if (oldEMC == 0)
			{
				drops.forEach(d -> Block.spawnAsEntity(player.getEntityWorld(), placePos, d));
				((IItemEmc) klein.getItem()).extractEmc(klein, newEMC);
			} else if (oldEMC > newEMC)
			{
				((IItemEmc) klein.getItem()).addEmc(klein, oldEMC - newEMC);
			} else if (oldEMC < newEMC)
			{
				((IItemEmc) klein.getItem()).extractEmc(klein, newEMC - oldEMC);
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, EnumHand hand)
	{
		player.openGui(PECore.instance, Constants.MERCURIAL_GUI, player.getEntityWorld(), hand == EnumHand.MAIN_HAND ? 0 : 1, -1, -1);
		return true;
	}

}
