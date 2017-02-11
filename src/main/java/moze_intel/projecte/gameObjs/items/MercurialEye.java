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
import net.minecraft.entity.Entity;
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

public class MercurialEye extends ItemMode implements IExtraFunction
{
	public MercurialEye()
	{
		super("mercurial_eye", (byte)4, new String[] {"Extension", "Transmutation", "Pillar"});
		this.setNoRepair();
		MinecraftForge.EVENT_BUS.register(this);
	}
		
	final private int EXTENSION_MODE = 0;
	final private int TRANSMUTATION_MODE = 1;
	final private int PILLAR_MODE = 2;
	
	final private int PILLAR_STEP_RANGE = 3;

	private double kleinEmcCache;

	@SubscribeEvent
	public void onLeftClick(PlayerInteractEvent event)
	{		
		if(event.action == Action.LEFT_CLICK_BLOCK)
		{
			World world = event.world;
			EntityPlayer player = event.entityPlayer;
			ItemStack itemStack = player.getCurrentEquippedItem();
			if(itemStack != null && itemStack.getItem() == this)
			{
				MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
				if (mop == null)
					return;
				int side = mop.sideHit;
				byte mode = this.getMode(itemStack);
				
				formBlocks(itemStack, player, world, event.x, event.y, event.z, side, 1, mode);
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
			magnitude = ((charge+1)*PILLAR_STEP_RANGE);
		else
			magnitude = (charge+1) * (charge+1) * (charge+1);
		
		return formBlocks(itemStack, player, world, x, y, z, side, magnitude, mode);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
	
		if(world.isRemote)
			return itemStack;
		
		byte mode = this.getMode(itemStack);

		if (mode == PILLAR_MODE)
		{
			if(itemStack != null && itemStack.getItem() == this)
			{
				Vec3 eyeVec = Vec3.createVectorHelper(player.posX,player.posY + player.getEyeHeight(),player.posZ);
				Vec3 lookVec = player.getLookVec();
				//I'm not sure why there has to be a one point offset to the X coordinate here, but it's pretty consistent in testing.
				Vec3 targVec = eyeVec.addVector(lookVec.xCoord*2, lookVec.yCoord*2, lookVec.zCoord*2);
				
				formBlocks(itemStack, player, world, (int)targVec.xCoord, (int)targVec.yCoord, (int)targVec.zCoord, -1, 0, mode);
			}
		}

		return itemStack;
	}
	
	public boolean formBlocks(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, int magnitude, byte mode)
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

		kleinEmcCache = ItemPE.getEmc(inventory[0]);
		int newBlockEmc = EMCHelper.getEmcValue(inventory[1]);

	
		HashSet<BlockPosition> redundancyTest = new HashSet<BlockPosition>();
		LinkedList<BlockPosition> possibleBlocks = new LinkedList<BlockPosition>();
		
		Block startingBlock = world.getBlock(x, y, z);
		int startingBlockMeta = world.getBlockMetadata(x,y,z);
		if ((startingBlock == null) || (startingBlock == Blocks.bedrock))
			return false;
		int startingBlockEmc = 0;
		if((startingBlock != Blocks.air) && EMCHelper.doesItemHaveEmc(new ItemStack(startingBlock, 1, startingBlockMeta)))
			startingBlockEmc = EMCHelper.getEmcValue(new ItemStack(startingBlock, 1, startingBlockMeta));
		BlockPosition startingPos = new BlockPosition(x, y, z);
		ForgeDirection facing = ForgeDirection.getOrientation(side);
		
		blockShouldBeSearched(redundancyTest,possibleBlocks,startingPos);

		int hitTargets = 0;

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
			if (magnitude > 0)
			{
				magnitude--;
				switch(facing)
				{
					case UP:
						BBx1--;
						BBx2++;
						BBy2+= magnitude;
						BBz1--;
						BBz2++;
						break;
					
					case DOWN:
						BBx1--;
						BBx2++;
						BBy1-= magnitude;
						BBz1--;
						BBz2++;
						break;
				
					case EAST:
						BBx2+= magnitude;
						BBy1--;
						BBy2++;
						BBz1--;
						BBz2++;
						break;

					case WEST:
						BBx1-= magnitude;
						BBy1--;
						BBy2++;
						BBz1--;
						BBz2++;
						break;

					case SOUTH:
						BBx1--;
						BBx2++;
						BBy1--;
						BBy2++;
						BBz2+= magnitude;
						break;

					case NORTH:
						BBx1--;
						BBx2++;
						BBy1--;
						BBy2++;
						BBz1-= magnitude;
						break;
					
					default:
						break;
				}
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
							if (kleinEmcCache < newBlockEmc)
								break;
							
							//if ((entitiesInBox == null) || (entitiesInBox.size() == 0))
							if (!entitiesInBlock(world, startingBlock, px, py, pz))
							{
								Block placeBlock = world.getBlock(px, py, pz);
								int placeBlockMeta = world.getBlockMetadata(px,py,pz);
								int placeBlockEmc = 0;
								if((placeBlock != Blocks.air) && EMCHelper.doesItemHaveEmc(new ItemStack(placeBlock, 1, placeBlockMeta)))
									placeBlockEmc = EMCHelper.getEmcValue(new ItemStack(placeBlock, 1, placeBlockMeta));
								BlockPosition placeBlockPos = new BlockPosition(px, py, pz);
									
								if (processAdjustmentsForBlock(world, player, placeBlock, placeBlockMeta, placeBlockPos, newBlock, newBlockMeta, itemStack, placeBlockEmc, newBlockEmc) == true)
								{
									hitTargets++;
								}
							}
						}
					}
				}
			}
		}
		else
		{
			if (startingBlock == Blocks.air)
				return false;
			int testTargets = 0;
			int maxTargets = magnitude;

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
				int faceBlockMeta = world.getBlockMetadata(faceBlockPos.x,faceBlockPos.y,faceBlockPos.z);

				if(!faceBlock.isSideSolid(world, faceBlockPos.x, faceBlockPos.y, faceBlockPos.z, facing.getOpposite()))
				{
					if (mode == EXTENSION_MODE)
					{
						if (!entitiesInBlock(world, startingBlock, faceBlockPos.x, faceBlockPos.y, faceBlockPos.z))
						{
							int faceBlockEmc = 0;
							if((faceBlock != Blocks.air) && EMCHelper.doesItemHaveEmc(new ItemStack(faceBlock, 1, faceBlockMeta)))
								faceBlockEmc = EMCHelper.getEmcValue(new ItemStack(faceBlock, 1, faceBlockMeta));
							
							hit = processAdjustmentsForBlock(world, player, faceBlock, faceBlockMeta, faceBlockPos, newBlock, newBlockMeta, itemStack, faceBlockEmc, newBlockEmc);
						}
					}
					else if (mode == TRANSMUTATION_MODE)
					{
						hit = processAdjustmentsForBlock(world, player, checkBlock, checkBlockMeta, checkBlockPos, newBlock, newBlockMeta, itemStack, startingBlockEmc, newBlockEmc);
					}
				}
				
				if (hit == true)
				{
					hit = false;
					hitTargets++;
				
					if ((facing != ForgeDirection.EAST) && (facing != ForgeDirection.WEST))
					{
						blockShouldBeSearched(redundancyTest, possibleBlocks, checkBlockPos.getNext(ForgeDirection.EAST));
						blockShouldBeSearched(redundancyTest, possibleBlocks, checkBlockPos.getNext(ForgeDirection.WEST));
					}
					
					if ((facing != ForgeDirection.UP) && (facing != ForgeDirection.DOWN))
					{
						blockShouldBeSearched(redundancyTest, possibleBlocks, checkBlockPos.getNext(ForgeDirection.UP));
						blockShouldBeSearched(redundancyTest, possibleBlocks, checkBlockPos.getNext(ForgeDirection.DOWN));
					}

					if ((facing != ForgeDirection.NORTH) && (facing != ForgeDirection.SOUTH))
					{
						blockShouldBeSearched(redundancyTest, possibleBlocks, checkBlockPos.getNext(ForgeDirection.NORTH));
						blockShouldBeSearched(redundancyTest, possibleBlocks, checkBlockPos.getNext(ForgeDirection.SOUTH));
					}
				}
			}
		}
		if (hitTargets > 0)
			player.worldObj.playSoundAtEntity(player, "projecte:item.pepower", 1.0F, 0.80F + ((0.20F / (float)numCharges) * getCharge(itemStack)));
		return true;
	}
	
	private void blockShouldBeSearched(HashSet<BlockPosition> redundancyTest, LinkedList<BlockPosition> possibleBlocks, BlockPosition iBlockPos)
	{
		if (redundancyTest.add(iBlockPos))
			possibleBlocks.add(iBlockPos);
	}
	
	private boolean entitiesInBlock(World world, Block templateBlock, int x, int y, int z)
	{
		AxisAlignedBB blockBB = templateBlock.getCollisionBoundingBoxFromPool(world, x, y, z);
		List entitiesInBox;
		if (blockBB == null)
		{
			entitiesInBox = null;
		}
		else
		{
			entitiesInBox = world.getEntitiesWithinAABB(EntityLivingBase.class, blockBB);
		}
	
		return ((!(entitiesInBox == null)) && (entitiesInBox.size() > 0));
	}
	
	private boolean processAdjustmentsForBlock(World world, EntityPlayer player, Block oldBlock, int oldBlockMeta, BlockPosition placePos, Block newBlock, int newBlockMeta, ItemStack kleinStar, int oldEMC, int newEMC)
	{
		if ((kleinEmcCache - (newEMC - oldEMC)) < 0)
			return false;
			
		if (world.getTileEntity(placePos.x, placePos.y, placePos.z) != null)
			return false;
		if ((oldBlock.equals(newBlock)) && (oldBlockMeta == newBlockMeta))
			return false;
		List<ItemStack> drops = oldBlock.getDrops(world, placePos.x, placePos.y, placePos.z, oldBlockMeta, 0);
		if (PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) player), placePos.x, placePos.y, placePos.z, newBlock, newBlockMeta))
		{
			if (oldEMC == 0)
			{
				for(ItemStack drop : drops)
					world.spawnEntityInWorld(new EntityItem(world, placePos.x + 0.5, placePos.y + 0.5, placePos.z + 0.5, drop));
				removeKleinEMC(kleinStar, newEMC);
				return true;
			}
			else if (oldEMC > newEMC)
			{
				addKleinEMC(kleinStar, oldEMC-newEMC);
			}
			else if (oldEMC < newEMC)
			{
				removeKleinEMC(kleinStar, newEMC-oldEMC);
				return true;
			}
			else
			{
				return true;
			}
		}
		return false;
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

				kleinEmcCache = MathHelper.clamp_double(tag.getDouble("StoredEMC") + amount, 0, EMCHelper.getKleinStarMaxEmc(kleinStar));
				tag.setDouble("StoredEMC", kleinEmcCache);
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
				ItemStack kleinStar = ItemStack.loadItemStackFromNBT(nbt);

				NBTTagCompound tag = nbt.getCompoundTag("tag");
				
				kleinEmcCache = MathHelper.clamp_double(tag.getDouble("StoredEMC") - amount, 0, EMCHelper.getKleinStarMaxEmc(kleinStar));
				tag.setDouble("StoredEMC", kleinEmcCache);
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

}
