package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.rings.BlackHoleBand;
import moze_intel.projecte.gameObjs.items.rings.VoidRing;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class AlchemicalBag extends ItemPE
{
	// MC Lang files have these unlocalized names mapped to raw color names
	private final String[] unlocalizedColors = {
			"item.fireworksCharge.white", "item.fireworksCharge.orange",
			"item.fireworksCharge.magenta", "item.fireworksCharge.lightBlue",
			"item.fireworksCharge.yellow", "item.fireworksCharge.lime",
			"item.fireworksCharge.pink", "item.fireworksCharge.gray",
			"item.fireworksCharge.silver", "item.fireworksCharge.cyan",
			"item.fireworksCharge.purple", "item.fireworksCharge.blue",
			"item.fireworksCharge.brown", "item.fireworksCharge.green",
			"item.fireworksCharge.red", "item.fireworksCharge.black"
	};
	
	public AlchemicalBag()
	{
		this.setTranslationKey("alchemical_bag");
		this.hasSubtypes = true;
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		if (!world.isRemote)
		{
			player.openGui(PECore.instance, Constants.ALCH_BAG_GUI, world, hand.ordinal(), -1, -1);
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Nonnull
	@Override
	public String getItemStackDisplayName(@Nonnull ItemStack stack)
	{
		String name = super.getItemStackDisplayName(stack);
		int i = stack.getItemDamage();

		if (stack.getItemDamage() > 15)
		{
			return name + " (" + I18n.translateToLocal("pe.debug.metainvalid.name") + ")";
		}

		String color = " (" + I18n.translateToLocal(unlocalizedColors[i]) + ")";
		return name + color;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs cTab, NonNullList<ItemStack> list)
	{
		if (isInCreativeTab(cTab))
		{
			for (int i = 0; i < 16; ++i)
				list.add(new ItemStack(this, 1, i));
		}
	}

	public static ItemStack getFirstBagWithSuctionItem(EntityPlayer player, NonNullList<ItemStack> inventory)
	{
		for (ItemStack stack : inventory)
		{
			if (stack.isEmpty())
			{
				continue;
			}

			if (stack.getItem() == ObjHandler.alchBag)
			{
				IItemHandler inv = player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY, null)
						.getBag(EnumDyeColor.byMetadata(stack.getItemDamage()));
				for (int i = 0; i < inv.getSlots(); i++) {
					ItemStack ring = inv.getStackInSlot(i);

					if (!ring.isEmpty() && (ring.getItem() instanceof BlackHoleBand || ring.getItem() instanceof VoidRing)) {
						if (ItemHelper.getOrCreateCompound(ring).getBoolean(TAG_ACTIVE)) {
							return stack;
						}
					}
				}
			}
		}

		return ItemStack.EMPTY;
	}
}
