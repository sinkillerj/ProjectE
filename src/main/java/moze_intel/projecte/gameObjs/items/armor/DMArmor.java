package moze_intel.projecte.gameObjs.items.armor;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class DMArmor extends ItemArmor
{
	public DMArmor(EntityEquipmentSlot armorPiece, Properties props)
	{
		// TODO 1.13 custom material?
		super(ArmorMaterial.DIAMOND, armorPiece, props);
	}
	
	/*@Override TODO 1.13
	public ArmorProperties getProperties(EntityLivingBase player, @Nonnull ItemStack armor, DamageSource source, double damage, int slot)
	{
		EntityEquipmentSlot type = ((DMArmor) armor.getItem()).armorType;
		if (source.isExplosion())
		{
			return new ArmorProperties(1, 1.0D, 350);
		}

		if (type == EntityEquipmentSlot.HEAD && source == DamageSource.FALL)
		{
			return new ArmorProperties(1, 1.0D, 5);
		}

		if (type == EntityEquipmentSlot.HEAD || type == EntityEquipmentSlot.FEET)
		{
			return new ArmorProperties(0, 0.2D, 100);
		}

		return new ArmorProperties(0, 0.3D, 150);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, @Nonnull ItemStack armor, int slot)
	{
		EntityEquipmentSlot type = ((DMArmor) armor.getItem()).armorType;
		return (type == EntityEquipmentSlot.HEAD || type == EntityEquipmentSlot.FEET) ? 4 : 6;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, @Nonnull ItemStack stack, DamageSource source, int damage, int slot) {}*/

	@Override
	@OnlyIn(Dist.CLIENT)
	public String getArmorTexture (ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
	{
		char index = this.armorType == EntityEquipmentSlot.LEGS ? '2' : '1';
		return PECore.MODID + ":textures/armor/darkmatter_"+index+".png";
	}
}
