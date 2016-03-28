package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.collect.Lists;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.List;

public class ArchangelSmite extends ItemPE implements IPedestalItem
{
	public ArchangelSmite()
	{
		this.setUnlocalizedName("archangel_smite");
		this.setMaxStackSize(1);
		this.setNoRepair();
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		EntityHomingArrow arrow = new EntityHomingArrow(world, player, 2.0F);

		if (!world.isRemote)
		{
			world.spawnEntityInWorld(arrow);
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
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
						EntityHomingArrow arrow = new EntityHomingArrow(world, FakePlayerFactory.getMinecraft(((WorldServer) world)), 2.0F);
						arrow.posX = tile.centeredX;
						arrow.posY = tile.centeredY + 2;
						arrow.posZ = tile.centeredZ;
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
