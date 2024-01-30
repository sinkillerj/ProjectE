package moze_intel.projecte.gameObjs.items.armor;

import java.util.function.Consumer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public abstract class PEArmor extends ArmorItem {

	protected PEArmor(ArmorMaterial material, ArmorItem.Type armorPiece, Properties props) {
		super(material, armorPiece, props);
	}

	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		return 0;
	}

	/**
	 * Minimum percent damage will be reduced to if the full set is worn
	 */
	public abstract float getFullSetBaseReduction();

	/**
	 * Gets the max damage that a piece of this armor in a given slot can absorb of a specific type.
	 *
	 * @apiNote A value of zero means that there is no special bonus blocking powers for that damage type, and the piece's base reduction will be get used instead by the
	 * damage calculation event.
	 */
	public abstract float getMaxDamageAbsorb(ArmorItem.Type type, DamageSource source);

	/**
	 * Gets the overall effectiveness of a given slots piece.
	 */
	public float getPieceEffectiveness(ArmorItem.Type type) {
		if (type == ArmorItem.Type.BOOTS || type == ArmorItem.Type.HELMET) {
			return 0.2F;
		} else if (type == ArmorItem.Type.CHESTPLATE || type == ArmorItem.Type.LEGGINGS) {
			return 0.3F;
		}
		return 0;
	}

	protected static boolean isArmorSlot(int slot) {
		return slot >= Inventory.INVENTORY_SIZE && slot < Inventory.INVENTORY_SIZE + 4;
	}
}