package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockClay;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class RedStar extends PEToolBase
{
	public RedStar() 
	{
		super("rm_morning_star", (byte) 4, new String[]{
				I18n.translateToLocal("pe.morningstar.mode1"), I18n.translateToLocal("pe.morningstar.mode2"),
				I18n.translateToLocal("pe.morningstar.mode3"), I18n.translateToLocal("pe.morningstar.mode4"),
		});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.pePrimaryToolClass = "morning_star";

		this.harvestMaterials.add(Material.grass);
		this.harvestMaterials.add(Material.ground);
		this.harvestMaterials.add(Material.sand);
		this.harvestMaterials.add(Material.snow);
		this.harvestMaterials.add(Material.clay);
		
		this.harvestMaterials.add(Material.iron);
		this.harvestMaterials.add(Material.anvil);
		this.harvestMaterials.add(Material.rock);

		this.harvestMaterials.add(Material.wood);
		this.harvestMaterials.add(Material.plants);
		this.harvestMaterials.add(Material.vine);

		this.secondaryClasses.add("pickaxe");
		this.secondaryClasses.add("chisel");
		this.secondaryClasses.add("shovel");
		this.secondaryClasses.add("axe");
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase damaged, EntityLivingBase damager)
	{
		boolean flag = ProjectEConfig.useOldDamage;
		attackWithCharge(stack, damaged, damager, flag ? STAR_BASE_ATTACK : 1.0F);
		return true;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase eLiving)
	{
		digBasedOnMode(stack, world, state.getBlock(), pos, eLiving);
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (!world.isRemote)
		{
			if (ProjectEConfig.pickaxeAoeVeinMining)
			{
				mineOreVeinsInAOE(stack, player);
			}

			RayTraceResult mop = this.getMovingObjectPositionFromPlayer(world, player, true);

			if (mop == null)
			{
				return ActionResult.newResult(EnumActionResult.FAIL, stack);
			}
			else if (mop.typeOfHit == Type.BLOCK)
			{
				IBlockState state = world.getBlockState(mop.getBlockPos());
				Block block = state.getBlock();

				if (block instanceof BlockGravel || block instanceof BlockClay)
				{
					if (ProjectEConfig.pickaxeAoeVeinMining)
					{
						digAOE(stack, world, player, false, 0);
					}
					else
					{
						tryVeinMine(stack, player, mop);
					}
				}
				else if (ItemHelper.isOre(state))
				{
					if (!ProjectEConfig.pickaxeAoeVeinMining)
					{
						tryVeinMine(stack, player, mop);
					}
				}
				else if (block instanceof BlockGrass || block instanceof BlockDirt || block instanceof BlockSand)
				{
					digAOE(stack, world, player, false, 0);
				}
				else
				{
					digAOE(stack, world, player, true, 0);
				}
			}
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state)
	{
		Block block = state.getBlock();
		if (block == ObjHandler.matterBlock || block == ObjHandler.dmFurnaceOff || block == ObjHandler.dmFurnaceOn || block == ObjHandler.rmFurnaceOff || block == ObjHandler.rmFurnaceOn)
		{
			return 1200000.0F;
		}
		
		return super.getStrVsBlock(stack, state) + 48.0F;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		if (ProjectEConfig.useOldDamage || slot != EntityEquipmentSlot.MAINHAND)
		{
			return super.getAttributeModifiers(slot, stack);
		}

		byte charge = stack.getTagCompound() == null ? 0 : getCharge(stack);
		float damage = STAR_BASE_ATTACK + charge;

		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", damage, 0));
		return multimap;
	}
}
