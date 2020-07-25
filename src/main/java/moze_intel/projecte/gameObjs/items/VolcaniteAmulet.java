package moze_intel.projecte.gameObjs.items;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.FluidHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
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
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class VolcaniteAmulet extends ItemPE implements IProjectileShooter, IPedestalItem, IFireProtector {

	private static final AttributeModifier SPEED_BOOST = new AttributeModifier("Walk on lava speed boost", 0.15, Operation.ADDITION);

	public VolcaniteAmulet(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
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
	public ActionResultType onItemUse(ItemUseContext ctx) {
		World world = ctx.getWorld();
		PlayerEntity player = ctx.getPlayer();
		BlockPos pos = ctx.getPos();
		ItemStack stack = ctx.getItem();
		if (!world.isRemote && PlayerHelper.hasEditPermission((ServerPlayerEntity) player, pos) && consumeFuel(player, stack, 32, true)) {
			TileEntity tile = world.getTileEntity(pos);
			Direction sideHit = ctx.getFace();
			if (tile != null && tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sideHit).isPresent()) {
				FluidHelper.tryFillTank(tile, Fluids.LAVA, sideHit, FluidAttributes.BUCKET_VOLUME);
			} else {
				WorldHelper.placeFluid((ServerPlayerEntity) player, world, pos, sideHit, Fluids.LAVA, false);
				world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), PESounds.TRANSMUTE, SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, PlayerEntity player) {
		if (player.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(SPEED_BOOST)) {
			player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_BOOST);
		}
		return true;
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull Entity entity, int invSlot, boolean par5) {
		if (invSlot > 8 || !(entity instanceof LivingEntity)) {
			return;
		}
		LivingEntity living = (LivingEntity) entity;
		int x = (int) Math.floor(living.getPosX());
		int y = (int) (living.getPosY() - living.getYOffset());
		int z = (int) Math.floor(living.getPosZ());
		BlockPos pos = new BlockPos(x, y, z);
		if (world.getFluidState(pos.down()).getFluid().isIn(FluidTags.LAVA) && world.isAirBlock(pos)) {
			if (!living.isSneaking()) {
				living.setMotion(living.getMotion().mul(1, 0, 1));
				living.fallDistance = 0.0F;
				living.setOnGround(true);
			}
			if (!world.isRemote && !living.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(SPEED_BOOST)) {
				living.getAttribute(Attributes.MOVEMENT_SPEED).applyNonPersistentModifier(SPEED_BOOST);
			}
		} else if (!world.isRemote) {
			if (living.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(SPEED_BOOST)) {
				living.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_BOOST);
			}
		}
	}

	@Override
	public boolean shootProjectile(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand) {
		player.getEntityWorld().playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), PESounds.TRANSMUTE, SoundCategory.PLAYERS, 1, 1);
		EntityLavaProjectile ent = new EntityLavaProjectile(player, player.getEntityWorld());
		ent.func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
		player.getEntityWorld().addEntity(ent);
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<ITextComponent> list, @Nonnull ITooltipFlag flags) {
		list.add(new TranslationTextComponent("pe.volcanite.tooltip1", ClientKeyHelper.getKeyName(PEKeybind.FIRE_PROJECTILE)));
		list.add(new TranslationTextComponent("pe.volcanite.tooltip2"));
		list.add(new TranslationTextComponent("pe.volcanite.tooltip3"));
		list.add(new TranslationTextComponent("pe.volcanite.tooltip4"));
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		if (!world.isRemote && ProjectEConfig.server.cooldown.pedestal.volcanite.get() != -1) {
			TileEntity te = world.getTileEntity(pos);
			if (!(te instanceof DMPedestalTile)) {
				return;
			}
			DMPedestalTile tile = (DMPedestalTile) te;
			if (tile.getActivityCooldown() == 0) {
				if (world.getWorldInfo() instanceof IServerWorldInfo) {
					IServerWorldInfo worldInfo = (IServerWorldInfo) world.getWorldInfo();
					worldInfo.setRainTime(0);
					worldInfo.setThunderTime(0);
					worldInfo.setRaining(false);
					worldInfo.setThundering(false);
				}
				tile.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.volcanite.get());
			} else {
				tile.decrementActivityCooldown();
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.volcanite.get() != -1) {
			list.add(new TranslationTextComponent("pe.volcanite.pedestal1").mergeStyle(TextFormatting.BLUE));
			list.add(new TranslationTextComponent("pe.volcanite.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.volcanite.get())).mergeStyle(TextFormatting.BLUE));
		}
		return list;
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, ServerPlayerEntity player) {
		return true;
	}
}