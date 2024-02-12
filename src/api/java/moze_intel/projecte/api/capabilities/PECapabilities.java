package moze_intel.projecte.api.capabilities;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class PECapabilities {

	private PECapabilities() {
	}

	private static ResourceLocation rl(String path) {
		return new ResourceLocation(ProjectEAPI.PROJECTE_MODID, path);
	}

	/**
	 * The capability object for IEmcStorage
	 */
	public static final BlockCapability<IEmcStorage, @Nullable Direction> EMC_STORAGE_CAPABILITY = BlockCapability.createSided(rl("emc_storage"), IEmcStorage.class);

	/**
	 * The capability object for IAlchBagProvider
	 */
	public static final EntityCapability<IAlchBagProvider, Void> ALCH_BAG_CAPABILITY = EntityCapability.createVoid(rl("alchemical_bag"), IAlchBagProvider.class);

	/**
	 * The capability object for IKnowledgeProvider
	 */
	public static final EntityCapability<IKnowledgeProvider, Void> KNOWLEDGE_CAPABILITY = EntityCapability.createVoid(rl("knowledge"), IKnowledgeProvider.class);

	/**
	 * The capability object for IAlchBagItem
	 */
	public static final ItemCapability<IAlchBagItem, Void> ALCH_BAG_ITEM_CAPABILITY = ItemCapability.createVoid(rl("alchemical_bag"), IAlchBagItem.class);

	/**
	 * The capability object for IAlchChestItem
	 */
	public static final ItemCapability<IAlchChestItem, Void> ALCH_CHEST_ITEM_CAPABILITY = ItemCapability.createVoid(rl("alchemical_chest"), IAlchChestItem.class);

	/**
	 * The capability object for IExtraFunction
	 */
	public static final ItemCapability<IExtraFunction, Void> EXTRA_FUNCTION_ITEM_CAPABILITY = ItemCapability.createVoid(rl("extra_function"), IExtraFunction.class);

	/**
	 * The capability object for IItemCharge
	 */
	public static final ItemCapability<IItemCharge, Void> CHARGE_ITEM_CAPABILITY = ItemCapability.createVoid(rl("charge"), IItemCharge.class);

	/**
	 * The capability object for IItemEmcHolder
	 */
	public static final ItemCapability<IItemEmcHolder, Void> EMC_HOLDER_ITEM_CAPABILITY = ItemCapability.createVoid(rl("emc_holder"), IItemEmcHolder.class);

	/**
	 * The capability object for IModeChanger
	 */
	public static final ItemCapability<IModeChanger<?>, Void> MODE_CHANGER_ITEM_CAPABILITY = ItemCapability.createVoid(rl("mode_changer"), (Class) IModeChanger.class);//TODO - 1.20.4: Re-evaluate

	/**
	 * The capability object for IPedestalItem
	 */
	public static final ItemCapability<IPedestalItem, Void> PEDESTAL_ITEM_CAPABILITY = ItemCapability.createVoid(rl("pedestal"), IPedestalItem.class);

	/**
	 * The capability object for IProjectileShooter
	 */
	public static final ItemCapability<IProjectileShooter, Void> PROJECTILE_SHOOTER_ITEM_CAPABILITY = ItemCapability.createVoid(rl("projectile_shooter"), IProjectileShooter.class);
}