package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.container.BaseContainerProvider;
import moze_intel.projecte.gameObjs.container.MercurialEyeContainer;
import moze_intel.projecte.gameObjs.container.inventory.MercurialEyeInventory;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class MercurialEye extends ItemMode implements IExtraFunction
{
	public MercurialEye(Properties props)
	{
		super(props, (byte)4, new String[] {"Normal", "Transmutation"});
	}
	
	private static final int NORMAL_MODE = 0;
	private static final int TRANSMUTATION_MODE = 1;

	private static final double WALL_MODE = Math.sin(Math.toRadians(45));

	@Nonnull
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT prevCapNBT)
	{
		return new ICapabilitySerializable<CompoundNBT>() {
			private final IItemHandler inv = new ItemStackHandler(2);
			private final LazyOptional<IItemHandler> invInst = LazyOptional.of(() -> inv);

			@Override
			public CompoundNBT serializeNBT()
			{
				CompoundNBT ret = new CompoundNBT();
				ret.put("Items", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inv, null));
				return ret;
			}

			@Override
			public void deserializeNBT(CompoundNBT nbt)
			{
				CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inv, null, nbt.getList("Items", NBT.TAG_COMPOUND));
			}

			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
				if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				{
					return invInst.cast();
				} else
				{
					return LazyOptional.empty();
				}
			}
		};
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		if (!ctx.getWorld().isRemote)
		{
			ItemStack stack = ctx.getItem();
			IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(NullPointerException::new);

			if (inventory.getStackInSlot(0).isEmpty()|| inventory.getStackInSlot(1).isEmpty())
			{
				return ActionResultType.FAIL;
			}

			if (!(inventory.getStackInSlot(0).getItem() instanceof IItemEmc))
			{
				return ActionResultType.FAIL;
			}

			BlockState newState = ItemHelper.stackToState(inventory.getStackInSlot(1));
			if (newState == null || newState.getBlock() == Blocks.AIR)
			{
				return ActionResultType.FAIL;
			}

			double kleinEmc = ((IItemEmc) inventory.getStackInSlot(0).getItem()).getStoredEmc(inventory.getStackInSlot(0));
			long reqEmc = EMCHelper.getEmcValue(inventory.getStackInSlot(1));

			int charge = getCharge(stack);
			byte mode = this.getMode(stack);

			Vec3d look = ctx.getPlayer().getLookVec();

			int dX = 0, dY = 0, dZ = 0;

			boolean lookingDown = look.y >= -1 && look.y <= -WALL_MODE;
			boolean lookingUp   = look.y <=  1 && look.y >=  WALL_MODE;

			boolean lookingAlongZ = ctx.getPlayer().getHorizontalFacing().getAxis() == Direction.Axis.Z;

			BlockPos pos = ctx.getPos();
			AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
			switch (ctx.getFace()) {
				case UP:
					if (lookingDown || mode == TRANSMUTATION_MODE)
					{
						box = box.expand(charge * 2, 0, charge * 2).offset(-charge, 0, -charge);
						dY = 1;
					}
					else if (lookingAlongZ)
						box = box.expand(charge * 2, charge * 2, 0).offset(-charge, 1, 0);
					else
						box = box.expand(0, charge * 2, charge * 2).offset(0, 1, -charge);

					break;

				case DOWN:
					if (lookingUp || mode == TRANSMUTATION_MODE)
					{
						box = box.expand(charge * 2, 0, charge * 2).offset(-charge, 0, -charge);
						dY = -1;

					}
					else if (lookingAlongZ)
						box = box.expand(charge *2, charge * 2, 0).offset(-charge, -1 - charge*2, 0);
					else
						box = box.expand(0, charge * 2, charge * 2).offset(0, -1 - charge*2, -charge);

					break;

				case EAST:
					box = box.expand(0, charge * 2, charge * 2).offset(0, -charge, -charge);
					dX = 1;
					break;

				case WEST:
					box = box.expand(0, charge * 2, charge * 2).offset(0, -charge, -charge);
					dX = -1;
					break;

				case SOUTH:
					box = box.expand(charge * 2, charge * 2, 0).offset(-charge, -charge, 0);
					dZ = 1;
					break;

				case NORTH:
					box = box.expand(charge * 2, charge * 2, 0).offset(-charge, -charge, 0);
					dZ = -1;
					break;
			}

			if (NORMAL_MODE == mode)
				box = box.offset(dX, dY, dZ);

			for (BlockPos currentPos : WorldHelper.getPositionsFromBox(box))
            {
                BlockState oldState = ctx.getWorld().getBlockState(currentPos);
                Block oldBlock = oldState.getBlock();

                if (mode == NORMAL_MODE && oldBlock == Blocks.AIR)
                {
                    if (kleinEmc < reqEmc)
                        break;
                    if (PlayerHelper.checkedPlaceBlock(((ServerPlayerEntity) ctx.getPlayer()), currentPos, newState))
                    {
                        removeKleinEMC(stack, reqEmc);
                        kleinEmc -= reqEmc;
                    }
                }
                else if (mode == TRANSMUTATION_MODE)
                {
					if (oldState == newState || oldBlock == Blocks.AIR || ctx.getWorld().getTileEntity(currentPos) != null || !EMCHelper.doesItemHaveEmc(new ItemStack(oldState.getBlock(), 1)))
                    {
                        continue;
                    }

					long emc = EMCHelper.getEmcValue(new ItemStack(oldState.getBlock(), 1));

                    if (emc > reqEmc)
                    {
                        if (PlayerHelper.checkedReplaceBlock(((ServerPlayerEntity) ctx.getPlayer()), currentPos, newState))
                        {
                            long difference = emc - reqEmc;
                            kleinEmc += MathHelper.clamp(kleinEmc, 0, ((IItemEmc) inventory.getStackInSlot(0).getItem()).getMaximumEmc(inventory.getStackInSlot(0)));
                            addKleinEMC(stack, difference);
                        }
                    }
                    else if (emc < reqEmc)
                    {
                        long difference = reqEmc - emc;

                        if (kleinEmc >= difference)
                        {
                            if (PlayerHelper.checkedReplaceBlock(((ServerPlayerEntity) ctx.getPlayer()), currentPos, newState))
                            {
                                kleinEmc -= difference;
                                removeKleinEMC(stack, difference);
                            }
                        }
                    }
                    else
                    {
                        PlayerHelper.checkedReplaceBlock(((ServerPlayerEntity) ctx.getPlayer()), currentPos, newState);
                    }
                }

            }

			ctx.getWorld().playSound(null, ctx.getPlayer().posX, ctx.getPlayer().posY, ctx.getPlayer().posZ, PESounds.POWER, SoundCategory.PLAYERS, 1.0F, 0.80F + ((0.20F / (float)getNumCharges(stack)) * charge));
		}

		return ActionResultType.SUCCESS;
	}

	private void addKleinEMC(ItemStack eye, long amount)
	{
		eye.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
			ItemStack stack = handler.getStackInSlot(0);

			if (!stack.isEmpty() && stack.getItem() instanceof IItemEmc)
			{
				((IItemEmc) stack.getItem()).addEmc(stack, amount);
			}
		});
	}

	private void removeKleinEMC(ItemStack eye, long amount)
	{
		eye.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
			ItemStack stack = handler.getStackInSlot(0);

			if (!stack.isEmpty() && stack.getItem() instanceof IItemEmc)
			{
				((IItemEmc) stack.getItem()).extractEmc(stack, amount);
			}
		});
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand)
	{
		NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(player.getHeldItem(hand)));
		return true;
	}

	private static class ContainerProvider extends BaseContainerProvider
	{
		private final ItemStack stack;

		private ContainerProvider(ItemStack stack)
		{
			this.stack = stack;
		}

		@Nonnull
		@Override
		public Container createContainer(@Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerIn)
		{
			return new MercurialEyeContainer(playerInventory, new MercurialEyeInventory(stack));
		}

		@Nonnull
		@Override
		public String getGuiID()
		{
			return "projecte:mercurial_eye";
		}
	}

}
