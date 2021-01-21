package moze_intel.projecte.gameObjs.items.rings;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class Arcana extends ItemPE implements IItemMode, IFlightProvider, IFireProtector, IExtraFunction, IProjectileShooter {

	private final static ILangEntry[] modes = new ILangEntry[]{
			PELang.MODE_ARCANA_1,
			PELang.MODE_ARCANA_2,
			PELang.MODE_ARCANA_3,
			PELang.MODE_ARCANA_4
	};

	public Arcana(Properties props) {
		super(props);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
		addItemCapability(ModeChangerItemCapabilityWrapper::new);
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

	@Override
	public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> list) {
		//Only used on the client
		if (isInGroup(group)) {
			for (byte i = 0; i < getModeCount(); ++i) {
				ItemStack stack = new ItemStack(this);
				stack.getOrCreateTag().putByte(Constants.NBT_KEY_MODE, i);
				list.add(stack);
			}
		}
	}

	@Override
	public ILangEntry[] getModeLangEntries() {
		return modes;
	}

	private void tick(ItemStack stack, World world, ServerPlayerEntity player) {
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			switch (getMode(stack)) {
				case 0:
					WorldHelper.freezeInBoundingBox(world, player.getBoundingBox().grow(5), player, true);
					break;
				case 1:
					WorldHelper.igniteNearby(world, player);
					break;
				case 2:
					WorldHelper.growNearbyRandomly(true, world, player.getPosition(), player);
					break;
				case 3:
					WorldHelper.repelEntitiesSWRG(world, player.getBoundingBox().grow(5), player);
					break;
			}
		}
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int slot, boolean held) {
		if (!world.isRemote && slot < PlayerInventory.getHotbarSize() && entity instanceof ServerPlayerEntity) {
			tick(stack, world, (ServerPlayerEntity) entity);
		}
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flags) {
		super.addInformation(stack, world, tooltips, flags);
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE)) {
			tooltips.add(getToolTip(stack));
		} else {
			tooltips.add(PELang.TOOLTIP_ARCANA_INACTIVE.translateColored(TextFormatting.RED));
		}
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
		if (!world.isRemote) {
			CompoundNBT compound = player.getHeldItem(hand).getOrCreateTag();
			compound.putBoolean(Constants.NBT_KEY_ACTIVE, !compound.getBoolean(Constants.NBT_KEY_ACTIVE));
		}
		return ActionResult.resultSuccess(player.getHeldItem(hand));
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		if (getMode(ctx.getItem()) == 1) {
			ActionResultType result = WorldHelper.igniteBlock(ctx);
			if (result != ActionResultType.PASS) {
				return result;
			}
		}
		return super.onItemUse(ctx);
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand) {
		//GIANT FIRE ROW OF DEATH
		World world = player.getEntityWorld();
		if (world.isRemote) {
			return true;
		}
		switch (getMode(stack)) {
			case 1: // ignition
				switch (player.getHorizontalFacing()) {
					case SOUTH: // fall through
					case NORTH:
						for (BlockPos pos : BlockPos.getAllInBoxMutable(player.getPosition().add(-30, -5, -3), player.getPosition().add(30, 5, 3))) {
							if (world.isAirBlock(pos)) {
								PlayerHelper.checkedPlaceBlock((ServerPlayerEntity) player, pos.toImmutable(), Blocks.FIRE.getDefaultState());
							}
						}
						break;
					case WEST: // fall through
					case EAST:
						for (BlockPos pos : BlockPos.getAllInBoxMutable(player.getPosition().add(-3, -5, -30), player.getPosition().add(3, 5, 30))) {
							if (world.isAirBlock(pos)) {
								PlayerHelper.checkedPlaceBlock((ServerPlayerEntity) player, pos.toImmutable(), Blocks.FIRE.getDefaultState());
							}
						}
						break;
				}
				world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), PESoundEvents.POWER.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
				break;
		}
		return true;
	}

	@Override
	public boolean shootProjectile(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand) {
		World world = player.getEntityWorld();
		if (world.isRemote) {
			return false;
		}
		switch (getMode(stack)) {
			case 0: // zero
				SnowballEntity snowball = new SnowballEntity(world, player);
				snowball.func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
				world.addEntity(snowball);
				snowball.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 1.0F, 1.0F);
				break;
			case 1: // ignition
				EntityFireProjectile fire = new EntityFireProjectile(player, world);
				fire.func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
				world.addEntity(fire);
				fire.playSound(PESoundEvents.POWER.get(), 1.0F, 1.0F);
				break;
			case 3: // swrg
				EntitySWRGProjectile lightning = new EntitySWRGProjectile(player, true, world);
				lightning.func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
				world.addEntity(lightning);
				break;
		}
		return true;
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, ServerPlayerEntity player) {
		return true;
	}

	@Override
	public boolean canProvideFlight(ItemStack stack, ServerPlayerEntity player) {
		return true;
	}
}