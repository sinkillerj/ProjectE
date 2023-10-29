package moze_intel.projecte.gameObjs.registries;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PEDamageTypes {

	private static final Map<String, PEDamageType> INTERNAL_DAMAGE_TYPES = new HashMap<>();
	public static final Map<String, PEDamageType> DAMAGE_TYPES = Collections.unmodifiableMap(INTERNAL_DAMAGE_TYPES);

	//Override the msgId to be player so that the translation uses the normal player attack one
	public static final PEDamageType BYPASS_ARMOR_PLAYER_ATTACK = new PEDamageType("player_attack", "player", 0.1F);

	public record PEDamageType(ResourceKey<DamageType> key, String msgId, float exhaustion) implements IHasTranslationKey {

		public PEDamageType {
			INTERNAL_DAMAGE_TYPES.put(key.location().toString(), this);
		}

		private PEDamageType(String name, float exhaustion) {
			this(name, PECore.MODID + "." + name, exhaustion);
		}

		private PEDamageType(String name, String msgId, float exhaustion) {
			this(ResourceKey.create(Registries.DAMAGE_TYPE, PECore.rl(name)), msgId, exhaustion);
		}

		@NotNull
		@Override
		public String getTranslationKey() {
			return "death.attack." + msgId();
		}

		public DamageSource source(@NotNull LivingEntity entity) {
			return source(entity.level(), entity);
		}

		public DamageSource source(Level level, @Nullable LivingEntity entity) {
			return source(level.registryAccess(), entity);
		}

		public DamageSource source(RegistryAccess registryAccess, @Nullable LivingEntity entity) {
			Reference<DamageType> damageTypeReference = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key());
			return new DamageSource(damageTypeReference, entity);
		}
	}
}