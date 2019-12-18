package moze_intel.projecte.gameObjs.items;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.BasicItemCapability;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.FluidHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class EvertideAmulet extends ItemPE implements IProjectileShooter, IPedestalItem {

	private static final AttributeModifier SPEED_BOOST = new AttributeModifier("Walk on water speed boost", 0.15, Operation.ADDITION).setSaved(false);

	public EvertideAmulet(Properties props) {
		super(props);
		addItemCapability(new PedestalItemCapabilityWrapper());
		addItemCapability(new InfiniteFluidHandler());
		addItemCapability(new ProjectileShooterItemCapabilityWrapper());
		//TODO: Curios
		//addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
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
	public ActionResultType onItemUse(ItemUseContext ctx) {
		World world = ctx.getWorld();
		PlayerEntity player = ctx.getPlayer();
		BlockPos pos = ctx.getPos();
		if (!world.isRemote && PlayerHelper.hasEditPermission((ServerPlayerEntity) player, pos)) {
			TileEntity tile = world.getTileEntity(pos);
			Direction sideHit = ctx.getFace();
			if (tile != null && tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sideHit).isPresent()) {
				FluidHelper.tryFillTank(tile, Fluids.WATER, sideHit, FluidAttributes.BUCKET_VOLUME);
			} else {
				BlockState state = world.getBlockState(pos);
				if (state.getBlock() == Blocks.CAULDRON) {
					int waterLevel = state.get(CauldronBlock.LEVEL);
					if (waterLevel < 3) {
						((CauldronBlock) state.getBlock()).setWaterLevel(world, pos, state, waterLevel + 1);
					}
				} else {
					WorldHelper.placeFluid((ServerPlayerEntity) player, world, pos, sideHit, Fluids.WATER, !ProjectEConfig.server.items.opEvertide.get());
					world.playSound(null, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), PESounds.WATER, SoundCategory.PLAYERS, 1.0F, 1.0F);
				}
			}
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, PlayerEntity player) {
		if (player.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(SPEED_BOOST)) {
			player.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(SPEED_BOOST);
		}
		return true;
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull Entity entity, int invSlot, boolean par5) {
		if (invSlot > 8 || !(entity instanceof LivingEntity)) {
			return;
		}
		LivingEntity living = (LivingEntity) entity;
		int x = (int) Math.floor(living.func_226277_ct_());
		int y = (int) (living.func_226278_cu_() - living.getYOffset());
		int z = (int) Math.floor(living.func_226281_cx_());
		BlockPos pos = new BlockPos(x, y, z);
		if (world.getFluidState(pos.down()).getFluid().isIn(FluidTags.WATER) && world.isAirBlock(pos)) {
			if (!living.func_225608_bj_()) {
				living.setMotion(living.getMotion().mul(1, 0, 1));
				living.fallDistance = 0.0F;
				living.onGround = true;
			}
			if (!world.isRemote && !living.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(SPEED_BOOST)) {
				living.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(SPEED_BOOST);
			}
		} else if (!world.isRemote) {
			if (living.isInWater()) {
				living.setAir(300);
			}
			if (living.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(SPEED_BOOST)) {
				living.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(SPEED_BOOST);
			}
		}
	}

	@Override
	public boolean shootProjectile(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand) {
		World world = player.getEntityWorld();
		if (ProjectEConfig.server.items.opEvertide.get() || !world.dimension.doesWaterVaporize()) {
			world.playSound(null, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), PESounds.WATER, SoundCategory.PLAYERS, 1.0F, 1.0F);
			EntityWaterProjectile ent = new EntityWaterProjectile(player, world);
			ent.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
			world.addEntity(ent);
			return true;
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flags) {
		list.add(new TranslationTextComponent("pe.evertide.tooltip1", ClientKeyHelper.getKeyName(PEKeybind.FIRE_PROJECTILE)));
		list.add(new TranslationTextComponent("pe.evertide.tooltip2"));
		list.add(new TranslationTextComponent("pe.evertide.tooltip3"));
		list.add(new TranslationTextComponent("pe.evertide.tooltip4"));
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		if (!world.isRemote && ProjectEConfig.server.cooldown.pedestal.evertide.get() != -1) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof DMPedestalTile) {
				DMPedestalTile tile = (DMPedestalTile) te;
				if (tile.getActivityCooldown() == 0) {
					int i = (300 + world.rand.nextInt(600)) * 20;
					world.getWorldInfo().setRainTime(i);
					world.getWorldInfo().setThunderTime(i);
					world.getWorldInfo().setRaining(true);
					tile.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.evertide.get());
				} else {
					tile.decrementActivityCooldown();
				}
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.evertide.get() != -1) {
			list.add(new TranslationTextComponent("pe.evertide.pedestal1").applyTextStyle(TextFormatting.BLUE));
			list.add(new TranslationTextComponent("pe.evertide.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.evertide.get())).applyTextStyle(TextFormatting.BLUE));
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
			return stack.getFluid().isIn(FluidTags.WATER);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (resource.getFluid().isIn(FluidTags.WATER)) {
				return resource.getAmount();
			}
			return 0;
		}

		@Nonnull
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if (resource.getFluid().isIn(FluidTags.WATER)) {
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