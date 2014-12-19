package moze_intel.projecte.gameObjs.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.IModeChanger;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.KeyBinds;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.Utils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class GemEternalDensity extends ItemPE implements IModeChanger, IBauble
{
	private final String[] targets = new String[] {"Iron", "Gold", "Diamond", "Dark Matter", "Red Matter"};
	
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
		
		if (world.isRemote || !(entity instanceof EntityPlayer))
		{
			return;
		}
		
		condense(stack, ((EntityPlayer) entity).inventory.mainInventory);
	}
	
	public static void condense(ItemStack gem, ItemStack[] inv)
	{
		if (gem.getItemDamage() == 0 || ItemPE.getEmc(gem) >= Constants.TILE_MAX_EMC)
		{
			return;
		}
		
		boolean isWhitelist = isWhitelistMode(gem);
		List<ItemStack> whitelist = getWhitelist(gem);
		
		ItemStack target = getTarget(gem);
		
		for (int i = 0; i < inv.length; i++)
		{
			ItemStack s = inv[i];
			
			if (s == null || !Utils.doesItemHaveEmc(s) || s.getMaxStackSize() == 1 || Utils.getEmcValue(s) >= Utils.getEmcValue(target))
			{
				continue;
			}
			
			if ((isWhitelist && listContains(whitelist, s)) || (!isWhitelist && !listContains(whitelist, s)))
			{
				ItemStack copy = s.copy();
				copy.stackSize = 1;
				
				addToList(gem, copy);
				
				inv[i].stackSize--;
				
				if (inv[i].stackSize <= 0)
				{
					inv[i] = null;
				}
				
				ItemPE.addEmc(gem, Utils.getEmcValue(copy));
				break;
			}
		}
		
		int value = Utils.getEmcValue(target);
		
		while (ItemPE.getEmc(gem) >= value)
		{
			ItemStack remain = Utils.pushStackInInv(inv, target);
			
			if (remain != null)
			{
				return;
			}
			
			ItemPE.removeEmc(gem, value);
			setItems(gem, new ArrayList<ItemStack>());
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (player.isSneaking())
			{
				if (stack.getItemDamage() == 1)
				{
					List<ItemStack> items = getItems(stack);
					
					if (!items.isEmpty())
					{
						EntityLootBall loot = new EntityLootBall(world, items, player.posX, player.posY, player.posZ);
						world.spawnEntityInWorld(loot);
						
						setItems(stack, new ArrayList<ItemStack>());
						ItemPE.setEmc(stack, 0);
					}
					
					stack.setItemDamage(0);
				}
				else
				{
					stack.setItemDamage(1);
				}
			}
			else
			{
				player.openGui(PECore.instance, Constants.ETERNAL_DENSITY_GUI, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			}
		}
		
		return stack;
	}
	
	private String getTargetDesciption(ItemStack stack)
	{
		return targets[stack.stackTagCompound.getByte("Target")];
	}
	
	private static ItemStack getTarget(ItemStack stack)
	{
		switch (stack.stackTagCompound.getByte("Target"))
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
				PELogger.logFatal("Invalid target for gem of eternal density: " + stack.stackTagCompound.getByte("Target"));
				return null;
		}
	}
	
	private static void setItems(ItemStack stack, List<ItemStack> list)
	{
		NBTTagList tList = new NBTTagList();
		
		for (ItemStack s : list)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			s.writeToNBT(nbt);
			tList.appendTag(nbt);
		}
		
		stack.stackTagCompound.setTag("Consumed", tList);
	}
	
	private static List<ItemStack> getItems(ItemStack stack)
	{
		List<ItemStack> list = new ArrayList<ItemStack>();
		NBTTagList tList = stack.stackTagCompound.getTagList("Consumed", NBT.TAG_COMPOUND);
		
		for (int i = 0; i < tList.tagCount(); i++)
		{
			list.add(ItemStack.loadItemStackFromNBT(tList.getCompoundTagAt(i)));
		}
		
		return list;
	}
	
	private static void addToList(ItemStack gem, ItemStack stack)
	{
		List<ItemStack> list = getItems(gem);
		
		addToList(list, stack);
		
		setItems(gem, list);
	}
	
	private static void addToList(List<ItemStack> list, ItemStack stack)
	{
		boolean hasFound = false;
		
		for (ItemStack s : list)
		{
			if (s.stackSize < s.getMaxStackSize() && Utils.areItemStacksEqual(s, stack))
			{
				int remain = s.getMaxStackSize() - s.stackSize;
				
				if (stack.stackSize <= remain)
				{
					s.stackSize += stack.stackSize;
					hasFound = true;
					break;
				}
				else
				{
					s.stackSize += remain;
					stack.stackSize -= remain;
				}
			}
		}
		
		if (!hasFound)
		{
			list.add(stack);
		}
	}
	
	private static boolean isWhitelistMode(ItemStack stack)
	{
		return stack.stackTagCompound.getBoolean("Whitelist");
	}
	
	private static List<ItemStack> getWhitelist(ItemStack stack)
	{
		List<ItemStack> result = new ArrayList<ItemStack>();
		NBTTagList list = stack.stackTagCompound.getTagList("Items", NBT.TAG_COMPOUND);
		
		for (int i = 0; i < list.tagCount(); i++)
		{
			result.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
		}
		
		return result;
	}
	
	private static boolean listContains(List<ItemStack> list, ItemStack stack)
	{
		for (ItemStack s : list)
		{
			if (Utils.areItemStacksEqual(s, stack))
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public byte getMode(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			return stack.stackTagCompound.getByte("Target");
		}

		return 0;
	}

	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		byte oldMode = getMode(stack);

		if (oldMode == 4)
		{
			stack.stackTagCompound.setByte("Target", (byte) 0);
		}
		else
		{
			stack.stackTagCompound.setByte("Target", (byte) (oldMode + 1));
		}

		player.addChatComponentMessage(new ChatComponentText("Set target to: " + getTargetDesciption(stack)));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) 
	{
		list.add("Condenses items on the go.");
		
		if (stack.hasTagCompound())
		{
			list.add("Current target: " + getTargetDesciption(stack));
		}
		
		if (KeyBinds.getModeKeyCode() >= 0 && KeyBinds.getModeKeyCode() < Keyboard.getKeyCount())
		{
			list.add("Press " + Keyboard.getKeyName(KeyBinds.getModeKeyCode()) + " to change target");
		}
		
		list.add("Right click to set up the whitelist/blacklist");
		list.add("Shift right click to activate/deactivate");
	}

	@Override
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
	
	@Override
	@Optional.Method(modid = "Baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.RING;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase player) 
	{
		this.onUpdate(stack, player.worldObj, player, 0, false);
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}
}
