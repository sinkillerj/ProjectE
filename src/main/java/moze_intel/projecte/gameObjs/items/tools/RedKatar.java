package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class RedKatar extends PEToolBase implements IExtraFunction
{
	public RedKatar() 
	{
		super("rm_katar", (byte)4, new String[] {
				"pe.katar.mode1", "pe.katar.mode2",
		});
		this.setNoRepair();
		this.peToolMaterial = "rm_tools";
		this.harvestMaterials.add(Material.WOOD);
		this.harvestMaterials.add(Material.WEB);
		this.harvestMaterials.add(Material.CLOTH);
		this.harvestMaterials.add(Material.PLANTS);
		this.harvestMaterials.add(Material.LEAVES);
		this.harvestMaterials.add(Material.VINE);

		this.toolClasses.add("katar");
		this.toolClasses.add("sword");
		this.toolClasses.add("axe");
		this.toolClasses.add("shears");
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase damaged, EntityLivingBase damager)
	{
		attackWithCharge(stack, damaged, damager, 1.0F);
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
	{
		// Shear
		shearBlock(stack, pos, player);
		return false;
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote)
		{
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}
		RayTraceResult mop = this.rayTrace(world, player, false);
		if (mop != null)
		{
			if (mop.typeOfHit == RayTraceResult.Type.BLOCK)
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
					clearOdAOE(world, stack, player, "logWood", 0, hand);
				}
				else if (blockHit instanceof BlockLeaves) {
					// Shear leaves
					clearOdAOE(world, stack, player, "treeLeaves", 0, hand);
				}
			}
		}
		else
		{
			// Shear
			shearEntityAOE(stack, player, 0, hand);
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, EnumHand hand)
	{
		if (player.getCooledAttackStrength(0F) == 1)
		{
			attackAOE(stack, player, getMode(stack) == 1, ProjectEConfig.difficulty.katarDeathAura, 0, hand);
			PlayerHelper.resetCooldown(player);
			return true;
		}
		else
		{
			return false;
		}
	}

	@Nonnull
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

	@Nonnull
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, ItemStack stack)
	{
		if (slot != EntityEquipmentSlot.MAINHAND)
		{
			return super.getAttributeModifiers(slot, stack);
		}

		int charge = getCharge(stack);
		float damage = KATAR_BASE_ATTACK + charge; // Sword

		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", damage, 0));
		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.4, 0));
		return multimap;
	}

}
