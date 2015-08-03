package moze_intel.projecte.gameObjs.items.rings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public abstract class RingToggle extends ItemPE implements IModeChanger
{
	private String name;
	@SideOnly(Side.CLIENT)
	private IIcon ringOn;
	@SideOnly(Side.CLIENT)
	private IIcon ringOff;
	
	public RingToggle(String unlocalName)
	{
		name = unlocalName;
		this.setUnlocalizedName(unlocalName);
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
		}
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int dmg)
	{
		return dmg == 0 ? ringOff : ringOn;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		ringOn = register.registerIcon(this.getTexture("rings", name+"_on"));
		ringOff = register.registerIcon(this.getTexture("rings", name+"_off"));
	}

	@Override
	public byte getMode(ItemStack stack)
	{
		return (byte) stack.getItemDamage();
	}

	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			player.worldObj.playSoundAtEntity(player, "projecte:item.peheal", 1.0F, 1.0F);
			stack.setItemDamage(1);
		}
		else
		{
			player.worldObj.playSoundAtEntity(player, "projecte:item.peuncharge", 1.0F, 1.0F);
			stack.setItemDamage(0);
		}
	}
}
