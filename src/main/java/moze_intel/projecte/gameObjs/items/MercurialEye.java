package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class MercurialEye extends ItemMode implements IExtraFunction
{
	public MercurialEye()
	{
		super("mercurial_eye", (byte)4, new String[] {"Normal", "Transmutation"});
		this.setNoRepair();
	}
	
	final private int NORMAL_MODE = 0;
	final private int TRANSMUTATION_MODE = 1;

	final private double WALL_MODE = Math.sin(Math.toRadians(45));

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);

			if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
			{
				return stack;
			}

			ItemStack[] inventory = getInventory(stack);

			if (inventory[0] == null || inventory[1] == null)
			{
				return stack;
			}

			IBlockState newState = ItemHelper.stackToState(inventory[1]);
			if (newState == null || newState.getBlock() == Blocks.air)
			{
				return stack;
			}

			double kleinEmc = ItemPE.getEmc(inventory[0]);
			int reqEmc = EMCHelper.getEmcValue(inventory[1]);

			byte charge = getCharge(stack);
			byte mode = this.getMode(stack);

			int facing = MathHelper.floor_double((double) ((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
			Vec3 look = player.getLookVec();

			int dX = 0, dY = 0, dZ = 0;

			boolean lookingDown = look.yCoord >= -1 && look.yCoord <= -WALL_MODE;
			boolean lookingUp   = look.yCoord <=  1 && look.yCoord >=  WALL_MODE;

			boolean lookingAlongZ = facing == 0 || facing == 2;

			BlockPos pos = mop.getBlockPos();
			AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
			switch (mop.sideHit) {
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

			if (box != null)
			{
				for (BlockPos currentPos : WorldHelper.getPositionsFromBox(box))
				{
					IBlockState state = world.getBlockState(currentPos);
					Block oldBlock = state.getBlock();

					if (mode == NORMAL_MODE && world.isAirBlock(currentPos))
					{
						if (kleinEmc < reqEmc)
							break;
						world.setBlockState(pos, newState, 3);
						removeKleinEMC(stack, reqEmc);
						kleinEmc -= reqEmc;
					}
					else if (mode == TRANSMUTATION_MODE)
					{
						if (newState == state || oldBlock.isAir(world, currentPos) || world.getTileEntity(currentPos) != null || !EMCHelper.doesItemHaveEmc(ItemHelper.stateToStack(state, 1)))
						{
							continue;
						}

						int emc = EMCHelper.getEmcValue(ItemHelper.stateToStack(state, 1));

						if (emc > reqEmc)
						{
							int difference = emc - reqEmc;

							kleinEmc += MathHelper.clamp_double(kleinEmc, 0, EMCHelper.getKleinStarMaxEmc(inventory[0]));

							addKleinEMC(stack, difference);
							world.setBlockState(pos, newState, 3);
						}
						else if (emc < reqEmc)
						{
							int difference = reqEmc - emc;

							if (kleinEmc >= difference)
							{
								kleinEmc -= difference;
								removeKleinEMC(stack, difference);
								world.setBlockState(pos, newState, 3);
							}
						}
						else
						{
							world.setBlockState(pos, newState, 3);
						}
					}
				}

				player.worldObj.playSoundAtEntity(player, "projecte:item.pepower", 1.0F, 0.80F + ((0.20F / (float)numCharges) * charge));
			}
		}

		return stack;
	}

	private void addKleinEMC(ItemStack eye, int amount)
	{
		NBTTagList list = eye.getTagCompound().getTagList("Items", NBT.TAG_COMPOUND);

		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound nbt = list.getCompoundTagAt(i);

			if (nbt.getByte("Slot") == 0)
			{
				ItemStack kleinStar = ItemStack.loadItemStackFromNBT(nbt);

				NBTTagCompound tag = nbt.getCompoundTag("tag");

				double newEmc = MathHelper.clamp_double(tag.getDouble("StoredEMC") + amount, 0, EMCHelper.getKleinStarMaxEmc(kleinStar));

				tag.setDouble("StoredEMC", newEmc);
				break;
			}
		}
	}

	private void removeKleinEMC(ItemStack eye, int amount)
	{
		NBTTagList list = eye.getTagCompound().getTagList("Items", NBT.TAG_COMPOUND);

		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound nbt = list.getCompoundTagAt(i);

			if (nbt.getByte("Slot") == 0)
			{
				NBTTagCompound tag = nbt.getCompoundTag("tag");
				tag.setDouble("StoredEMC", tag.getDouble("StoredEMC") - amount);
				break;
			}
		}
	}

	private ItemStack[] getInventory(ItemStack eye)
	{
		ItemStack[] result = new ItemStack[2];

		if (eye.hasTagCompound())
		{
			NBTTagList list = eye.getTagCompound().getTagList("Items", NBT.TAG_COMPOUND);

			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound nbt = list.getCompoundTagAt(i);
				result[nbt.getByte("Slot")] = ItemStack.loadItemStackFromNBT(nbt);
			}
		}

		return result;
	}

	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player) 
	{
		player.openGui(PECore.instance, Constants.MERCURIAL_GUI, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) 
	{
		return 1; 
	}
}
