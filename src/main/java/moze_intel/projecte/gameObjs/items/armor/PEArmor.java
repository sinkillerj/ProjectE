package moze_intel.projecte.gameObjs.items.armor;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class PEArmor extends ArmorItem {

	protected PEArmor(IArmorMaterial material, EquipmentSlotType armorPiece, Properties props) {
		super(material, armorPiece, props);
	}

	protected abstract String getNameForLocation();

	@Override
	public boolean isBookEnchantable(@Nonnull ItemStack stack, @Nonnull ItemStack book) {
		return false;
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		return 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		char index = this.getEquipmentSlot() == EquipmentSlotType.LEGS ? '2' : '1';
		return PECore.MODID + ":textures/armor/" + getNameForLocation() + "_" + index + ".png";
	}
}