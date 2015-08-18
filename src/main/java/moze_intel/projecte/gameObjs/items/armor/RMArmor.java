package moze_intel.projecte.gameObjs.items.armor;

import cpw.mods.fml.common.Optional;
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
import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;

import java.util.Locale;

@Optional.InterfaceList(value = {@Optional.Interface(iface = "thaumcraft.api.nodes.IRevealer", modid = "Thaumcraft"), @Optional.Interface(iface = "thaumcraft.api.IGoggles", modid = "Thaumcraft")})
public class RMArmor extends ItemArmor implements ISpecialArmor, IRevealer, IGoggles
{
	private final EnumArmorType armorPiece;
	public RMArmor(EnumArmorType armorType)
	{
		super(ArmorMaterial.DIAMOND, 0, armorType.ordinal());
		this.setCreativeTab(ObjHandler.cTab);
		this.setUnlocalizedName("pe_rm_armor_" + armorType.ordinal());
		this.setHasSubtypes(false);
		this.setMaxDamage(0);
		this.armorPiece = armorType;
	}
	
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) 
	{
		EnumArmorType type = ((RMArmor) armor.getItem()).armorPiece;
		if (source.isExplosion())
		{
			return new ArmorProperties(1, 1.0D, 500);
		}

		if (type == EnumArmorType.HEAD && source == DamageSource.fall)
		{
			return new ArmorProperties(1, 1.0D, 10);
		}
		
		if (type == EnumArmorType.HEAD || type == EnumArmorType.FEET)
		{
			return new ArmorProperties(0, 0.2D, 250);
		}
		
		return new ArmorProperties(0, 0.3D, 350);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) 
	{
		EnumArmorType type = ((RMArmor) armor.getItem()).armorPiece;
		return (type == EnumArmorType.HEAD || type == EnumArmorType.FEET) ? 4 : 6;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons (IIconRegister par1IconRegister)
	{
		String type = this.armorPiece.name.toLowerCase(Locale.ROOT);
		this.itemIcon = par1IconRegister.registerIcon("projecte:rm_armor/" + type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture (ItemStack stack, Entity entity, int slot, String type)
	{
		char index = this.armorPiece == EnumArmorType.LEGS ? '2' : '1';
		return "projecte:textures/armor/redmatter_"+index+".png";
	}

	@Override
	@Optional.Method(modid = "Thaumcraft")
	public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) 
	{
		return ((RMArmor) itemstack.getItem()).armorPiece == EnumArmorType.HEAD;
	}

	@Override
	@Optional.Method(modid = "Thaumcraft")
	public boolean showNodes(ItemStack itemstack, EntityLivingBase player) 
	{
		return ((RMArmor) itemstack.getItem()).armorPiece == EnumArmorType.HEAD;
	}
}
