package moze_intel.projecte.events;

import java.util.Optional;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.capability.managing.BasicCapabilityResolver;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import moze_intel.projecte.gameObjs.items.armor.PEArmor;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.impl.TransmutationOffline;
import moze_intel.projecte.impl.capability.AlchBagImpl;
import moze_intel.projecte.impl.capability.KnowledgeImpl;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

@Mod.EventBusSubscriber(modid = PECore.MODID)
public class PlayerEvents {

	// On death or return from end, copy the capability data
	@SubscribeEvent
	public static void cloneEvent(PlayerEvent.Clone evt) {
		evt.getOriginal().getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY).ifPresent(old -> {
			CompoundNBT bags = old.serializeNBT();
			evt.getPlayer().getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY).ifPresent(c -> c.deserializeNBT(bags));
		});
		evt.getOriginal().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(old -> {
			CompoundNBT knowledge = old.serializeNBT();
			evt.getPlayer().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(c -> c.deserializeNBT(knowledge));
		});
	}

	// On death or return from end, sync to the client
	@SubscribeEvent
	public static void respawnEvent(PlayerEvent.PlayerRespawnEvent evt) {
		evt.getPlayer().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(c -> c.sync((ServerPlayerEntity) evt.getPlayer()));
		evt.getPlayer().getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY).ifPresent(c -> c.sync(null, (ServerPlayerEntity) evt.getPlayer()));
	}

	@SubscribeEvent
	public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		// Sync to the client for "normal" interdimensional teleports (nether portal, etc.)
		event.getPlayer().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(c -> c.sync((ServerPlayerEntity) event.getPlayer()));
		event.getPlayer().getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY, null).ifPresent(c -> c.sync(null, (ServerPlayerEntity) event.getPlayer()));

		event.getPlayer().getCapability(InternalAbilities.CAPABILITY).ifPresent(InternalAbilities::onDimensionChange);
	}

	@SubscribeEvent
	public static void attachCaps(AttachCapabilitiesEvent<Entity> evt) {
		if (evt.getObject() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) evt.getObject();
			attachCapability(evt, AlchBagImpl.Provider.NAME, new AlchBagImpl.Provider());
			attachCapability(evt, KnowledgeImpl.Provider.NAME, new KnowledgeImpl.Provider(player));
			if (player instanceof ServerPlayerEntity) {
				attachCapability(evt, InternalTimers.NAME, new InternalTimers.Provider());
				attachCapability(evt, InternalAbilities.NAME, new InternalAbilities.Provider((ServerPlayerEntity) player));
			}
		}
	}

	private static void attachCapability(AttachCapabilitiesEvent<Entity> evt, ResourceLocation name, BasicCapabilityResolver<?> cap) {
		evt.addCapability(name, cap);
		evt.addListener(cap::invalidateAll);
	}

	@SubscribeEvent
	public static void playerConnect(PlayerEvent.PlayerLoggedInEvent event) {
		ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
		PacketHandler.sendFragmentedEmcPacket(player);

		player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(knowledge -> {
			knowledge.sync(player);
			PlayerHelper.updateScore(player, PlayerHelper.SCOREBOARD_EMC, knowledge.getEmc());
		});

		player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY).ifPresent(c -> c.sync(null, player));

		PECore.debugLog("Sent knowledge and bag data to {}", player.getName());
	}

	@SubscribeEvent
	public static void onConstruct(EntityEvent.EntityConstructing evt) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER // No world to check yet
			&& evt.getEntity() instanceof PlayerEntity && !(evt.getEntity() instanceof FakePlayer)) {
			TransmutationOffline.clear(evt.getEntity().getUUID());
			PECore.debugLog("Clearing offline data cache in preparation to load online data");
		}
	}

	@SubscribeEvent
	public static void onHighAlchemistJoin(PlayerEvent.PlayerLoggedInEvent evt) {
		if (PECore.uuids.contains(evt.getPlayer().getUUID().toString())) {
			ITextComponent joinMessage = PELang.HIGH_ALCHEMIST.translateColored(TextFormatting.BLUE, TextFormatting.GOLD, evt.getPlayer().getDisplayName());
			ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(joinMessage, ChatType.SYSTEM, Util.NIL_UUID);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void pickupItem(EntityItemPickupEvent event) {
		PlayerEntity player = event.getPlayer();
		World world = player.getCommandSenderWorld();
		if (world.isClientSide) {
			return;
		}
		ItemStack bag = AlchemicalBag.getFirstBagWithSuctionItem(player, player.inventory.items);
		if (bag.isEmpty()) {
			return;
		}
		Optional<IAlchBagProvider> cap = player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY).resolve();
		if (!cap.isPresent()) {
			return;
		}
		IItemHandler handler = cap.get().getBag(((AlchemicalBag) bag.getItem()).color);
		ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, event.getItem().getItem(), false);
		if (remainder.isEmpty()) {
			event.getItem().remove();
			world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			((ServerPlayerEntity) player).connection.send(new SCollectItemPacket(event.getItem().getId(), player.getId(), 1));
		} else {
			event.getItem().setItem(remainder);
		}
		event.setCanceled(true);
	}

	//This event is called when the entity first is about to take damage, if it gets cancelled it is as if they never got hit/damaged
	@SubscribeEvent
	public static void onAttacked(LivingAttackEvent evt) {
		if (evt.getEntity() instanceof ServerPlayerEntity && evt.getSource().isFire() && TickEvents.shouldPlayerResistFire((ServerPlayerEntity) evt.getEntity())) {
			evt.setCanceled(true);
		}
	}

	//This event gets called when calculating how much damage to do to the entity, even if it is canceled the entity will still get "hit"
	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent evt) {
		float damage = evt.getAmount();
		if (damage > 0) {
			LivingEntity entityLiving = evt.getEntityLiving();
			DamageSource source = evt.getSource();
			float totalPercentReduced = getReductionForSlot(entityLiving, source, EquipmentSlotType.HEAD, damage) +
										getReductionForSlot(entityLiving, source, EquipmentSlotType.CHEST, damage) +
										getReductionForSlot(entityLiving, source, EquipmentSlotType.LEGS, damage) +
										getReductionForSlot(entityLiving, source, EquipmentSlotType.FEET, damage);
			float damageAfter = totalPercentReduced >= 1 ? 0 : damage - damage * totalPercentReduced;
			if (damageAfter <= 0) {
				evt.setCanceled(true);
			} else if (damage != damageAfter) {
				evt.setAmount(damageAfter);
			}
		}
	}

	private static float getReductionForSlot(LivingEntity entityLiving, DamageSource source, EquipmentSlotType slot, float damage) {
		ItemStack armorStack = entityLiving.getItemBySlot(slot);
		if (armorStack.getItem() instanceof PEArmor) {
			PEArmor armorItem = (PEArmor) armorStack.getItem();
			EquipmentSlotType type = armorItem.getSlot();
			if (type != slot) {
				//If the armor slot does not match the slot this piece of armor is for then it shouldn't be providing any reduction
				return 0;
			}
			//We return the max of this piece's base reduction (in relation to the full set), and the
			// max damage an item can absorb for a given source
			return Math.max(armorItem.getFullSetBaseReduction(), armorItem.getMaxDamageAbsorb(type, source) / damage) * armorItem.getPieceEffectiveness(type);
		}
		return 0;
	}
}