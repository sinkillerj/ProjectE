package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.collect.Lists;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ArchangelSmite extends RingToggle implements IPedestalItem, IModeChanger
{
	public ArchangelSmite()
	{
		super("archangel_smite");
		this.setMaxStackSize(1);
		this.setNoRepair();
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5)
	{
		if (!world.isRemote && getMode(stack) == 1 && entity instanceof EntityLivingBase)
		{
			fireArrow(stack, world, ((EntityLivingBase) entity));
		}
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (!world.isRemote)
		{
			fireArrow(stack, world, player);
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	private void fireArrow(ItemStack ring, World world, EntityLivingBase shooter)
	{
		EntityHomingArrow arrow = new EntityHomingArrow(world, shooter, 2.0F);

		if (!(shooter instanceof EntityPlayer) || consumeFuel(((EntityPlayer) shooter), ring, EMCHelper.getEmcValue(Items.ARROW), true))
		{
			world.playSound(null, shooter.posX, shooter.posY, shooter.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);
			world.spawnEntityInWorld(arrow);
		}
	}

	@Override
	public void updateInPedestal(World world, BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.archangelPedCooldown != -1)
		{
			DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(pos));
			if (tile.getActivityCooldown() == 0)
			{
				if (!world.getEntitiesWithinAABB(EntityLiving.class, tile.getEffectBounds()).isEmpty())
				{
					for (int i = 0; i < 3; i++)
					{
						EntityHomingArrow arrow = new EntityHomingArrow(world, FakePlayerFactory.get(((WorldServer) world), PECore.FAKEPLAYER_GAMEPROFILE), 2.0F);
						arrow.posX = tile.centeredX;
						arrow.posY = tile.centeredY + 2;
						arrow.posZ = tile.centeredZ;
						arrow.motionX = 0;
						arrow.motionZ = 0;
						arrow.motionY = 1;
						arrow.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);
						world.spawnEntityInWorld(arrow);
					}
				}
				tile.setActivityCooldown(ProjectEConfig.archangelPedCooldown);
			}
			else
			{
				tile.decrementActivityCooldown();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = Lists.newArrayList();
		if (ProjectEConfig.archangelPedCooldown != -1) {
			list.add(TextFormatting.BLUE + I18n.translateToLocal("pe.archangel.pedestal1"));
			list.add(TextFormatting.BLUE + String.format(
					I18n.translateToLocal("pe.archangel.pedestal2"), MathUtils.tickToSecFormatted(ProjectEConfig.archangelPedCooldown)));
		}
		return list;
	}
}
