package moze_intel.projecte.gameObjs.items.armor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

public class GemLegs extends GemArmorBase {

	public GemLegs(Properties props) {
		super(EquipmentSlotType.LEGS, props);
		MinecraftForge.EVENT_BUS.addListener(this::onJump);
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flags) {
		super.appendHoverText(stack, world, tooltips, flags);
		tooltips.add(PELang.GEM_LORE_LEGS.translate());
	}

	private final Map<Integer, Long> lastJumpTracker = new HashMap<>();

	private void onJump(LivingEvent.LivingJumpEvent evt) {
		if (evt.getEntityLiving() instanceof PlayerEntity && evt.getEntityLiving().getCommandSenderWorld().isClientSide) {
			lastJumpTracker.put(evt.getEntityLiving().getId(), evt.getEntityLiving().getCommandSenderWorld().getGameTime());
		}
	}

	private boolean jumpedRecently(PlayerEntity player) {
		return lastJumpTracker.containsKey(player.getId()) && player.getCommandSenderWorld().getGameTime() - lastJumpTracker.get(player.getId()) < 5;
	}

	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
		if (world.isClientSide) {
			if (player.isShiftKeyDown() && !player.isOnGround() && player.getDeltaMovement().y() > -8 && !jumpedRecently(player)) {
				player.setDeltaMovement(player.getDeltaMovement().add(0, -0.32F, 0));
			}
		}
		if (player.isShiftKeyDown()) {
			AxisAlignedBB box = new AxisAlignedBB(player.getX() - 3.5, player.getY() - 3.5, player.getZ() - 3.5,
					player.getX() + 3.5, player.getY() + 3.5, player.getZ() + 3.5);
			WorldHelper.repelEntitiesSWRG(world, box, player);
			if (!world.isClientSide && player.getDeltaMovement().y() < -0.08) {
				List<Entity> entities = player.getCommandSenderWorld().getEntities(player, player.getBoundingBox().move(player.getDeltaMovement()).inflate(2.0D),
						entity -> entity instanceof LivingEntity);
				for (Entity e : entities) {
					if (e.isPickable()) {
						e.hurt(DamageSource.playerAttack(player), (float) -player.getDeltaMovement().y() * 6F);
					}
				}
			}
		}
	}
}