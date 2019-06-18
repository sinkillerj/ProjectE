package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TNTBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NovaCataclysm extends TNTBlock
{
	public NovaCataclysm(Properties props)
	{
		super(props);
	}

	private void explode(World world, BlockPos pos, @Nullable LivingEntity exploder)
	{
		if (!world.isRemote)
		{
			TNTEntity entitytntprimed = new EntityNovaCataclysmPrimed(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, exploder);
			world.spawnEntity(entitytntprimed);
			world.playSound(null, entitytntprimed.posX, entitytntprimed.posY, entitytntprimed.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}
	
	@Override
	public void explode(World world, @Nonnull BlockPos pos)
	{
		this.explode(world, pos, null);
	}

	@Override
	public void onExplosionDestroy(World world, @Nonnull BlockPos pos, @Nonnull Explosion explosion)
	{
		if (!world.isRemote)
		{
			EntityNovaCataclysmPrimed cataclysmPrimed = new EntityNovaCataclysmPrimed(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, explosion.getExplosivePlacedBy());
			cataclysmPrimed.setFuse(world.rand.nextInt(cataclysmPrimed.getFuse() / 4) + cataclysmPrimed.getFuse() / 8);
			world.spawnEntity(cataclysmPrimed);
		}
	}

	// [VanillaCopy] super to call our own private explode
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		ItemStack itemstack = player.getHeldItem(hand);
		Item item = itemstack.getItem();
		if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
			return false;
		} else {
			this.explode(worldIn, pos, player);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
			if (item == Items.FLINT_AND_STEEL) {
				itemstack.damageItem(1, player);
			} else {
				itemstack.shrink(1);
			}

			return true;
		}
	}

	// [VanillaCopy] super to call our own private explode
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (!worldIn.isRemote && entityIn instanceof AbstractArrowEntity) {
			AbstractArrowEntity entityarrow = (AbstractArrowEntity)entityIn;
			Entity entity = entityarrow.getShooter();
			if (entityarrow.isBurning()) {
				this.explode(worldIn, pos, entity instanceof LivingEntity ? (LivingEntity)entity : null);
				worldIn.removeBlock(pos);
			}
		}

	}
}
