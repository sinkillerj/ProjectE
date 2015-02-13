package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.IExtraFunction;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.CoordinateBox;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;

public class MercurialEye extends ItemMode implements IExtraFunction
{
	public MercurialEye()
	{
		super("mercurial_eye", (byte) 4, new String[] {"Normal", "Transmutation"});
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

			Block toSet = Block.getBlockFromItem(inventory[1].getItem());

			if (toSet == Blocks.air)
			{
				return stack;
			}

			int toSetMeta = inventory[1].getItemDamage();

			double kleinEmc = ItemPE.getEmc(inventory[0]);
			int reqEmc = Utils.getEmcValue(inventory[1]);

			byte charge = getCharge(stack);
			byte mode = this.getMode(stack);

			int facing = MathHelper.floor_double((double) ((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
			ForgeDirection dir = ForgeDirection.getOrientation(mop.sideHit);
			Vec3 look = player.getLookVec();

			CoordinateBox box = new CoordinateBox(
				mop.blockX,
				mop.blockY,
				mop.blockZ,
				mop.blockX,
				mop.blockY,
				mop.blockZ
			);

			int dX = 0, dY = 0, dZ = 0;

			boolean lookingDown = look.yCoord >= -1 && look.yCoord <= -WALL_MODE;
			boolean lookingUp   = look.yCoord <=  1 && look.yCoord >=  WALL_MODE;

			boolean lookingAlongZ = facing == 0 || facing == 2;

			switch (dir) {
				case UP:
					if (lookingDown || mode == TRANSMUTATION_MODE)
					{
						box.expand(charge, 0, charge);
						dY = 1;
					}
					else if (lookingAlongZ)
						box.expand(charge, charge * 2, 0).offset(0, charge, 0);
					else
						box.expand(0, charge * 2, charge).offset(0, charge, 0);

					break;

				case DOWN:
					if (lookingUp || mode == TRANSMUTATION_MODE)
					{
						box.expand(charge, 0, charge);
						dY = 1;

					}
					else if (lookingAlongZ)
						box.expand(charge, charge * 2, 0).offset(0, -charge, 0);
					else
						box.expand(0, charge * 2, charge).offset(0, -charge, 0);

					break;

				case EAST:
					box.expand(0, charge, charge);
					dX = 1;
					break;

				case WEST:
					box.expand(0, charge, charge);
					dX = -1;
					break;

				case SOUTH:
					box.expand(charge, charge, 0);
					dZ = 1;
					break;

				case NORTH:
					box.expand(charge, charge, 0);
					dZ = -1;
					break;
			}

			if (NORMAL_MODE == mode)
				box.offset(dX, dY, dZ);

			if (box != null)
			{
				for (int x = (int) box.minX; x <= (int) box.maxX; x++)
				for (int y = (int) box.minY; y <= (int) box.maxY; y++)
				for (int z = (int) box.minZ; z <= (int) box.maxZ; z++)
				{
					Block b = world.getBlock(x, y, z);

					if (mode == NORMAL_MODE && b == Blocks.air)
					{
						if (kleinEmc < reqEmc)
							break;

						world.setBlock(x, y, z, toSet);
						world.setBlockMetadataWithNotify(x, y, z, toSetMeta, 3);
						removeKleinEMC(stack, reqEmc);
						kleinEmc -= reqEmc;
					}
					else if (mode == TRANSMUTATION_MODE)
					{
						if (b == toSet || b == Blocks.air || world.getTileEntity(x, y, z) != null || !Utils.doesItemHaveEmc(new ItemStack(b)))
						{
							continue;
						}

						int emc = Utils.getEmcValue(new ItemStack(b));

						if (emc > reqEmc)
						{
							int difference = emc - reqEmc;

							kleinEmc += MathHelper.clamp_double(kleinEmc, 0, Utils.getKleinStarMaxEmc(inventory[0]));

							addKleinEMC(stack, difference);
							world.setBlock(x, y, z, toSet);
							world.setBlockMetadataWithNotify(x, y, z, toSetMeta, 3);
						}
						else if (emc < reqEmc)
						{
							int difference = reqEmc - emc;

							if (kleinEmc >= difference)
							{
								kleinEmc -= difference;
								removeKleinEMC(stack, difference);
								world.setBlock(x, y, z, toSet);
								world.setBlockMetadataWithNotify(x, y, z, toSetMeta, 3);
							}
						}
						else
						{
							world.setBlock(x, y, z, toSet);
							world.setBlockMetadataWithNotify(x, y, z, toSetMeta, 3);
						}
					}
				}
			}
		}

		return stack;
	}

	private void addKleinEMC(ItemStack eye, int amount)
	{
		NBTTagList list = eye.stackTagCompound.getTagList("Items", NBT.TAG_COMPOUND);

		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound nbt = list.getCompoundTagAt(i);

			if (nbt.getByte("Slot") == 0)
			{
				ItemStack kleinStar = ItemStack.loadItemStackFromNBT(nbt);

				NBTTagCompound tag = nbt.getCompoundTag("tag");

				double newEmc = MathHelper.clamp_double(tag.getDouble("StoredEMC") + amount, 0, Utils.getKleinStarMaxEmc(kleinStar));

				tag.setDouble("StoredEMC", newEmc);
				break;
			}
		}
	}

	private void removeKleinEMC(ItemStack eye, int amount)
	{
		NBTTagList list = eye.stackTagCompound.getTagList("Items", NBT.TAG_COMPOUND);

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
			NBTTagList list = eye.stackTagCompound.getTagList("Items", NBT.TAG_COMPOUND);

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
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("mercurial_eye"));
	}
}
