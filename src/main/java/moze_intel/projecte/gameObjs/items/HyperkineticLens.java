package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.impl.ChargeableItem;
import moze_intel.projecte.impl.ChargeableProvider;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;

public class HyperkineticLens extends ItemPE implements IProjectileShooter
{
	public HyperkineticLens() 
	{
		this.setTranslationKey("hyperkinetic_lens");
		this.setMaxStackSize(1);
		this.setNoRepair();
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		
		if (!world.isRemote && shootProjectile(player, stack, hand))
		{
			PlayerHelper.swingItem(player, hand);
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound stackNbt)
	{
		return new ChargeableProvider(new ChargeableItem(stack, 3));
	}
	
	@Override
	public boolean shootProjectile(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
		World world = player.getEntityWorld();
		int charge = stack.getCapability(PECore.CHARGEABLE_CAP, null).getCharge();
		int requiredEmc = Constants.EXPLOSIVE_LENS_COST[charge];
		
		if (!consumeFuel(player, stack, requiredEmc, true))
		{
			return false;
		}

		world.playSound(null, player.posX, player.posY, player.posZ, PESounds.POWER, SoundCategory.PLAYERS, 1.0F, 1.0F);
		EntityLensProjectile ent = new EntityLensProjectile(world, player, charge);
		ent.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
		world.spawnEntity(ent);
		return true;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		int charge = stack.getCapability(PECore.CHARGEABLE_CAP, null).getCharge();
		int numCharge = stack.getCapability(PECore.CHARGEABLE_CAP, null).getNumCharges();
		return 1.0D - (double) charge / numCharge;
	}
}
