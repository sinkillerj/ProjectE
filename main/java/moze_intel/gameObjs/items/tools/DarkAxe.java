package moze_intel.gameObjs.items.tools;

import java.util.ArrayList;
import java.util.List;

import moze_intel.MozeCore;
import moze_intel.gameObjs.entity.LootBall;
import moze_intel.gameObjs.items.ItemCharge;
import moze_intel.network.packets.SwingItemPKT;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DarkAxe extends ItemCharge
{
	public DarkAxe()
	{
		super("dm_axe", (byte) 3);
	}
	
	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack)
	{
		if (block.equals(Blocks.bedrock))
		{
			return false;
		}
		
		String harvest = block.getHarvestTool(0);
		
		if (harvest == null || harvest.equals("axe"))
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int metadata)
	{
		if(block.getHarvestTool(metadata) != null && block.getHarvestTool(metadata).equals("axe"))
		{
			return 14.0f + (12.0f * this.getCharge(stack));
		}
		
		return 1.0F;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
		if (!world.isRemote)
		{
			byte charge = this.getCharge(stack);
			
			if (charge == 0)
			{
				return stack;
			}
			
			List<ItemStack> drops = new ArrayList();
			
			for (int x = (int) player.posX - (5 * charge); x <= player.posX + (5 * charge); x++)
				for (int y = (int) player.posY - (10 * charge); y <= player.posY + (10 * charge); y++)
					for (int z = (int) player.posZ - (5 * charge); z <= player.posZ + (5 * charge); z++)
					{
						Block block = world.getBlock(x, y, z);
						
						if (block == null)
						{
							continue;
						}
						
						ItemStack s = new ItemStack(block);
						int[] oreIds = OreDictionary.getOreIDs(s);
						
						if (oreIds.length == 0)
						{
							continue;
						}
						
						String oreName = OreDictionary.getOreName(oreIds[0]);
						
						if (oreName.equals("logWood") || oreName.equals("treeLeaves"))
						{
							ArrayList<ItemStack> blockDrops = block.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));
						
							if (!blockDrops.isEmpty())
							{
								drops.addAll(blockDrops);
							}
						
							world.setBlockToAir(x, y, z);
						}
					}
			
			if (!drops.isEmpty())
			{
				world.spawnEntityInWorld(new LootBall(world, drops, player.posX, player.posY, player.posZ));
				MozeCore.pktHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
			}
		}
		
        return stack;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("dm_tools", "axe"));
	}
}
