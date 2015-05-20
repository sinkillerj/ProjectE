package moze_intel.projecte.gameObjs.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.gameObjs.items.rings.RingToggle;
import moze_intel.projecte.playerData.AlchemicalBags;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class AlchemicalBag extends ItemPE
{
	private final String[] colors = new String[] {"white", "orange", "magenta", "lightBlue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"};

	// MC Lang files have these unlocalized names mapped to raw color names
	private final String[] unlocalizedColors = new String[] {
			"item.fireworksCharge.white", "item.fireworksCharge.orange",
			"item.fireworksCharge.magenta", "item.fireworksCharge.lightBlue",
			"item.fireworksCharge.yellow", "item.fireworksCharge.lime",
			"item.fireworksCharge.pink", "item.fireworksCharge.gray",
			"item.fireworksCharge.silver", "item.fireworksCharge.cyan",
			"item.fireworksCharge.purple", "item.fireworksCharge.blue",
			"item.fireworksCharge.brown", "item.fireworksCharge.green",
			"item.fireworksCharge.red", "item.fireworksCharge.black"};
	
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
		if (!(entity instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		ItemStack[] inv = AlchemicalBags.get(player.getName(), (byte) stack.getItemDamage());
		
		if (ItemHelper.invContainsItem(inv, new ItemStack(ObjHandler.blackHole, 1, 1)))
		{
			AxisAlignedBB bBox = player.getEntityBoundingBox().expand(7, 7, 7);
			List<EntityItem> itemList = world.getEntitiesWithinAABB(EntityItem.class, bBox);
			
			for (EntityItem item : itemList)
			{
				item.delayBeforeCanPickup = 0;
				WorldHelper.gravitateEntityTowards(item, player.posX, player.posY, player.posZ);
			}
			
			List<EntityLootBall> lootBallList = world.getEntitiesWithinAABB(EntityLootBall.class, bBox);
			
			for (EntityLootBall ball : lootBallList)
			{
				WorldHelper.gravitateEntityTowards(ball, player.posX, player.posY, player.posZ);
			}
		}

		if (world.isRemote)
		{
			return;
		}

		ItemStack rTalisman = ItemHelper.getStackFromInv(inv, new ItemStack(ObjHandler.repairTalisman));
		
		if (rTalisman != null)
		{
			byte coolDown = rTalisman.getTagCompound().getByte("Cooldown");
			
			if (coolDown > 0)
			{
				rTalisman.getTagCompound().setByte("Cooldown", (byte) (coolDown - 1));
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
					rTalisman.getTagCompound().setByte("Cooldown", (byte) 19);
				}
			}
		}
		
		if (player.openContainer instanceof AlchBagContainer)
		{
			ItemStack gemDensity = ItemHelper.getStackFromInv(((AlchBagContainer) player.openContainer).inventory, new ItemStack(ObjHandler.eternalDensity, 1, 1));
			
			if (gemDensity != null)
			{
				GemEternalDensity.condense(gemDensity, ((AlchBagContainer) player.openContainer).inventory.getInventory());
			}
		}
		else
		{
			ItemStack gemDensity = ItemHelper.getStackFromInv(inv, new ItemStack(ObjHandler.eternalDensity, 1, 1));
			
			if (gemDensity != null)
			{
				GemEternalDensity.condense(gemDensity, inv); 
		
				AlchemicalBags.set(entity.getName(), (byte) stack.getItemDamage(), inv);
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
		String color = " (" + StatCollector.translateToLocal(unlocalizedColors[i]) + ")";
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

	public static ItemStack getFirstBagItem(EntityPlayer player, ItemStack[] inventory)
	{
		for (ItemStack stack : inventory)
		{
			if (stack == null)
			{
				continue;
			}

			if (stack.getItem() == ObjHandler.alchBag && ItemHelper.invContainsItem(AlchemicalBags.get(player.getName(), (byte) stack.getItemDamage()), new ItemStack(ObjHandler.blackHole, 1, 1)))
			{
				return stack;
			}
		}

		return null;
	}
}
