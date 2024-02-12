package moze_intel.projecte.gameObjs.registration.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AttachmentTypeDeferredRegister extends PEDeferredRegister<AttachmentType<?>> {

	public AttachmentTypeDeferredRegister(String namespace) {
		super(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, namespace);
	}

	public PEDeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> registerBoolean(String name, boolean defaultValue) {
		return register(name, () -> AttachmentType.builder(() -> defaultValue)
				//If we are true by default we only care about serializing the value when it is false
				.serialize(defaultValue ? FALSE_SERIALIZER : TRUE_SERIALIZER)
				.comparator(Boolean::equals)
				.build());
	}

	public PEDeferredHolder<AttachmentType<?>, AttachmentType<Byte>> registerByte(String name, byte defaultValue, byte min, byte max) {
		return register(name, () -> AttachmentType.builder(() -> defaultValue)
				.serialize(new IAttachmentSerializer<ByteTag, Byte>() {
					@Nullable
					@Override
					public ByteTag write(@NotNull Byte value) {
						if (value == defaultValue || value < min || value > max) {
							//If it is the default or invalid value that was manually set then don't save it
							return null;
						}
						return ByteTag.valueOf(value);
					}

					@Override
					public Byte read(@NotNull IAttachmentHolder holder, @NotNull ByteTag tag) {
						byte value = tag.getAsByte();
						if (value < min) {
							return min;
						}
						return value > max ? max : value;
					}
				}).comparator(Byte::equals)
				.build());
	}

	public PEDeferredHolder<AttachmentType<?>, AttachmentType<Integer>> registerNonNegativeInt(String name, int defaultValue) {
		return registerInt(name, defaultValue, 0, Integer.MAX_VALUE);
	}

	public PEDeferredHolder<AttachmentType<?>, AttachmentType<Integer>> registerInt(String name, int defaultValue, int min, int max) {
		return register(name, () -> AttachmentType.builder(() -> defaultValue)
				.serialize(new IAttachmentSerializer<IntTag, Integer>() {
					@Nullable
					@Override
					public IntTag write(@NotNull Integer value) {
						if (value == defaultValue || value < min || value > max) {
							//If it is the default or invalid value that was manually set then don't save it
							return null;
						}
						return IntTag.valueOf(value);
					}

					@Override
					public Integer read(@NotNull IAttachmentHolder holder, @NotNull IntTag tag) {
						return Mth.clamp(tag.getAsInt(), min, max);
					}
				}).comparator(Integer::equals)
				.build());
	}

	public PEDeferredHolder<AttachmentType<?>, AttachmentType<Long>> registerNonNegativeLong(String name, long defaultValue) {
		return registerLong(name, defaultValue, 0, Long.MAX_VALUE);
	}

	public PEDeferredHolder<AttachmentType<?>, AttachmentType<Long>> registerLong(String name, long defaultValue, long min, long max) {
		return register(name, () -> AttachmentType.builder(() -> defaultValue)
				.serialize(new IAttachmentSerializer<LongTag, Long>() {
					@Nullable
					@Override
					public LongTag write(@NotNull Long value) {
						if (value == defaultValue || value < min || value > max) {
							//If it is the default or invalid value that was manually set then don't save it
							return null;
						}
						return LongTag.valueOf(value);
					}

					@Override
					public Long read(@NotNull IAttachmentHolder holder, @NotNull LongTag tag) {
						return Mth.clamp(tag.getAsLong(), min, max);
					}
				}).comparator(Long::equals)
				.build());
	}

	public PEDeferredHolder<AttachmentType<?>, AttachmentType<Double>> registerNonNegativeDouble(String name, double defaultValue) {
		return registerDouble(name, defaultValue, 0, Double.MAX_VALUE);
	}

	public PEDeferredHolder<AttachmentType<?>, AttachmentType<Double>> registerDouble(String name, double defaultValue, double min, double max) {
		return register(name, () -> AttachmentType.builder(() -> defaultValue)
				.serialize(new IAttachmentSerializer<DoubleTag, Double>() {
					@Nullable
					@Override
					public DoubleTag write(@NotNull Double value) {
						if (value == defaultValue || value < min || value > max) {
							//If it is the default or invalid value that was manually set then don't save it
							return null;
						}
						return DoubleTag.valueOf(value);
					}

					@Override
					public Double read(@NotNull IAttachmentHolder holder, @NotNull DoubleTag tag) {
						return Mth.clamp(tag.getAsDouble(), min, max);
					}
				}).comparator(Double::equals)
				.build());
	}

	public PEDeferredHolder<AttachmentType<?>, AttachmentType<List<ItemStack>>> registerItemList(String name) {
		return registerItemList(name, stack -> !stack.isEmpty(), (list, stack) -> !stack.isEmpty());
	}

	public PEDeferredHolder<AttachmentType<?>, AttachmentType<List<ItemStack>>> registerNonDuplicateItemList(String name) {
		return registerItemList(name, stack -> !stack.isEmpty(), (list, stack) -> !stack.isEmpty() && list.stream().noneMatch(r -> ItemHandlerHelper.canItemStacksStack(r, stack)));
	}

	public PEDeferredHolder<AttachmentType<?>, AttachmentType<List<ItemStack>>> registerItemList(String name, Predicate<ItemStack> serializationChecker,
			BiPredicate<List<ItemStack>, ItemStack> deserializationChecker) {
		return register(name, () -> AttachmentType.<List<ItemStack>>builder(() -> new ArrayList<>())
				.serialize(new IAttachmentSerializer<ListTag, List<ItemStack>>() {
					@Nullable
					@Override
					public ListTag write(@NotNull List<ItemStack> value) {
						if (value.isEmpty()) {
							return null;
						}
						ListTag tag = new ListTag();
						for (ItemStack s : value) {
							if (serializationChecker.test(s)) {
								CompoundTag nbt = new CompoundTag();
								s.save(nbt);
								tag.add(nbt);
							}
						}
						return tag.isEmpty() ? null : tag;
					}

					@Override
					public List<ItemStack> read(@NotNull IAttachmentHolder holder, @NotNull ListTag tag) {
						List<ItemStack> list = new ArrayList<>(tag.size());
						for (int i = 0; i < tag.size(); i++) {
							ItemStack stack = ItemStack.of(tag.getCompound(i));
							if (deserializationChecker.test(list, stack)) {
								list.add(stack);
							}
						}
						return list;
					}
				}).comparator(List::equals)
				.build());
	}

	public <ENUM extends Enum<ENUM>> PEDeferredHolder<AttachmentType<?>, AttachmentType<ENUM>> register(String name, Class<ENUM> clazz) {
		ENUM[] values = clazz.getEnumConstants();
		ENUM defaultValue = values[0];
		return register(name, () -> AttachmentType.builder(() -> defaultValue)
				.serialize(new IAttachmentSerializer<IntTag, ENUM>() {
					@Nullable
					@Override
					public IntTag write(@NotNull ENUM value) {
						if (value == defaultValue) {
							return null;
						}
						return IntTag.valueOf(value.ordinal());
					}

					@Override
					public ENUM read(@NotNull IAttachmentHolder holder, @NotNull IntTag tag) {
						//TODO - 1.20.4: Sanitize value
						return values[tag.getAsInt()];
					}
				}).comparator((a, b) -> a == b)
				.build());
	}

	private static final IAttachmentSerializer<ByteTag, Boolean> TRUE_SERIALIZER = new IAttachmentSerializer<>() {
		@Nullable
		@Override
		public ByteTag write(@NotNull Boolean attachment) {
			return attachment ? ByteTag.ONE : null;
		}

		@Override
		public Boolean read(@NotNull IAttachmentHolder holder, ByteTag tag) {
			return tag.getAsByte() != 0;
		}
	};

	private static final IAttachmentSerializer<ByteTag, Boolean> FALSE_SERIALIZER = new IAttachmentSerializer<>() {
		@Nullable
		@Override
		public ByteTag write(@NotNull Boolean attachment) {
			return attachment ? null : ByteTag.ZERO;
		}

		@Override
		public Boolean read(@NotNull IAttachmentHolder holder, ByteTag tag) {
			return tag.getAsByte() != 0;
		}
	};
}