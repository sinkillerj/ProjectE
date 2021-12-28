package moze_intel.projecte.gameObjs.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.BasicItemCapability;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.block_entities.DMPedestalTile;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class EvertideAmulet extends ItemPE implements IProjectileShooter, IPedestalItem {

	public EvertideAmulet(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
		addItemCapability(InfiniteFluidHandler::new);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return stack.copy();
	}

	@Nonnull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		if (player == null) {
			return InteractionResult.FAIL;
		}
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		if (!world.isClientSide && PlayerHelper.hasEditPermission((ServerPlayer) player, pos)) {
			BlockEntity tile = WorldHelper.getTileEntity(world, pos);
			Direction sideHit = ctx.getClickedFace();
			if (tile != null) {
				Optional<IFluidHandler> capability = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sideHit).resolve();
				if (capability.isPresent()) {
					capability.get().fill(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
					return InteractionResult.SUCCESS;
				}
			}
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() == Blocks.CAULDRON) {
				//TODO - 1.18: Re-evaluate I think this should end up becoming a CauldronInteraction
				/*int waterLevel = state.getValue(CauldronBlock.LEVEL);
				if (waterLevel < 3) {
					((CauldronBlock) state.getBlock()).setWaterLevel(world, pos, state, waterLevel + 1);
				}*/
			} else {
				WorldHelper.placeFluid((ServerPlayer) player, world, pos, sideHit, Fluids.WATER, !ProjectEConfig.server.items.opEvertide.get());
				world.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.WATER_MAGIC.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean shootProjectile(@Nonnull Player player, @Nonnull ItemStack stack, InteractionHand hand) {
		Level world = player.getCommandSenderWorld();
		if (ProjectEConfig.server.items.opEvertide.get() || !world.dimensionType().ultraWarm()) {
			world.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.WATER_MAGIC.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			EntityWaterProjectile ent = new EntityWaterProjectile(player, world);
			ent.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
			world.addFreshEntity(ent);
			return true;
		}
		return false;
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flags) {
		super.appendHoverText(stack, world, tooltips, flags);
		tooltips.add(PELang.TOOLTIP_EVERTIDE_1.translate(ClientKeyHelper.getKeyName(PEKeybind.FIRE_PROJECTILE)));
		tooltips.add(PELang.TOOLTIP_EVERTIDE_2.translate());
		tooltips.add(PELang.TOOLTIP_EVERTIDE_3.translate());
		tooltips.add(PELang.TOOLTIP_EVERTIDE_4.translate());
	}

	@Override
	public void updateInPedestal(@Nonnull Level world, @Nonnull BlockPos pos) {
		if (!world.isClientSide && ProjectEConfig.server.cooldown.pedestal.evertide.get() != -1) {
			DMPedestalTile tile = WorldHelper.getTileEntity(DMPedestalTile.class, world, pos, true);
			if (tile != null) {
				if (tile.getActivityCooldown() == 0) {
					if (world.getLevelData() instanceof ServerLevelData) {
						int i = (300 + world.random.nextInt(600)) * 20;
						ServerLevelData worldInfo = (ServerLevelData) world.getLevelData();
						worldInfo.setRainTime(i);
						worldInfo.setThunderTime(i);
						worldInfo.setRaining(true);
					}
					tile.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.evertide.get());
				} else {
					tile.decrementActivityCooldown();
				}
			}
		}
	}

	@Nonnull
	@Override
	public List<Component> getPedestalDescription() {
		List<Component> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.evertide.get() != -1) {
			list.add(PELang.PEDESTAL_EVERTIDE_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_EVERTIDE_2.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.evertide.get())));
		}
		return list;
	}

	private static class InfiniteFluidHandler extends BasicItemCapability<IFluidHandlerItem> implements IFluidHandlerItem {

		@Nonnull
		@Override
		public ItemStack getContainer() {
			return getStack();
		}

		@Override
		public int getTanks() {
			return 1;
		}

		@Nonnull
		@Override
		public FluidStack getFluidInTank(int tank) {
			return tank == 0 ? new FluidStack(Fluids.WATER, Integer.MAX_VALUE) : FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank) {
			return tank == 0 ? Integer.MAX_VALUE : 0;
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
			return stack.getFluid().is(FluidTags.WATER);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (resource.getFluid().is(FluidTags.WATER)) {
				return resource.getAmount();
			}
			return 0;
		}

		@Nonnull
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if (resource.getFluid().is(FluidTags.WATER)) {
				return resource;
			}
			return FluidStack.EMPTY;
		}

		@Nonnull
		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			return new FluidStack(Fluids.WATER, maxDrain);
		}

		@Override
		public Capability<IFluidHandlerItem> getCapability() {
			return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
		}
	}
}