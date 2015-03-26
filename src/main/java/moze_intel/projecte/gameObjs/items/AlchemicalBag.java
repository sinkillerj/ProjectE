package moze_intel.projecte.gameObjs.items;

import java.util.List;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.IAlchBagItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.gameObjs.container.inventory.AlchBagInventory;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.gameObjs.items.rings.RingToggle;
import moze_intel.projecte.playerData.AlchemicalBags;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.Utils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AlchemicalBag extends ItemPE
{
	private final String[] colors = new String[] {"white", "orange", "magenta", "lightBlue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"};
	private final String[] localizedColors = new String[] {"White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Silver", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};
	
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	
	public AlchemicalBag()
	{
		this.setUnlocalizedName("alchemical_bag");
		this.hasSubtypes = true;
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			player.openGui(PECore.instance, Constants.ALCH_BAG_GUI, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
		
		return stack;
	}
	
	@Override
	public void onUpdate(ItemStack bag, World world, Entity entity, int par4, boolean par5)
	{
		if (!(entity instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		ItemStack[] invBag;
		ItemStack[] deepCopy;
		ItemStack tickedItem;
		for (int i = 0; i < 104; i++)
		{
			if (player.openContainer instanceof AlchBagContainer)
			{
				// Update inventory, then grab shallow copy of data from it
				AlchBagInventory abi = ((AlchBagContainer) player.openContainer).inventory;
				abi.refresh();
				invBag = ((AlchBagContainer) player.openContainer).inventory.getInventory();
			}
			else
			{
				// Grab latest shallow copy of data
				invBag = AlchemicalBags.get(player.getCommandSenderName(), ((byte) bag.getItemDamage()));
			}

			if (invBag[i] != null && invBag[i].getItem() instanceof IAlchBagItem)
			{
				deepCopy = Utils.deepCopyItemStackArr(invBag);
				tickedItem = deepCopy[i];
				// pass deep copy of state to bag to modify. This prevents weirdness when nulling stuff
				((IAlchBagItem) invBag[i].getItem()).updateInAlchBag(player, deepCopy, tickedItem);
				removeEmptyStacks(deepCopy);
				if (!world.isRemote) {
					AlchemicalBags.set(player.getCommandSenderName(), ((byte) bag.getItemDamage()), deepCopy);
					AlchemicalBags.sync(player);
				}
			}
		}
	}

	private void removeEmptyStacks(ItemStack[] deepCopy)
	{
		for (int i = 0; i < deepCopy.length; i++)
		{
			if (deepCopy[i] != null && deepCopy[i].stackSize <= 0)
			{
				deepCopy[i] = null;
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) 
	{
		return 1; 
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{	
		return super.getUnlocalizedName()+ "_" +colors[MathHelper.clamp_int(stack.getItemDamage(), 0, 15)];
	}
	
	public String getItemStackDisplayName(ItemStack stack)
	{
		String name = super.getItemStackDisplayName(stack);
		int i = stack.getItemDamage();
		String color = " ("+localizedColors[i]+")";
		return name + color;
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) 
	{
		super.onCreated(stack, world, player);
		
		if (!world.isRemote)
		{
			player.addStat(AchievementHandler.ALCH_BAG, 1);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs cTab, List list)
	{
		for (int i = 0; i < 16; ++i)
			list.add(new ItemStack(item, 1, i));
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1)
	{
		return icons[MathHelper.clamp_int(par1, 0, 15)];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		icons = new IIcon[16];
		
		for (int i = 0; i < 16; i++)
		{
			icons[i] = register.registerIcon(this.getTexture("alchemy_bags", colors[i]));
		}
	}
}
