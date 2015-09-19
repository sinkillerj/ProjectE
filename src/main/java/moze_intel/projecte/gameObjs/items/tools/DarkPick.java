package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class DarkPick extends PEToolBase
{
	public DarkPick()
	{
		super("dm_pick", (byte)2, new String[] {
				StatCollector.translateToLocal("pe.darkpick.mode1"), StatCollector.translateToLocal("pe.darkpick.mode2"),
				StatCollector.translateToLocal("pe.darkpick.mode3"), StatCollector.translateToLocal("pe.darkpick.mode4")});
		this.setNoRepair();
		this.peToolMaterial = "dm_tools";
		this.pePrimaryToolClass = "pickaxe";
		this.harvestMaterials.add(Material.iron);
		this.harvestMaterials.add(Material.anvil);
		this.harvestMaterials.add(Material.rock);
	}

	// Only for RedPick
	protected DarkPick(String name, byte numCharges, String[] modeDesc)
	{
		super(name, numCharges, modeDesc);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote)
		{
			return stack;
		}

		if (ProjectEConfig.pickaxeAoeVeinMining)
		{
			mineOreVeinsInAOE(stack, player);
		}
		else
		{
			MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
			if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			{
				Block b = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
				if (ItemHelper.isOre(b, world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ)))
				{
					tryVeinMine(stack, player, mop);
				}
			}
		}

		return stack;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase eLiving)
	{
		digBasedOnMode(stack, world, block, x, y, z, eLiving);
		return true;
	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int metadata)
	{
		if ((block == ObjHandler.matterBlock && metadata == 0) || block == ObjHandler.dmFurnaceOff || block == ObjHandler.dmFurnaceOn)
		{
			return 1200000.0F;
		}
		
		return super.getDigSpeed(stack, block, metadata);
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) 
	{
		super.onCreated(stack, world, player);
		
		if (!world.isRemote)
		{
			player.addStat(AchievementHandler.DM_PICK, 1);
		}
	}

	@Override
	public Multimap getAttributeModifiers(ItemStack stack)
	{
		Multimap multimap = super.getAttributeModifiers(stack);
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Tool modifier", this instanceof RedPick ? 8 : 7, 0));
		return multimap;
	}
}
