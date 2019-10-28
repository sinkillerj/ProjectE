package moze_intel.projecte.gameObjs.items;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.container.PhilosStoneContainer;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class PhilosophersStone extends ItemMode implements IProjectileShooter, IExtraFunction
{
	public PhilosophersStone(Properties props)
	{
		super(props, (byte)4, new String[] {
				"pe.philstone.mode1",
				"pe.philstone.mode2",
				"pe.philstone.mode3"});
		addItemCapability(new ExtraFunctionItemCapabilityWrapper());
		addItemCapability(new ProjectileShooterItemCapabilityWrapper());
	}

	@Override
	public boolean hasContainerItem(ItemStack stack)
	{
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack)
	{
		return stack.copy();
	}

	public RayTraceResult getHitBlock(PlayerEntity player)
	{
		return rayTrace(player.getEntityWorld(), player, player.isSneaking() ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		BlockPos pos = ctx.getPos();
		Direction sideHit = ctx.getFace();
		World world = ctx.getWorld();
		PlayerEntity player = ctx.getPlayer();
		ItemStack stack = ctx.getItem();

		if (world.isRemote)
		{
			return ActionResultType.SUCCESS;
		}

		RayTraceResult rtr = getHitBlock(player);

		if (rtr instanceof BlockRayTraceResult && !((BlockRayTraceResult) rtr).getPos().equals(pos))
		{
			pos = ((BlockRayTraceResult) rtr).getPos();
			sideHit = ((BlockRayTraceResult) rtr).getFace();
		}

		BlockState result = WorldTransmutations.getWorldTransmutation(world, pos, player.isSneaking());

		if (result != null)
		{
			int mode = this.getMode(stack);
			int charge = this.getCharge(stack);

			for (BlockPos currentPos : getAffectedPositions(world, pos, player, sideHit, mode, charge))
			{
				PlayerHelper.checkedReplaceBlock(((ServerPlayerEntity) player), currentPos, result);
				if (world.rand.nextInt(8) == 0)
				{
					((ServerWorld) world).spawnParticle(ParticleTypes.LARGE_SMOKE, currentPos.getX(), currentPos.getY() + 1, currentPos.getZ(), 2, 0, 0, 0, 0);
				}
			}

			world.playSound(null, player.posX, player.posY, player.posZ, PESounds.TRANSMUTE, SoundCategory.PLAYERS, 1, 1);
		}
		
		return ActionResultType.SUCCESS;
	}

	@Override
	public boolean shootProjectile(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand)
	{
		World world = player.getEntityWorld();
		world.playSound(null, player.posX, player.posY, player.posZ, PESounds.TRANSMUTE, SoundCategory.PLAYERS, 1, 1);
		EntityMobRandomizer ent = new EntityMobRandomizer(player, world);
		ent.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
		world.addEntity(ent);
		return true;
	}
	
	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand)
	{
		if (!player.getEntityWorld().isRemote)
		{
			NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(stack));
		}

		return true;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flags)
	{
		super.addInformation(stack, world, list, flags);
		list.add(new TranslationTextComponent("pe.philstone.tooltip1", ClientKeyHelper.getKeyName(PEKeybind.EXTRA_FUNCTION)));
	}

	public static Set<BlockPos> getAffectedPositions(World world, BlockPos pos, PlayerEntity player, Direction sideHit, int mode, int charge)
	{
		Set<BlockPos> ret = new HashSet<>();
		BlockState targeted = world.getBlockState(pos);
		Stream<BlockPos> stream = null;

		switch (mode)
		{
			case 0: // Cube
				stream = BlockPos.getAllInBox(pos.add(-charge, -charge, -charge), pos.add(charge, charge, charge));
				break;
			case 1: // Panel
				if (sideHit == Direction.UP || sideHit == Direction.DOWN)
				{
					stream = BlockPos.getAllInBox(pos.add(-charge, 0, -charge), pos.add(charge, 0, charge));
				}
				else if (sideHit == Direction.EAST || sideHit == Direction.WEST)
				{
					stream = BlockPos.getAllInBox(pos.add(0, -charge, -charge), pos.add(0, charge, charge));
				}
				else if (sideHit == Direction.SOUTH || sideHit == Direction.NORTH)
				{
					stream = BlockPos.getAllInBox(pos.add(-charge, -charge, 0), pos.add(charge, charge, 0));
				}
				break;
			case 2: // Line
				Direction playerFacing = player.getHorizontalFacing();

				if (playerFacing.getAxis() == Direction.Axis.Z)
				{
					stream = BlockPos.getAllInBox(pos.add(0, 0, -charge), pos.add(0, 0, charge));
				}
				else if (playerFacing.getAxis() == Direction.Axis.X)
				{
					stream = BlockPos.getAllInBox(pos.add(-charge, 0, 0), pos.add(charge, 0, 0));
				}
				break;
		}

		if (stream != null) {
			stream.forEach(currentPos ->
			{
				if (world.getBlockState(currentPos) == targeted)
				{
					ret.add(currentPos.toImmutable());
				}
			});
		}

		return ret;
	}

	private static class ContainerProvider implements INamedContainerProvider
	{
		private final ItemStack stack;

		private ContainerProvider(ItemStack stack)
		{
			this.stack = stack;
		}

		@Nonnull
		@Override
		public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerIn) {
			return new PhilosStoneContainer(windowId, playerInventory);
		}

		@Nonnull
		@Override
		public ITextComponent getDisplayName()
		{
			return stack.getDisplayName();
		}
	}
}
