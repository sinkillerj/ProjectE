package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NovaCataclysm extends BlockTNT
{
	public NovaCataclysm(Properties props)
	{
		super(props);
	}

	private void explode(World world, BlockPos pos, @Nullable EntityLivingBase exploder)
	{
		if (!world.isRemote)
		{
			EntityTNTPrimed entitytntprimed = new EntityNovaCataclysmPrimed(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, exploder);
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
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
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
	public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (!worldIn.isRemote && entityIn instanceof EntityArrow) {
			EntityArrow entityarrow = (EntityArrow)entityIn;
			Entity entity = entityarrow.getShooter();
			if (entityarrow.isBurning()) {
				this.explode(worldIn, pos, entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null);
				worldIn.removeBlock(pos);
			}
		}

	}
}
