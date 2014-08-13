package moze_intel.gameObjs.items.rings;

import moze_intel.gameObjs.items.IItemModeChanger;
import moze_intel.gameObjs.items.ItemCharge;
import moze_intel.utils.CoordinateBox;
import moze_intel.utils.Utils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Zero extends ItemCharge implements IItemModeChanger
{
	@SideOnly(Side.CLIENT)
	private IIcon ringOff;
	@SideOnly(Side.CLIENT)
	private IIcon ringOn;
	
	public Zero() 
	{
		super("zero_ring", (byte) 4);
		this.setContainerItem(this);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		super.onUpdate(stack, world, entity, par4, par5);
		
		if (world.isRemote || par4 > 8 || stack.getItemDamage() == 0)
		{
			return;
		}
		
		Utils.freezeNearby(world, entity);
	}
	 
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			Utils.freezeBlocks(world, new CoordinateBox(player.boundingBox), 4 + this.getCharge(stack));
			/*CoordinateBox box = new CoordinateBox(player.boundingBox);
			int offset = 4 + this.getCharge(stack);
			box.expand(offset, offset, offset);
			
			for (int x = (int) box.minX; x <= box.maxX; x++)
				for (int y = (int) box.minY; y <= box.maxY; y++)
					for (int z = (int) box.minZ; z <= box.maxZ; z++)
					{
						Block b = world.getBlock(x, y, z);
						
						if (b == Blocks.water || b == Blocks.flowing_water)
						{
							world.setBlock(x, y, z, Blocks.ice);
						}
						else if (b.isSideSolid(world, x, y, z, ForgeDirection.UP))
						{
							Block b2 = world.getBlock(x, y + 1, z);
							
							if (b2 == Blocks.air)
							{
								world.setBlock(x, y + 1, z, Blocks.snow_layer);
							}
						}
					}*/
		}
		
		return stack;
	}
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack)
	{
		return false;
	}
	
	@Override
	public void changeMode(EntityPlayer player, ItemStack stack) 
	{
		stack.setItemDamage(stack.getItemDamage() == 0 ? 1 : 0);
	}
	
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int dmg)
    {
		return dmg == 0 ? ringOff : ringOn;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		ringOn = register.registerIcon(this.getTexture("rings", "zero_on"));
		ringOff = register.registerIcon(this.getTexture("rings", "zero_off"));
	}
}
