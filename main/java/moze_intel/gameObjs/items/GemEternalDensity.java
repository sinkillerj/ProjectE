package moze_intel.gameObjs.items;

import java.util.ArrayList;
import java.util.List;

import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.entity.LootBall;
import moze_intel.utils.Utils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GemEternalDensity extends ItemBase implements IItemModeChanger
{
	private final String[] targets = new String[] {"Iron", "Gold", "Diamond", "Dark Matter", "Red Matter"};
	private final int[] emcChain = new int[] {256, 2048, 8192, 139264, 466944};
	
	@SideOnly(Side.CLIENT)
	private IIcon gemOff;
	@SideOnly(Side.CLIENT)
	private IIcon gemOn;
	
	public GemEternalDensity()
	{
		this.setUnlocalizedName("gem_density");
		this.setMaxStackSize(1);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) 
	{
		if (!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		
		if (world.isRemote || !(entity instanceof EntityPlayer) || stack.getItemDamage() == 0)
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		IInventory inv = player.inventory;
		
		for (int i = 0; i < 36; i++)
		{
			if (i == slot)
			{
				continue;
			}
			
			ItemStack current = inv.getStackInSlot(i);
			
			if (current == null || Utils.areItemStacksEqual(Utils.getNormalizedStack(current), getItemStackTarget(getTarget(stack))))
			{
				continue;
			}
					
			if (Utils.doesItemHaveEmc(current) && current.getMaxStackSize() > 1)
			{
				consumeItem(stack, current, inv, i);
				break;
			}
		}
		
		checkEmcBounds(stack, inv);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (stack.getItemDamage() == 0)
			{
				stack.setItemDamage(1);
				playChargeSound(player);
			}
			else
			{
				stack.setItemDamage(0);
				playUnChargeSound(player);
				onGemUncharge(player, stack);
			}
		}
		return stack;
	}
	
	private void onGemUncharge(EntityPlayer player, ItemStack stack)
	{
		List<ItemStack> remaining = new ArrayList();
		NBTTagList list = getTagList(stack);
		
		for (int i = 0; i < list.tagCount(); i++)
		{
			ItemStack s = Utils.pushStackInInv(player.inventory, ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
			
			if (s != null)
			{
				remaining.add(s);
			}
		}
		
		if (!remaining.isEmpty())
		{
			World world = player.worldObj;
			world.spawnEntityInWorld(new LootBall(world, remaining, player.posX, player.posY, player.posZ));
		}
		
		clearItemBuffer(stack);
		this.setEmc(stack, 0);
	}
	
	public void checkEmcBounds(ItemStack gem, IInventory inv)
	{
		int reqEmc = emcChain[this.getTarget(gem)];
		
		if (this.getEmc(gem) >= reqEmc)
		{
			ItemStack remain = Utils.pushStackInInv(inv, getItemStackTarget(gem));
			
			if (remain != null)
			{
				gem.setItemDamage(0);
			}
			
			this.removeEmc(gem, reqEmc);
			clearItemBuffer(gem);
		}
	}
	
	public void consumeItem(ItemStack gem, ItemStack stack, IInventory inv, int slotIndex)
	{
		int itemEmc = Utils.getEmcValue(stack);
		inv.decrStackSize(slotIndex, 1);
		this.addEmc(gem, itemEmc);
		addStackToBuffer(gem, Utils.getNormalizedStack(stack));
	}
	
	private ItemStack getItemStackTarget(ItemStack stack)
	{
		return getItemStackTarget(getTarget(stack));
	}
	
	public ItemStack getItemStackTarget(byte target)
	{
		switch (target)
		{
			case 0:
				return new ItemStack(Items.iron_ingot);
			case 1:
				return new ItemStack(Items.gold_ingot);
			case 2:
				return new ItemStack(Items.diamond);
			case 3:
				return new ItemStack(ObjHandler.matter, 1, 0);
			case 4:
				return new ItemStack(ObjHandler.matter, 1, 1);
			default:
				return null;
		}
	}
	
	private void clearItemBuffer(ItemStack stack)
	{
		stack.stackTagCompound.setTag("Item Buffer", new NBTTagList());
	}
	
	private void addStackToBuffer(ItemStack gem, ItemStack stack)
	{
		NBTTagList nbtList = getTagList(gem);
		NBTTagCompound nbt = new NBTTagCompound();
		stack.writeToNBT(nbt);
		nbtList.appendTag(nbt);
		gem.stackTagCompound.setTag("Item Buffer", nbtList);
	}
	
	private NBTTagList getTagList(ItemStack stack)
	{
		return stack.stackTagCompound.getTagList("Item Buffer", NBT.TAG_COMPOUND);
	}
	
	private void playChargeSound(EntityPlayer player)
	{
		player.worldObj.playSoundAtEntity(player, "projecte:heal", 0.8F, 1.0F / (Item.itemRand.nextFloat() * 0.4F + 0.8F));
	}
	
	private void playUnChargeSound(EntityPlayer player)
	{
		player.worldObj.playSoundAtEntity(player, "projecte:break", 0.8F, 1.0F / (Item.itemRand.nextFloat() * 0.4F + 0.8F));
	}
	
	private void changeTarget(ItemStack stack)
	{
		byte current = stack.stackTagCompound.getByte("Target");
		
		if (current == 4)
		{
			stack.stackTagCompound.setByte("Target", (byte) 0);
		}
		else
		{
			stack.stackTagCompound.setByte("Target", (byte) (current + 1));
		}
	}
	
	public byte getTarget(ItemStack stack)
	{
		return stack.stackTagCompound.getByte("Target");
	}
	
	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		changeTarget(stack);
		player.addChatComponentMessage(new ChatComponentText("Set target to: "+targets[getTarget(stack)]));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) 
	{
		if (stack.hasTagCompound())
		{
			byte target = getTarget(stack);
			list.add("Target: "+EnumChatFormatting.AQUA+targets[target]);
			list.add("Required EMC: "+String.format("%,d",emcChain[target]));
		}
	}
	
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int dmg)
    {
		return dmg == 0 ? gemOff : gemOn;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		gemOn = register.registerIcon(this.getTexture("dense_gem_on"));
		gemOff = register.registerIcon(this.getTexture("dense_gem_off"));
	}
}
