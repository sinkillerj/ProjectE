package moze_intel.projecte.utils;

import com.google.common.collect.Lists;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public final class NBTWhitelist
{
	private static final List<SimpleStack> LIST = Lists.newArrayList();
	
	public static void init(){
		IBlacklistProxy blacklistProxy = ProjectEAPI.getBlacklistProxy();
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.kleinStars, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.repairTalisman, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.philosStone, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.dmPick, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.dmAxe, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.dmShovel, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.dmSword, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.dmHoe, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.dmShears, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.dmHammer, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.rmPick, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.rmAxe, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.rmShovel, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.rmSword, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.rmHoe, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.rmShears, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.rmHammer, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.rmKatar, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.rmStar, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.zero, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.timeWatch, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.mercEye, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.dCatalyst, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.hyperLens, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.cataliticLens, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.blackHole, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.angelSmite, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.harvestGod, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.ignition, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.swrg, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.eternalDensity, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.voidRing, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.arcana, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.bodyStone, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.soulStone, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.lifeStone, 1));
		blacklistProxy.whitelistNBT(new ItemStack(ObjHandler.mindStone, 1));
	}

	public static boolean register(ItemStack stack)
	{
		SimpleStack s = new SimpleStack(stack);

		if (!s.isValid())
		{
			return false;
		}

		s.qnty = 1;
		s.damage = OreDictionary.WILDCARD_VALUE;

		if (!LIST.contains(s))
		{
			LIST.add(s);
			return true;
		}

		return false;
	}

	public static boolean shouldDupeWithoutNBT(ItemStack stack)
	{
		SimpleStack s = new SimpleStack(stack);

		if (!s.isValid())
		{
			return false;
		}

		return LIST.contains(s);
	}
	
	public static boolean shouldDupeWithoutNBT(SimpleStack stack)
	{
		SimpleStack s = stack.copy();
		if(!s.isValid())
		{
			return false;
		}
		s.nbt = null;
		return LIST.contains(s);
	}
}
