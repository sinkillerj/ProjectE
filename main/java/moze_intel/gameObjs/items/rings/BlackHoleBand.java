package moze_intel.gameObjs.items.rings;

import java.util.List;

import moze_intel.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlackHoleBand extends RingToggle
{
	public BlackHoleBand()
	{
		super("black_hole");
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			changeMode(player, stack);
		}
		
		return stack;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (world.isRemote || stack.getItemDamage() != 1) return;
		if (!(entity instanceof EntityPlayer)) return;
		
		EntityPlayer player = (EntityPlayer) entity;
		AxisAlignedBB bBox = player.boundingBox.expand(7, 7, 7);
		List<EntityItem> itemList = world.getEntitiesWithinAABB(EntityItem.class, bBox);
		for (EntityItem item : itemList)
			if (Utils.HasSpace(player.inventory.mainInventory, item.getEntityItem())) 
			{
				item.delayBeforeCanPickup = 0;
				double d1 = (player.posX - item.posX);
	            double d2 = (player.posY + (double)player.getEyeHeight() - item.posY);
	            double d3 = (player.posZ - item.posZ);
	            double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);

	            item.motionX += d1 / d4 * 0.1D;
	            item.motionY += d2 / d4 * 0.1D;
	            item.motionZ += d3 / d4 * 0.1D;
                
                item.moveEntity(item.motionX, item.motionY, item.motionZ);
			}
	}
}
