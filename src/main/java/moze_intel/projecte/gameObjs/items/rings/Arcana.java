package moze_intel.projecte.gameObjs.items.rings;

/*import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import moze_intel.projecte.api.IModeChanger;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.Utils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

//Need to finish implementation. Very WIP
public class Arcana extends ItemPE implements IModeChanger
{
	private final String[] MODES = new String[] {"Zero", "Ignition", "Harvest", "SWRG"};
	
	@SideOnly(Side.CLIENT)
	private IIcon[] icons = new IIcon[4];
	
	public Arcana()
	{
		super();
		this.setUnlocalizedName("arcana_ring");
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isHeld)
	{
		if (stack.stackTagCompound == null)
		{
			stack.stackTagCompound = new NBTTagCompound();
		}
		
		if (world.isRemote || slotIndex > 8 || !stack.getTagCompound().getBoolean("Active") || !(entity instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		EntityPlayerMP playerMP = (EntityPlayerMP) entity;
		
		if (player.capabilities.isCreativeMode)
		{
			return;
		}
		
		if (!playerMP.capabilities.allowFlying)
		{
			Utils.updateClientFlight(playerMP, true);
			PlayerChecksEvent.addPlayerFlyChecks(playerMP);
		}
		
		if (!player.isImmuneToFire())
		{
			Utils.setPlayerFireImmunity(player, true);
			PlayerChecksEvent.addPlayerFireChecks((EntityPlayerMP) player);
		}
		
		switch (stack.getItemDamage())
		{
			case 0:
				Utils.freezeNearby(world, entity);
				break;
			case 1:
				Utils.igniteNearby(world, entity);
				break;
			case 2:
				Utils.growNearbyRandomly(true, world, entity);
				break;
			case 3:
				Utils.repelEntities(player);
				break;
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (stack.getTagCompound().getBoolean("Active"))
			{
				stack.getTagCompound().setBoolean("Active", false);
			}
			else
			{
				stack.getTagCompound().setBoolean("Active", true);
			}
		}
		
		return stack;
	}
	

	@Override
	public void changeMode(EntityPlayer player, ItemStack stack) 
	{
		if (stack.getTagCompound().getBoolean("Active"))
		{
			int dmg = stack.getItemDamage();
			
			if (dmg < 3)
			{
				stack.setItemDamage(++dmg);
			}
			else
			{
				stack.setItemDamage(0);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int dmg)
	{
		return icons[MathHelper.clamp_int(dmg, 0, 4)];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		for (int i = 0; i < 4; i++)
		{
			icons[i] = register.registerIcon(this.getTexture("rings", "arcana_" + i));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		if (stack.hasTagCompound())
		{
			if (!stack.getTagCompound().getBoolean("Active"))
			{
				list.add(EnumChatFormatting.RED+"Not active!");
			}
			else
			{
				list.add("Mode: "+EnumChatFormatting.AQUA+MODES[stack.getItemDamage()]);
			}
		}
	}
}*/
