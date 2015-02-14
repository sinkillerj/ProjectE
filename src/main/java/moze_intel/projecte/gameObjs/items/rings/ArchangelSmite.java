package moze_intel.projecte.gameObjs.items.rings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class ArchangelSmite extends ItemPE implements IPedestalItem
{
	private int arrowCooldown;

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
		if (!world.isRemote)
		{
			if (arrowCooldown == 0)
			{
				for (int i = 0; i < 5; i++)
				{
					world.spawnEntityInWorld(new EntityHomingArrow(world, FakePlayerFactory.getMinecraft(((WorldServer) world)), 2.0F));
				}
				arrowCooldown = 100;
			}
			else
			{
				arrowCooldown--;
			}
		}
	}
}
