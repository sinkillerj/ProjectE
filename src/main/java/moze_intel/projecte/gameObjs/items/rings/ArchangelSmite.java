package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
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
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		EntityHomingArrow arrow = new EntityHomingArrow(world, player, 2.0F);

		if (!world.isRemote)
		{
			world.spawnEntityInWorld(arrow);
		}

		return stack;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("rings", "archangel_smite"));
	}

	@Override
	public void updateInPedestal(World world, int x, int y, int z)
	{
		if (!world.isRemote && ProjectEConfig.archangelPedCooldown != -1)
		{
			DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(x, y, z));
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
			list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("pe.archangel.pedestal1"));
			list.add(EnumChatFormatting.BLUE + String.format(
					StatCollector.translateToLocal("pe.archangel.pedestal2"), MathUtils.tickToSecFormatted(ProjectEConfig.archangelPedCooldown)));
		}
		return list;
	}
}
