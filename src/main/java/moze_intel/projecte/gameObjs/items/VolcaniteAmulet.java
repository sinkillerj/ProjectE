package moze_intel.projecte.gameObjs.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VolcaniteAmulet extends ItemPE implements IProjectileShooter, IPedestalItem, IFireProtector {

	public VolcaniteAmulet(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack stack) {
		return stack.copy();
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level level = ctx.getLevel();
		Player player = ctx.getPlayer();
		BlockPos pos = ctx.getClickedPos();
		ItemStack stack = ctx.getItemInHand();
		if (player != null && !level.isClientSide && PlayerHelper.hasEditPermission((ServerPlayer) player, pos) && consumeFuel(player, stack, 32, true)) {
			BlockEntity blockEntity = WorldHelper.getBlockEntity(level, pos);
			Direction sideHit = ctx.getClickedFace();
			if (blockEntity != null) {
				Optional<IFluidHandler> capability = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, sideHit).resolve();
				if (capability.isPresent()) {
					capability.get().fill(new FluidStack(Fluids.LAVA, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
					return InteractionResult.CONSUME;
				}
			}
			WorldHelper.placeFluid((ServerPlayer) player, level, pos, sideHit, Fluids.LAVA, false);
			level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.TRANSMUTE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
		player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.TRANSMUTE.get(), SoundSource.PLAYERS, 1, 1);
		EntityLavaProjectile ent = new EntityLavaProjectile(player, player.getCommandSenderWorld());
		ent.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
		player.getCommandSenderWorld().addFreshEntity(ent);
		return true;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(PELang.TOOLTIP_VOLCANITE_1.translate(ClientKeyHelper.getKeyName(PEKeybind.FIRE_PROJECTILE)));
		tooltips.add(PELang.TOOLTIP_VOLCANITE_2.translate());
		tooltips.add(PELang.TOOLTIP_VOLCANITE_3.translate());
		tooltips.add(PELang.TOOLTIP_VOLCANITE_4.translate());
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		if (!level.isClientSide && ProjectEConfig.server.cooldown.pedestal.volcanite.get() != -1) {
			if (pedestal.getActivityCooldown() == 0) {
				if (level.getLevelData() instanceof ServerLevelData worldInfo) {
					worldInfo.setRainTime(0);
					worldInfo.setThunderTime(0);
					worldInfo.setRaining(false);
					worldInfo.setThundering(false);
				}
				pedestal.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.volcanite.get());
			} else {
				pedestal.decrementActivityCooldown();
			}
		}
		return false;
	}

	@NotNull
	@Override
	public List<Component> getPedestalDescription() {
		List<Component> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.volcanite.get() != -1) {
			list.add(PELang.PEDESTAL_VOLCANITE_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_VOLCANITE_2.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.volcanite.get())));
		}
		return list;
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, ServerPlayer player) {
		return true;
	}
}