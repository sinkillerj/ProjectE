package moze_intel.projecte.gameObjs.items.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EnumArmorType;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;

import java.util.Locale;

public class DMArmor extends ItemArmor implements ISpecialArmor
{
	private final EnumArmorType armorPiece;
	public DMArmor(EnumArmorType armorPiece)
	{
		super(ArmorMaterial.DIAMOND, 0, armorPiece.ordinal());
		this.setCreativeTab(ObjHandler.cTab);
		this.setUnlocalizedName("pe_dm_armor_" + armorPiece.ordinal());
		this.setHasSubtypes(false);
		this.setMaxDamage(0);
		this.armorPiece = armorPiece;
		this.setMaxDamage(0);
	}
	
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) 
	{
		EnumArmorType type = ((DMArmor) armor.getItem()).armorPiece;
		if (source.isExplosion())
		{
			return new ArmorProperties(1, 1.0D, 350);
		}

		if (type == EnumArmorType.HEAD && source == DamageSource.fall)
		{
			return new ArmorProperties(1, 1.0D, 5);
		}

		if (type == EnumArmorType.HEAD || type == EnumArmorType.FEET)
		{
			return new ArmorProperties(0, 0.2D, 100);
		}

		return new ArmorProperties(0, 0.3D, 150);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) 
	{
		EnumArmorType type = ((DMArmor) armor.getItem()).armorPiece;
		return (type == EnumArmorType.HEAD || type == EnumArmorType.FEET) ? 4 : 6;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons (IIconRegister par1IconRegister)
	{
		String type = this.armorPiece.name.toLowerCase(Locale.ROOT);
		
		this.itemIcon = par1IconRegister.registerIcon("projecte:dm_armor/" + type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture (ItemStack stack, Entity entity, int slot, String type)
	{
		char index = this.armorPiece == EnumArmorType.LEGS ? '2' : '1';
		return "projecte:textures/armor/darkmatter_"+index+".png";
	}
}
