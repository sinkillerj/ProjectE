package moze_intel.projecte.gameObjs.items.armor;

import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DMArmor extends ItemArmor implements ISpecialArmor
{
	public DMArmor(int armorType)
	{
		super(ArmorMaterial.DIAMOND, 0, armorType);
		this.setCreativeTab(ObjHandler.cTab);
		this.setUnlocalizedName("pe_dm_armor_"+armorType);
		this.setHasSubtypes(false);
		this.setMaxDamage(0);
	}
	
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) 
	{
		if (source.isExplosion())
		{
			return new ArmorProperties(1, 1.0D, 350);
		}

		if (slot == 0 && source == DamageSource.fall)
		{
			return new ArmorProperties(1, 1.0D, 5);
		}

		if (slot == 0 || slot == 3)
		{
			return new ArmorProperties(0, 0.2D, 100);
		}

		return new ArmorProperties(0, 0.3D, 150);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) 
	{
		if (slot == 0 || slot == 3)
		{
			return 4;
		}
		
		return 6;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) 
	{
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture (ItemStack stack, Entity entity, int slot, String type)
	{
		char index = this.armorType == 2 ? '2' : '1';
		return "projecte:textures/armor/darkmatter_"+index+".png";
	}
}
