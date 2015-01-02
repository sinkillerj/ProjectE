package moze_intel.projecte.gameObjs.items;

import java.util.List;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
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
	private final char[] colorCodes = new char[] {'f', '6', 'd', '9', 'e', 'a', 'c', '8', '7', 'b', '5', '1', '6', '2', '4', '0'}; 
	
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
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (world.isRemote || !(entity instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		ItemStack[] inv = AlchemicalBags.get(player.getCommandSenderName(), (byte) stack.getItemDamage());
		
		if (Utils.invContainsItem(inv, new ItemStack(ObjHandler.blackHole, 1, 1)))
		{
			AxisAlignedBB bBox = player.boundingBox.expand(7, 7, 7);
			List<EntityItem> itemList = world.getEntitiesWithinAABB(EntityItem.class, bBox);
			
			for (EntityItem item : itemList)
			{
				item.delayBeforeCanPickup = 0;
				double d1 = (player.posX - item.posX);
				double d2 = (player.posY + (double)player.getEyeHeight() - item.posY);
				double d3 = (player.posZ - item.posZ);
				double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);

				item.motionX += d1 / d4 * 0.1D;
				item.motionY += d2 / d4 * 0.1D;
				item.motionZ += d3 / d4 * 0.1D;
					
				item.moveEntity(item.motionX, item.motionY, item.motionZ);
			}
			
			List<EntityLootBall> lootBallList = world.getEntitiesWithinAABB(EntityLootBall.class, bBox);
			
			for (EntityLootBall ball : lootBallList)
			{
				double d1 = (player.posX - ball.posX);
				double d2 = (player.posY + (double)player.getEyeHeight() - ball.posY);
				double d3 = (player.posZ - ball.posZ);
				double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);

				ball.motionX += d1 / d4 * 0.1D;
				ball.motionY += d2 / d4 * 0.1D;
				ball.motionZ += d3 / d4 * 0.1D;
					
				ball.moveEntity(ball.motionX, ball.motionY, ball.motionZ);
			}
		}
		
		ItemStack rTalisman = Utils.getStackFromInv(inv, new ItemStack(ObjHandler.repairTalisman));
		
		if (rTalisman != null)
		{
			byte coolDown = rTalisman.stackTagCompound.getByte("Cooldown");
			
			if (coolDown > 0)
			{
				rTalisman.stackTagCompound.setByte("Cooldown", (byte) (coolDown - 1));
			}
			else
			{
				boolean hasAction = false;
				
				for (int i = 0; i < inv.length; i++)
				{
					ItemStack invStack = inv[i];
				
					if (invStack == null || invStack.getItem() instanceof RingToggle) 
					{
						continue;
					}
				
					if (!invStack.getHasSubtypes() && invStack.getMaxDamage() != 0 && invStack.getItemDamage() > 0)
					{
						invStack.setItemDamage(invStack.getItemDamage() - 1);
						inv[i] = invStack;
						
						if (!hasAction)
						{
							hasAction = true;
						}
					}
				}
				
				if (hasAction)
				{
					rTalisman.stackTagCompound.setByte("Cooldown", (byte) 19);
				}
			}
		}
		
		if (player.openContainer instanceof AlchBagContainer)
		{
			ItemStack gemDensity = Utils.getStackFromInv(((AlchBagContainer) player.openContainer).inventory, new ItemStack(ObjHandler.eternalDensity, 1, 1));
			
			if (gemDensity != null)
			{
				GemEternalDensity.condense(gemDensity, ((AlchBagContainer) player.openContainer).inventory.getInventory());
			}
		}
		else
		{
			ItemStack gemDensity = Utils.getStackFromInv(inv, new ItemStack(ObjHandler.eternalDensity, 1, 1));
			
			if (gemDensity != null)
			{
				GemEternalDensity.condense(gemDensity, inv); 
		
				AlchemicalBags.set(entity.getCommandSenderName(), (byte) stack.getItemDamage(), inv);
				AlchemicalBags.sync(player);
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
		String color = " ("+"\u00a7"+colorCodes[i]+localizedColors[i]+"\u00a7"+colorCodes[0]+")";
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
