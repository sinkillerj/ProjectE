package moze_intel.projecte.gameObjs.entity;

import com.google.common.collect.Lists;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

// todo remove in future
public class EntityLootBall extends Entity
{
	private List<ItemStack> items;
	private int age;

	public EntityLootBall(World world)
	{
		super(world);
		this.setSize(0.25F, 0.25F);
	}
	
	public EntityLootBall(World world, ItemStack[] drops, double x, double y, double z)
	{
		this(world, Arrays.asList(drops), x, y, z);
	}
	
	public EntityLootBall(World world, List<ItemStack> drops, double x, double y, double z)
	{
		super(world);
		items = drops;
		
		this.setSize(0.25F, 0.25F);
		this.setPosition(x, y, z);
		this.motionX = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
		this.motionY = 0.20000000298023224D;
		this.motionZ = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
		ItemHelper.compactItemList(items);
	}
	
	public List<ItemStack> getItemList()
	{
		return items;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= 0.03999999910593033D;
		this.noClip = this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		boolean flag = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;

		if (flag || this.ticksExisted % 25 == 0)
		{
			if (this.worldObj.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA)
			{
				this.motionY = 0.20000000298023224D;
				this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
				this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
				this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
			}
		}

		float f = 0.98F;

		if (this.onGround)
		{
			f = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.98F;
		}

		this.motionX *= (double)f;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= (double)f;

		if (this.onGround)
		{
			this.motionY *= -0.5D;
		}

		++this.age;

		if (!this.worldObj.isRemote)
		{
			for (ItemStack s : items)
			{
				WorldHelper.spawnEntityItem(worldObj, s, posX, posY, posZ);
			}
		}
	}
	
	@Override
	public boolean handleWaterMovement()
	{
		return this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.WATER, this);
	}

	@Override
	protected void readEntityFromNBT(@Nonnull NBTTagCompound nbt)
	{
		age = nbt.getShort("Age");
		items = Lists.newArrayList();
		
		NBTTagList list = nbt.getTagList("Items", 10);
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound listTag = list.getCompoundTagAt(i);
			items.add(ItemStack.loadItemStackFromNBT(listTag));
		}
	}

	@Override
	protected void writeEntityToNBT(@Nonnull NBTTagCompound nbt)
	{
		nbt.setShort("Age", (short)this.age);
		
		NBTTagList list = new NBTTagList();

		for (ItemStack item : items)
		{
			NBTTagCompound subNBT = new NBTTagCompound();
			item.writeToNBT(subNBT);
			list.appendTag(subNBT);
		}

		nbt.setTag("Items", list);
	}
	
	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	protected void entityInit() {}

	public void setItemList(List<ItemStack> itemList)
	{
		this.items = itemList;
	}
}
