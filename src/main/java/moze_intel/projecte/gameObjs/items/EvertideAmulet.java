package moze_intel.projecte.gameObjs.items;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
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
import net.minecraft.tags.FluidTags;
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
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvertideAmulet extends ItemPE implements IProjectileShooter, IPedestalItem, ICapabilityAware {

	public EvertideAmulet(Properties props) {
		super(props);
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
		Player player = ctx.getPlayer();
		if (player == null) {
			return InteractionResult.FAIL;
		}
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		if (!level.isClientSide && PlayerHelper.hasEditPermission((ServerPlayer) player, pos)) {
			Direction sideHit = ctx.getClickedFace();
			IFluidHandler fluidHandler = WorldHelper.getCapability(level, FluidHandler.BLOCK, pos, sideHit);
			if (fluidHandler != null) {
				fluidHandler.fill(new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
				return InteractionResult.CONSUME;
			}
			WorldHelper.placeFluid((ServerPlayer) player, level, pos, sideHit, Fluids.WATER, !ProjectEConfig.server.items.opEvertide.get());
			level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.WATER_MAGIC.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
		Level level = player.level();
		if (ProjectEConfig.server.items.opEvertide.get() || !level.dimensionType().ultraWarm()) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.WATER_MAGIC.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			EntityWaterProjectile ent = new EntityWaterProjectile(player, level);
			ent.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
			level.addFreshEntity(ent);
			return true;
		}
		return false;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(PELang.TOOLTIP_EVERTIDE_1.translate(ClientKeyHelper.getKeyName(PEKeybind.FIRE_PROJECTILE)));
		tooltips.add(PELang.TOOLTIP_EVERTIDE_2.translate());
		tooltips.add(PELang.TOOLTIP_EVERTIDE_3.translate());
		tooltips.add(PELang.TOOLTIP_EVERTIDE_4.translate());
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		if (!level.isClientSide && ProjectEConfig.server.cooldown.pedestal.evertide.get() != -1) {
			if (pedestal.getActivityCooldown() == 0) {
				if (level.getLevelData() instanceof ServerLevelData worldInfo) {
					int i = (300 + level.random.nextInt(600)) * 20;
					worldInfo.setRainTime(i);
					worldInfo.setThunderTime(i);
					worldInfo.setRaining(true);
				}
				pedestal.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.evertide.get());
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
		if (ProjectEConfig.server.cooldown.pedestal.evertide.get() != -1) {
			list.add(PELang.PEDESTAL_EVERTIDE_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_EVERTIDE_2.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.evertide.get())));
		}
		return list;
	}

	@Override
	public void attachCapabilities(RegisterCapabilitiesEvent event) {
		event.registerItem(FluidHandler.ITEM, (stack, context) -> new InfiniteFluidHandler(stack), this);
		IntegrationHelper.registerCuriosCapability(event, this);
	}

	private record InfiniteFluidHandler(ItemStack stack) implements IFluidHandlerItem {

		@NotNull
		@Override
		public ItemStack getContainer() {
			return stack;
		}

		@Override
		public int getTanks() {
			return 1;
		}

		@NotNull
		@Override
		public FluidStack getFluidInTank(int tank) {
			return tank == 0 ? new FluidStack(Fluids.WATER, Integer.MAX_VALUE) : FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank) {
			return tank == 0 ? Integer.MAX_VALUE : 0;
		}

		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
			return isWater(stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			return isWater(resource) ? resource.getAmount() : 0;
		}

		@NotNull
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			return isWater(resource) ? resource : FluidStack.EMPTY;
		}

		private boolean isWater(FluidStack stack) {
			return stack.getFluid().is(FluidTags.WATER);
		}

		@NotNull
		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			return new FluidStack(Fluids.WATER, maxDrain);
		}
	}
}