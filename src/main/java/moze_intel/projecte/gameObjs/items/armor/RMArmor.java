package moze_intel.projecte.gameObjs.items.armor;

import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;

import javax.annotation.Nonnull;

@Optional.InterfaceList(value = {@Optional.Interface(iface = "thaumcraft.api.items.IRevealer", modid = "Thaumcraft"), @Optional.Interface(iface = "thaumcraft.api.items.IGoggles", modid = "Thaumcraft")})
public class RMArmor extends ItemArmor implements ISpecialArmor, IRevealer, IGoggles
{
	private final EntityEquipmentSlot armorPiece;
	public RMArmor(EntityEquipmentSlot armorType)
	{
		super(ArmorMaterial.DIAMOND, 0, armorType);
		this.setCreativeTab(ObjHandler.cTab);
		this.setUnlocalizedName("pe_rm_armor_" + armorType.getIndex());
		this.setHasSubtypes(false);
		this.setMaxDamage(0);
		this.armorPiece = armorType;
	}
	
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) 
	{
		EntityEquipmentSlot type = ((RMArmor) armor.getItem()).armorPiece;
		if (source.isExplosion())
		{
			return new ArmorProperties(1, 1.0D, 500);
		}

		if (type == EntityEquipmentSlot.HEAD && source == DamageSource.fall)
		{
			return new ArmorProperties(1, 1.0D, 10);
		}
		
		if (type == EntityEquipmentSlot.HEAD || type == EntityEquipmentSlot.FEET)
		{
			return new ArmorProperties(0, 0.2D, 250);
		}
		
		return new ArmorProperties(0, 0.3D, 350);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) 
	{
		EntityEquipmentSlot type = ((RMArmor) armor.getItem()).armorPiece;
		return (type == EntityEquipmentSlot.HEAD || type == EntityEquipmentSlot.FEET) ? 4 : 6;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture (ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
	{
		char index = this.armorPiece == EntityEquipmentSlot.LEGS ? '2' : '1';
		return "projecte:textures/armor/redmatter_"+index+".png";
	}

	@Override
	@Optional.Method(modid = "Thaumcraft")
	public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) 
	{
		return ((RMArmor) itemstack.getItem()).armorPiece == EntityEquipmentSlot.HEAD;
	}

	@Override
	@Optional.Method(modid = "Thaumcraft")
	public boolean showNodes(ItemStack itemstack, EntityLivingBase player) 
	{
		return ((RMArmor) itemstack.getItem()).armorPiece == EntityEquipmentSlot.HEAD;
	}
}
