package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class MercurialEye extends ItemMode implements IExtraFunction
{
	public MercurialEye()
	{
		super("mercurial_eye", (byte)4, new String[] {"Normal", "Transmutation"});
		this.setNoRepair();
	}
	
	private static final int NORMAL_MODE = 0;
	private static final int TRANSMUTATION_MODE = 1;

	private static final double WALL_MODE = Math.sin(Math.toRadians(45));

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
		if (!world.isRemote)
		{
			IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

			if (inventory.getStackInSlot(0) == null || inventory.getStackInSlot(1) == null)
			{
				return EnumActionResult.FAIL;
			}

			if (!(inventory.getStackInSlot(0).getItem() instanceof IItemEmc))
			{
				return EnumActionResult.FAIL;
			}

			IBlockState newState = ItemHelper.stackToState(inventory.getStackInSlot(1));
			if (newState == null || newState.getBlock() == Blocks.AIR)
			{
				return EnumActionResult.FAIL;
			}

			double kleinEmc = ((IItemEmc) inventory.getStackInSlot(0).getItem()).getStoredEmc(inventory.getStackInSlot(0));
			int reqEmc = EMCHelper.getEmcValue(inventory.getStackInSlot(1));

			byte charge = getCharge(stack);
			byte mode = this.getMode(stack);

			Vec3d look = player.getLookVec();

			int dX = 0, dY = 0, dZ = 0;

			boolean lookingDown = look.yCoord >= -1 && look.yCoord <= -WALL_MODE;
			boolean lookingUp   = look.yCoord <=  1 && look.yCoord >=  WALL_MODE;

			boolean lookingAlongZ = facing.getAxis() == EnumFacing.Axis.Z;

			AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
			switch (facing) {
				case UP:
					if (lookingDown || mode == TRANSMUTATION_MODE)
					{
						box = box.expand(charge, 0, charge);
						dY = 1;
					}
					else if (lookingAlongZ)
						box = box.expand(charge, charge * 2, 0).offset(0, charge, 0);
					else
						box = box.expand(0, charge * 2, charge).offset(0, charge, 0);

					break;

				case DOWN:
					if (lookingUp || mode == TRANSMUTATION_MODE)
					{
						box = box.expand(charge, 0, charge);
						dY = -1;

					}
					else if (lookingAlongZ)
						box = box.expand(charge, charge * 2, 0).offset(0, -charge, 0);
					else
						box = box.expand(0, charge * 2, charge).offset(0, -charge, 0);

					break;

				case EAST:
					box = box.expand(0, charge, charge);
					dX = 1;
					break;

				case WEST:
					box = box.expand(0, charge, charge);
					dX = -1;
					break;

				case SOUTH:
					box = box.expand(charge, charge, 0);
					dZ = 1;
					break;

				case NORTH:
					box = box.expand(charge, charge, 0);
					dZ = -1;
					break;
			}

			if (NORMAL_MODE == mode)
				box = box.offset(dX, dY, dZ);

			for (BlockPos currentPos : WorldHelper.getPositionsFromBox(box))
            {
                IBlockState oldState = world.getBlockState(currentPos);
                Block oldBlock = oldState.getBlock();

                if (mode == NORMAL_MODE && oldBlock == Blocks.AIR)
                {
                    if (kleinEmc < reqEmc)
                        break;
                    if (PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), currentPos, newState))
                    {
                        removeKleinEMC(stack, reqEmc);
                        kleinEmc -= reqEmc;
                    }
                }
                else if (mode == TRANSMUTATION_MODE)
                {
                    if (oldState == newState || oldBlock == Blocks.AIR || world.getTileEntity(currentPos) != null || !EMCHelper.doesItemHaveEmc(ItemHelper.stateToStack(oldState, 1)))
                    {
                        continue;
                    }

                    int emc = EMCHelper.getEmcValue(ItemHelper.stateToStack(oldState, 1));

                    if (emc > reqEmc)
                    {
                        if (PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), currentPos, newState))
                        {
                            int difference = emc - reqEmc;
                            kleinEmc += MathHelper.clamp_double(kleinEmc, 0, ((IItemEmc) inventory.getStackInSlot(0).getItem()).getMaximumEmc(inventory.getStackInSlot(0)));
                            addKleinEMC(stack, difference);
                        }
                    }
                    else if (emc < reqEmc)
                    {
                        int difference = reqEmc - emc;

                        if (kleinEmc >= difference)
                        {
                            if (PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), currentPos, newState))
                            {
                                kleinEmc -= difference;
                                removeKleinEMC(stack, difference);
                            }
                        }
                    }
                    else
                    {
                        PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), currentPos, newState);
                    }
                }

            }

			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.POWER, SoundCategory.PLAYERS, 1.0F, 0.80F + ((0.20F / (float)numCharges) * charge));
		}

		return EnumActionResult.SUCCESS;
	}

	private void addKleinEMC(ItemStack eye, int amount)
	{
		IItemHandler handler = eye.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		ItemStack stack = handler.getStackInSlot(0);

		if (stack != null && stack.getItem() instanceof IItemEmc)
		{
			((IItemEmc) stack.getItem()).addEmc(stack, amount);
		}
	}

	private void removeKleinEMC(ItemStack eye, int amount)
	{
		IItemHandler handler = eye.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		ItemStack stack = handler.getStackInSlot(0);

		if (stack != null && stack.getItem() instanceof IItemEmc)
		{
			((IItemEmc) stack.getItem()).extractEmc(stack, amount);
		}
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, EnumHand hand)
	{
		player.openGui(PECore.instance, Constants.MERCURIAL_GUI, player.getEntityWorld(), hand == EnumHand.MAIN_HAND ? 0 : 1, -1, -1);
		return true;
	}

}
