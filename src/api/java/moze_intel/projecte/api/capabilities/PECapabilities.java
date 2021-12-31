package moze_intel.projecte.api.capabilities;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class PECapabilities {

	private PECapabilities() {
	}

	/**
	 * The capability object for IEmcStorage
	 */
	public static final Capability<IEmcStorage> EMC_STORAGE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	/**
	 * The capability object for IAlchBagProvider
	 */
	public static final Capability<IAlchBagProvider> ALCH_BAG_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	/**
	 * The capability object for IKnowledgeProvider
	 */
	public static final Capability<IKnowledgeProvider> KNOWLEDGE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	/**
	 * The capability object for IAlchBagItem
	 */
	public static final Capability<IAlchBagItem> ALCH_BAG_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	/**
	 * The capability object for IAlchChestItem
	 */
	public static final Capability<IAlchChestItem> ALCH_CHEST_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	/**
	 * The capability object for IExtraFunction
	 */
	public static final Capability<IExtraFunction> EXTRA_FUNCTION_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	/**
	 * The capability object for IItemCharge
	 */
	public static final Capability<IItemCharge> CHARGE_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	/**
	 * The capability object for IItemEmcHolder
	 */
	public static final Capability<IItemEmcHolder> EMC_HOLDER_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	/**
	 * The capability object for IModeChanger
	 */
	public static final Capability<IModeChanger> MODE_CHANGER_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	/**
	 * The capability object for IPedestalItem
	 */
	public static final Capability<IPedestalItem> PEDESTAL_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	/**
	 * The capability object for IProjectileShooter
	 */
	public static final Capability<IProjectileShooter> PROJECTILE_SHOOTER_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
}