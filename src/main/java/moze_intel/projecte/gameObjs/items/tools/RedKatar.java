package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class RedKatar extends PEToolBase implements IExtraFunction
{
	public RedKatar() 
	{
		super("rm_katar", (byte)4, new String[] {
				StatCollector.translateToLocal("pe.katar.mode1"), StatCollector.translateToLocal("pe.katar.mode2"),
		});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.pePrimaryToolClass = "katar";
		this.harvestMaterials.add(Material.wood);
		this.harvestMaterials.add(Material.web);
		this.harvestMaterials.add(Material.cloth);
		this.harvestMaterials.add(Material.plants);
		this.harvestMaterials.add(Material.leaves);
		this.harvestMaterials.add(Material.vine);

		this.secondaryClasses.add("sword");
		this.secondaryClasses.add("axe");
		this.secondaryClasses.add("shears");
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase damaged, EntityLivingBase damager)
	{
		boolean flag = ProjectEConfig.useOldDamage;
		attackWithCharge(stack, damaged, damager, flag ? KATAR_BASE_ATTACK : 1.0F);
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
	{
		// Shear
		shearBlock(stack, pos, player);
		return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		if (world.isRemote)
		{
			return stack;
		}
		MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
		if (mop != null)
		{
			if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			{
				IBlockState state = world.getBlockState(mop.getBlockPos());
				Block blockHit = state.getBlock();
				if (blockHit instanceof BlockGrass || blockHit instanceof BlockDirt)
				{
					// Hoe
					tillAOE(stack, player, world, mop.getBlockPos(), mop.sideHit, 0);
				}
				else if (blockHit instanceof BlockLog)
				{
					// Axe
					clearOdAOE(world, stack, player, "logWood", 0);
				}
				else if (blockHit instanceof BlockLeaves) {
					// Shear leaves
					clearOdAOE(world, stack, player, "treeLeaves", 0);
				}
			}
		}
		else
		{
			// Shear
			shearEntityAOE(stack, player, 0);
		}
		
		return stack;
	}

	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player)
	{
		attackAOE(stack, player, getMode(stack) == 1, ProjectEConfig.katarDeathAura, 0);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.BLOCK;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}

	@Override
	public Multimap getAttributeModifiers(ItemStack stack)
	{
		if (ProjectEConfig.useOldDamage)
		{
			return super.getAttributeModifiers(stack);
		}

		byte charge = stack.getTagCompound() == null ? 0 : getCharge(stack);
		float damage = KATAR_BASE_ATTACK + charge; // Sword

		Multimap multimap = super.getAttributeModifiers(stack);
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Weapon modifier", damage, 0));
		return multimap;
	}

}
