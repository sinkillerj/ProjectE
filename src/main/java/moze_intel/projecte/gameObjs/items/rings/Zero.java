package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Lists;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.ItemCharge;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class Zero extends ItemCharge implements IModeChanger, IBauble, IPedestalItem
{
	public Zero() 
	{
		super("zero_ring", (byte)4);
		this.setContainerItem(this);
		this.setNoRepair();
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		super.onUpdate(stack, world, entity, par4, par5);
		
		if (world.isRemote || !(entity instanceof EntityPlayer) || par4 > 8 || stack.getItemDamage() == 0)
		{
			return;
		}

		AxisAlignedBB box = new AxisAlignedBB(entity.posX - 3, entity.posY - 3, entity.posZ - 3, entity.posX + 3, entity.posY + 3, entity.posZ + 3);
		WorldHelper.freezeInBoundingBox(world, box, ((EntityPlayer) entity), true);
	}


	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (!world.isRemote)
		{
			int offset = 3 + this.getCharge(stack);
			AxisAlignedBB box = player.getEntityBoundingBox().expand(offset, offset, offset);
			world.playSound(null, player.posX, player.posY, player.posZ, PESounds.POWER, SoundCategory.PLAYERS, 1.0F, 1.0F);
			WorldHelper.freezeInBoundingBox(world, box, player, false);
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public byte getMode(@Nonnull ItemStack stack)
	{
		return (byte) stack.getItemDamage();
	}

	@Override
	public boolean changeMode(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
		stack.setItemDamage(stack.getItemDamage() == 0 ? 1 : 0);
		return true;
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
		this.onUpdate(stack, player.getEntityWorld(), player, 0, false);
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

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.zeroPedCooldown != -1)
		{
			DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(pos));
			if (tile.getActivityCooldown() == 0) {
				AxisAlignedBB aabb = tile.getEffectBounds();
				WorldHelper.freezeInBoundingBox(world, aabb, null, false);
				List<Entity> list = world.getEntitiesWithinAABB(Entity.class, aabb);
				for (Entity ent : list)
				{
					if (ent.isBurning())
					{
						ent.extinguish();
					}
				}
				tile.setActivityCooldown(ProjectEConfig.zeroPedCooldown);
			}
			else
			{
				tile.decrementActivityCooldown();
			}
		}
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = Lists.newArrayList();
		if (ProjectEConfig.zeroPedCooldown != -1) {
			list.add(TextFormatting.BLUE + I18n.format("pe.zero.pedestal1"));
			list.add(TextFormatting.BLUE + I18n.format("pe.zero.pedestal2"));
			list.add(TextFormatting.BLUE + I18n.format("pe.zero.pedestal3", MathUtils.tickToSecFormatted(ProjectEConfig.zeroPedCooldown)));
		}
		return list;
	}
}
