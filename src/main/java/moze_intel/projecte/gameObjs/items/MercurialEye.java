package moze_intel.projecte.gameObjs.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.List;

import moze_intel.projecte.utils.PELogger;

public class MercurialEye extends ItemMode implements IExtraFunction
{
	public MercurialEye()
	{
		super("mercurial_eye", (byte)4, new String[] {"Extension", "Transmutation", "Pillar"});
		this.setNoRepair();
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	protected class BlockPosition
	{
		public int x;
		public int y;
		public int z;
		
		BlockPosition(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public BlockPosition getNext(ForgeDirection facing)
		{
			switch(facing)
			{
			case EAST:
				return new BlockPosition(x+1, y, z);
			case WEST:
				return new BlockPosition(x-1, y, z);
			case UP:
				return new BlockPosition(x, y+1, z);
			case DOWN:
				return new BlockPosition(x, y-1, z);
			case SOUTH:
				return new BlockPosition(x, y, z+1);
			case NORTH:
				return new BlockPosition(x, y, z-1);
			}
			return this;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if(o == null || getClass() != o.getClass())
				return false;
			BlockPosition other = (BlockPosition) o;
			return x == other.x && y == other.y && z == other.z;
		}
		
		@Override
		public int hashCode()
		{
			int hashX = (x << 22);
			int hashY = (y << 22) >> 10;
			int hashZ = (z << 22) >> 20;
			return hashX + hashY + hashZ;
		}
	}
	
	final private int EXTENSION_MODE = 0;
	final private int TRANSMUTATION_MODE = 1;
	final private int PILLAR_MODE = 2;
	
	final private int PILLAR_STEP_RANGE = 3;

	//final private double WALL_MODE = Math.sin(Math.toRadians(45));

	public boolean isBlackListedBlock(Block block) {
		return block == null || block == Blocks.bedrock;
	}
	
	@SubscribeEvent
	public void onLeftClick(PlayerInteractEvent event) {		
		if(event.action == Action.LEFT_CLICK_BLOCK)
		{
			World world = event.world;
			EntityPlayer player = event.entityPlayer;
			ItemStack itemStack = player.getCurrentEquippedItem();
			if(itemStack != null && itemStack.getItem() == this)
			{
				PELogger.logInfo("Detected Left Click as Mercurial Eye Action");
				MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
				if (mop == null)
					return;
				int side = mop.sideHit;
				byte mode = this.getMode(itemStack);
				int magnitude = 0;
				if (mode != PILLAR_MODE)
					magnitude =1;
				
				formBlocks(itemStack, player, world, event.x, event.y, event.z, side, magnitude);
			}
		}
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		byte charge = getCharge(itemStack);
		
		byte mode = this.getMode(itemStack);
		int magnitude = charge;
		if(mode == PILLAR_MODE)
			magnitude = ((charge+1)*PILLAR_STEP_RANGE)-1;
		else
			magnitude = (charge+1) * (charge+1) * (charge+1);
		
		return formBlocks(itemStack, player, world, x, y, z, side, magnitude);
	}
	
	public boolean formBlocks(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, int magnitude)
	{
		if(world.isRemote)
			return true;
		if(itemStack == null)
			return false;
		
		if (player == null)
			return false;

		ItemStack[] inventory = getInventory(itemStack);

		if (inventory[0] == null || inventory[1] == null)
		{
			return false;
		}

		Block newBlock = Block.getBlockFromItem(inventory[1].getItem());

		if (newBlock == Blocks.air)
		{
			return false;
		}

		int newBlockMeta = inventory[1].getItemDamage();

		double kleinEmc = ItemPE.getEmc(inventory[0]);
		int newBlockEmc = EMCHelper.getEmcValue(inventory[1]);

//		byte charge = getCharge(itemStack);
		byte mode = this.getMode(itemStack);
		
		HashSet<BlockPosition> redundancyTest = new HashSet<BlockPosition>();
		
		int maxTargets = magnitude;

		LinkedList<BlockPosition> possibleBlocks = new LinkedList<BlockPosition>();
		
		Block startingBlock = world.getBlock(x, y, z);
		int startingBlockMeta = world.getBlockMetadata(x,y,z);
		
		if ((startingBlock == null) || (startingBlock == Blocks.bedrock))
			return false;
		
		int startingBlockEmc = 0;
		if(EMCHelper.doesItemHaveEmc(new ItemStack(startingBlock, 1, startingBlockMeta)))
			startingBlockEmc = EMCHelper.getEmcValue(new ItemStack(startingBlock, 1, startingBlockMeta));
		
		ForgeDirection facing = ForgeDirection.getOrientation(side);
		
		BlockPosition startingPos = new BlockPosition(x, y, z);
		
		if (redundancyTest.add(startingPos))
			possibleBlocks.add(startingPos);
		int hitTargets = 0;
		int testTargets = 0;

		if (((mode == EXTENSION_MODE) || mode == PILLAR_MODE) && (startingBlockEmc != 0))
		{
			newBlock = startingBlock;
			newBlockMeta = startingBlockMeta;
			newBlockEmc = startingBlockEmc;
		}

		if (mode == PILLAR_MODE)
		{
			facing = facing.getOpposite();
			
			double BBx1 = (double) startingPos.x;
			double BBy1 = (double) startingPos.y;
			double BBz1 = (double) startingPos.z;
			double BBx2 = BBx1;
			double BBy2 = BBy1;
			double BBz2 = BBz1;
			
			switch(facing)
			{
				case UP:
					BBx1--;
					BBx2++;
//					BBy1;
					BBy2+= magnitude;
					BBz1--;
					BBz2++;
//					box = box.expand(1, charge * 2, 1).offset(0, charge, 0);
					break;
				
				case DOWN:
					BBx1--;
					BBx2++;
					BBy1-= magnitude;
//					BBy2;
					BBz1--;
					BBz2++;
//					box = box.expand(1, charge *2, 1).offset(0, -charge, 0);
					break;
			
				case EAST:
					//BBx1--;
					BBx2+= magnitude;
					BBy1--;
					BBy2++;
					BBz1--;
					BBz2++;
//					box = box.expand(charge *2, 1, 1).offset(charge, 0, 0);
					break;

				case WEST:
					BBx1-= magnitude;
					//BBx2++;
					BBy1--;
					BBy2++;
					BBz1--;
					BBz2++;
//					box = box.expand(charge *2, 1, 1).offset(-charge, 0, 0);
					break;

				case SOUTH:
					BBx1--;
					BBx2++;
					BBy1--;
					BBy2++;
					//BBz1--;
					BBz2+= magnitude;
//					box = box.expand(1, 1, charge *2).offset(0, 0, charge);
					break;

				case NORTH:
					BBx1--;
					BBx2++;
					BBy1--;
					BBy2++;
					BBz1-= magnitude;
					//BBz2++;
//					box = box.expand(1, 1, charge*2).offset(0, 0, -charge);
					break;
			}

			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(BBx1,BBy1,BBz1,BBx2,BBy2,BBz2);

			if (box != null)
			{
				for (int px = (int) box.minX; px <= (int) box.maxX; px++)
				{
					for (int py = (int) box.minY; py <= (int) box.maxY; py++)
					{
						for (int pz = (int) box.minZ; pz <= (int) box.maxZ; pz++)
						{
							if (kleinEmc < newBlockEmc)
								break;
							
							AxisAlignedBB blockBB = startingBlock.getCollisionBoundingBoxFromPool(world, px, py, pz);
							List entitiesInBox;
							if (blockBB == null)
							{
								PELogger.logInfo("Null bounding box for block " + px + "," + py + "," + pz);
								entitiesInBox = null;
							}
							else
							{
								entitiesInBox = world.getEntitiesWithinAABB(EntityLivingBase.class, blockBB);
							}
							if ((entitiesInBox == null) || (entitiesInBox.size() == 0))
							{
	//							PELogger.logInfo("Attempting to place block at " + faceBlockPos.x + "," + faceBlockPos.y + "," + faceBlockPos.z);
							
								if (PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), px, py, pz, newBlock, newBlockMeta))
								{
									removeKleinEMC(itemStack, newBlockEmc);
									kleinEmc -= newBlockEmc;
									hitTargets++;
								}
							}
							else
							{
								PELogger.logInfo("" + entitiesInBox.size() + " entities interfered with placing a block at " + px + "," + py + "," + pz);
							}
						}
					}
				}
			}
		}
		else
		{
			while (possibleBlocks.size() > 0 && hitTargets < maxTargets && testTargets < maxTargets * 4)
			{
				boolean hit = false;
				testTargets++;
				BlockPosition checkBlockPos = possibleBlocks.removeFirst();
				
				Block checkBlock = world.getBlock(checkBlockPos.x,checkBlockPos.y,checkBlockPos.z);
				int checkBlockMeta = world.getBlockMetadata(checkBlockPos.x,checkBlockPos.y,checkBlockPos.z);
				
				if(!checkBlock.equals(startingBlock)) continue;
				if(checkBlockMeta != startingBlockMeta) continue;

				BlockPosition faceBlockPos = checkBlockPos.getNext(facing);
				Block faceBlock = world.getBlock(faceBlockPos.x, faceBlockPos.y, faceBlockPos.z);

				if(!faceBlock.isSideSolid(world, faceBlockPos.x, faceBlockPos.y, faceBlockPos.z, facing.getOpposite()))
				{
					if (mode == EXTENSION_MODE)
					{
							if (kleinEmc < newBlockEmc)
								break;
							
							AxisAlignedBB blockBB = startingBlock.getCollisionBoundingBoxFromPool(world, faceBlockPos.x, faceBlockPos.y, faceBlockPos.z);
							List entitiesInBox;
							if (blockBB == null)
							{
								PELogger.logInfo("Null bounding box for block " + faceBlockPos.x + "," + faceBlockPos.y + "," + faceBlockPos.z);
								entitiesInBox = null;
							}
							else
							{
								entitiesInBox = world.getEntitiesWithinAABB(EntityLivingBase.class, blockBB);
							}
							if ((entitiesInBox == null) || (entitiesInBox.size() == 0))
							{
	//							PELogger.logInfo("Attempting to place block at " + faceBlockPos.x + "," + faceBlockPos.y + "," + faceBlockPos.z);
							
								if (PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), faceBlockPos.x, faceBlockPos.y, faceBlockPos.z, newBlock, newBlockMeta))
								{
									removeKleinEMC(itemStack, newBlockEmc);
									kleinEmc -= newBlockEmc;
									hit = true;
								}
							}
							else
							{
								PELogger.logInfo("" + entitiesInBox.size() + " entities interfered with placing a block at " + faceBlockPos.x + "," + faceBlockPos.y + "," + faceBlockPos.z);
							}
					}
					else if (mode == TRANSMUTATION_MODE)
					{
						if (world.getTileEntity(checkBlockPos.x, checkBlockPos.y, checkBlockPos.z) != null)
							continue;
						
						if ((startingBlock.equals(newBlock)) && (startingBlockMeta == newBlockMeta))
							continue;

						if (startingBlockEmc == 0)
						{
							if (kleinEmc < newBlockEmc)
								break;
							List<ItemStack> drops = checkBlock.getDrops(world, checkBlockPos.x, checkBlockPos.y, checkBlockPos.z, checkBlockMeta, 0);
							for(ItemStack drop : drops)
								world.spawnEntityInWorld(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, drop));
							world.setBlock(checkBlockPos.x, checkBlockPos.y, checkBlockPos.z, Blocks.air, 0, 3);
							if (PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), checkBlockPos.x, checkBlockPos.y, checkBlockPos.z, newBlock, newBlockMeta))
							{
								removeKleinEMC(itemStack, newBlockEmc);
								kleinEmc -= newBlockEmc;
								hit = true;
							}
						}
						else if (startingBlockEmc > newBlockEmc)
						{
							if (PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), checkBlockPos.x, checkBlockPos.y, checkBlockPos.z, newBlock, newBlockMeta))
							{
								int difference = startingBlockEmc - newBlockEmc;
								kleinEmc += MathHelper.clamp_double(kleinEmc, 0, EMCHelper.getKleinStarMaxEmc(inventory[0]));
								addKleinEMC(itemStack, difference);
								hit = true;
							}
						}
						else if (startingBlockEmc < newBlockEmc)
						{
							int difference = newBlockEmc - startingBlockEmc;

							if (kleinEmc >= difference)
							{
								if (PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), checkBlockPos.x, checkBlockPos.y, checkBlockPos.z, newBlock, newBlockMeta))
								{
									kleinEmc -= difference;
									removeKleinEMC(itemStack, difference);
									hit = true;
								}
							}
						}
						else
						{
							PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), checkBlockPos.x, checkBlockPos.y, checkBlockPos.z, newBlock, newBlockMeta);
							hit = true;
						}
					}
				}
				
				if (hit == true)
				{
					hit = false;
					hitTargets++;
				
					if ((facing != ForgeDirection.EAST) && (facing != ForgeDirection.WEST))
					{
						if (redundancyTest.add(checkBlockPos.getNext(ForgeDirection.EAST)))
							possibleBlocks.add(checkBlockPos.getNext(ForgeDirection.EAST));
						if (redundancyTest.add(checkBlockPos.getNext(ForgeDirection.WEST)))
							possibleBlocks.add(checkBlockPos.getNext(ForgeDirection.WEST));
					}
					
					if ((facing != ForgeDirection.UP) && (facing != ForgeDirection.DOWN))
					{
						if (redundancyTest.add(checkBlockPos.getNext(ForgeDirection.UP)))
							possibleBlocks.add(checkBlockPos.getNext(ForgeDirection.UP));
						if (redundancyTest.add(checkBlockPos.getNext(ForgeDirection.DOWN)))
							possibleBlocks.add(checkBlockPos.getNext(ForgeDirection.DOWN));
					}

					if ((facing != ForgeDirection.NORTH) && (facing != ForgeDirection.SOUTH))
					{
						if (redundancyTest.add(checkBlockPos.getNext(ForgeDirection.NORTH)))
							possibleBlocks.add(checkBlockPos.getNext(ForgeDirection.NORTH));
						if (redundancyTest.add(checkBlockPos.getNext(ForgeDirection.SOUTH)))
							possibleBlocks.add(checkBlockPos.getNext(ForgeDirection.SOUTH));
					}
				}
			}
		}
		if (hitTargets > 0)
			player.worldObj.playSoundAtEntity(player, "projecte:item.pepower", 1.0F, 0.80F + ((0.20F / (float)numCharges) * getCharge(itemStack)));
		PELogger.logInfo("Mercurial scan complete, " + hitTargets + "/" + testTargets + "," + possibleBlocks.size());
		return true;
	}
	
	/*
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

			Block newBlock = Block.getBlockFromItem(inventory[1].getItem());

			if (newBlock == Blocks.air)
			{
				return stack;
			}

			int newMeta = inventory[1].getItemDamage();

			double kleinEmc = ItemPE.getEmc(inventory[0]);
			int reqEmc = EMCHelper.getEmcValue(inventory[1]);

			byte charge = getCharge(stack);
			byte mode = this.getMode(stack);

			int facing = MathHelper.floor_double((double) ((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
			ForgeDirection dir = ForgeDirection.getOrientation(mop.sideHit);
			Vec3 look = player.getLookVec();

			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
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
				for (int x = (int) box.minX; x <= (int) box.maxX; x++)
				{
					for (int y = (int) box.minY; y <= (int) box.maxY; y++)
					{
						for (int z = (int) box.minZ; z <= (int) box.maxZ; z++)
						{
							Block oldBlock = world.getBlock(x, y, z);
							int oldMeta = oldBlock.getDamageValue(world, x, y, z);

							if (mode == NORMAL_MODE && oldBlock == Blocks.air)
							{
								if (kleinEmc < reqEmc)
									break;
								if (PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), x, y, z, newBlock, newMeta))
								{
									removeKleinEMC(stack, reqEmc);
									kleinEmc -= reqEmc;
								}
							}
							else if (mode == TRANSMUTATION_MODE)
							{
								if ((oldBlock == newBlock && oldMeta == newMeta) || oldBlock == Blocks.air || world.getTileEntity(x, y, z) != null || !EMCHelper.doesItemHaveEmc(new ItemStack(oldBlock, 1, oldMeta)))
								{
									continue;
								}

								int emc = EMCHelper.getEmcValue(new ItemStack(oldBlock, 1, oldMeta));

								if (emc > reqEmc)
								{
									if (PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), x, y, z, newBlock, newMeta))
									{
										int difference = emc - reqEmc;
										kleinEmc += MathHelper.clamp_double(kleinEmc, 0, EMCHelper.getKleinStarMaxEmc(inventory[0]));
										addKleinEMC(stack, difference);
									}
								}
								else if (emc < reqEmc)
								{
									int difference = reqEmc - emc;

									if (kleinEmc >= difference)
									{
										if (PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), x, y, z, newBlock, newMeta))
										{
											kleinEmc -= difference;
											removeKleinEMC(stack, difference);
										}
									}
								}
								else
								{
									PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), x, y, z, newBlock, newMeta);
								}
							}
						}
					}
				}
				player.worldObj.playSoundAtEntity(player, "projecte:item.pepower", 1.0F, 0.80F + ((0.20F / (float)numCharges) * charge));
			}
		}

		return stack;
	}
	*/

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

				double newEmc = MathHelper.clamp_double(tag.getDouble("StoredEMC") + amount, 0, EMCHelper.getKleinStarMaxEmc(kleinStar));

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
