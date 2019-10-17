package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;

public class CataliticLens extends DestructionCatalyst implements IProjectileShooter
{
	public CataliticLens(Properties props)
	{
		super(props);
	}
	
	@Override
	public boolean shootProjectile(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand)
	{
		return ((IProjectileShooter) ObjHandler.hyperLens).shootProjectile(player, stack, hand);
	}

	@Override
	public int getNumCharges(@Nonnull ItemStack stack)
	{
		return 7;
	}
}
