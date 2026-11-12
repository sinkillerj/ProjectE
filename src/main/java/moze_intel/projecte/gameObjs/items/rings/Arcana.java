package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.base.Suppliers;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import java.util.Locale;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.items.ICapabilityAware;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.gameObjs.items.IModeEnum;
import moze_intel.projecte.gameObjs.items.ISelfCraftingRemainder;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.items.rings.Arcana.ArcanaMode;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.integration.curios.IExposesCurioAttributes;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Arcana extends ItemPE implements IItemMode<ArcanaMode>, IFireProtector, IExtraFunction, IProjectileShooter, ICapabilityAware, IExposesCurioAttributes, ISelfCraftingRemainder {

	private static final AttributeModifier FLIGHT = new AttributeModifier(PECore.rl("arcana_flight"), 1, Operation.ADD_VALUE);
	private final Supplier<ItemAttributeModifiers> defaultModifiers;

	public Arcana(Properties props) {
		super(props.component(PEDataComponentTypes.ACTIVE, false)
				.component(PEDataComponentTypes.ARCANA_MODE, ArcanaMode.ZERO)
				.component(PEDataComponentTypes.STORED_EMC, 0L)
		);
		this.defaultModifiers = Suppliers.memoize(() -> ItemAttributeModifiers.builder()
				.add(NeoForgeMod.CREATIVE_FLIGHT, FLIGHT, EquipmentSlotGroup.ANY)
				.build());
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
		return this.defaultModifiers.get();
	}

	@Override
	public void addAttributes(Multimap<Holder<Attribute>, AttributeModifier> attributes) {
		attributes.put(NeoForgeMod.CREATIVE_FLIGHT, FLIGHT);
	}

	private void tick(ItemStack stack, Level level, ServerPlayer player) {
		if (stack.getOrDefault(PEDataComponentTypes.ACTIVE, false)) {
			switch (getMode(stack)) {
				case ZERO -> WorldHelper.freezeInBoundingBox(level, player.getBoundingBox().inflate(5), player, true);
				case IGNITION -> WorldHelper.igniteNearby(level, player);
				case HARVEST -> WorldHelper.growNearbyRandomly(true, level, player);
				case SWRG -> WorldHelper.repelEntitiesSWRG(level, player.getBoundingBox().inflate(5), player);
			}
		}
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
		super.inventoryTick(stack, level, entity, slot);
		if (!level.isClientSide() && hotBarOrOffHand(slot) && entity instanceof ServerPlayer player) {
			tick(stack, level, player);
		}
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, context, display, tooltip, flags);
		if (stack.getOrDefault(PEDataComponentTypes.ACTIVE, false)) {
			tooltip.accept(getToolTip(stack));
		} else {
			tooltip.accept(PELang.TOOLTIP_ARCANA_INACTIVE.translateColored(ChatFormatting.RED));
		}
	}

	@NotNull
	@Override
	public InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		if (!level.isClientSide()) {
			ItemStack stack = player.getItemInHand(hand);
			stack.update(PEDataComponentTypes.ACTIVE, false, active -> !active);
		}
		return InteractionResult.SUCCESS;
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		if (getMode(ctx.getItemInHand()) == ArcanaMode.IGNITION) {
			InteractionResult result = WorldHelper.igniteBlock(ctx);
			if (result != InteractionResult.PASS) {
				return result;
			}
		}
		return super.useOn(ctx);
	}

	@Override
	public boolean doExtraFunction(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
		//GIANT FIRE ROW OF DEATH
		Level level = player.level();
		if (level.isClientSide()) {
			return true;
		}
		if (getMode(stack) == ArcanaMode.IGNITION) {
			switch (player.getDirection()) {
				case SOUTH, NORTH -> igniteNear(player, level, 30, 5, 3);
				case WEST, EAST -> igniteNear(player, level, 3, 5, 30);
			}
			level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		}
		return true;
	}

	private void igniteNear(Player player, Level level, int xOffset, int yOffset, int zOffset) {
		for (BlockPos pos : WorldHelper.getPositionsInBox(player.getBoundingBox().inflate(xOffset, yOffset, zOffset))) {
			if (level.isEmptyBlock(pos)) {
				PlayerHelper.checkedPlaceBlock(player, level, pos.immutable(), Blocks.FIRE.defaultBlockState());
			}
		}
	}

	@Override
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
		Level level = player.level();
		if (level.isClientSide()) {
			return false;
		}
		SoundEvent sound = null;
		Projectile projectile = switch (getMode(stack)) {
			case ZERO -> {
				sound = SoundEvents.SNOWBALL_THROW;
				yield new Snowball(level, player, ItemStack.EMPTY);
			}
			case IGNITION -> {
				sound = PESoundEvents.POWER.get();
				yield new EntityFireProjectile(player, true, level);
			}
			case SWRG -> new EntitySWRGProjectile(player, true, level);
			default -> null;
		};
		if (projectile == null) {
			return false;
		}
		projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
		level.addFreshEntity(projectile);
		if (sound != null) {
			projectile.playSound(sound, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
		}
		return true;
	}

	@Override
	public boolean canPerformAction(@NotNull ItemInstance stack, @NotNull ItemAbility action) {
		if (action == ItemAbilities.FIRESTARTER_LIGHT && getMode((ItemStack) stack) == ArcanaMode.IGNITION) {
			return true;
		}
		return super.canPerformAction(stack, action);
	}

	@Override
	public void attachCapabilities(RegisterCapabilitiesEvent event) {
		IntegrationHelper.registerCuriosCapability(event, this);
	}

	@Override
	public DataComponentType<ArcanaMode> getDataComponentType() {
		return PEDataComponentTypes.ARCANA_MODE.get();
	}

	@Override
	public ArcanaMode getDefaultMode() {
		return ArcanaMode.ZERO;
	}

	public enum ArcanaMode implements IModeEnum<ArcanaMode> {
		ZERO(PELang.MODE_ARCANA_1),
		IGNITION(PELang.MODE_ARCANA_2),
		HARVEST(PELang.MODE_ARCANA_3),
		SWRG(PELang.MODE_ARCANA_4);

		public static final Codec<ArcanaMode> CODEC = StringRepresentable.fromEnum(ArcanaMode::values);
		public static final IntFunction<ArcanaMode> BY_ID = ByIdMap.continuous(ArcanaMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
		public static final StreamCodec<ByteBuf, ArcanaMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ArcanaMode::ordinal);

		private final IHasTranslationKey langEntry;
		private final String serializedName;

		ArcanaMode(IHasTranslationKey langEntry) {
			this.serializedName = name().toLowerCase(Locale.ROOT);
			this.langEntry = langEntry;
		}

		@NotNull
		@Override
		public String getSerializedName() {
			return serializedName;
		}

		@Override
		public String getTranslationKey() {
			return langEntry.getTranslationKey();
		}

		@Override
		public ArcanaMode next(ItemStack stack) {
			return switch (this) {
				case ZERO -> IGNITION;
				case IGNITION -> HARVEST;
				case HARVEST -> SWRG;
				case SWRG -> ZERO;
			};
		}
	}
}
