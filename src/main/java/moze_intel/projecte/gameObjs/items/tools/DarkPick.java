package moze_intel.projecte.gameObjs.items.tools;

import com.google.common.collect.Multimap;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.blocks.MatterBlock;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.Block;
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
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class DarkPick extends PEToolBase
{
	public DarkPick()
	{
		super("dm_pick", (byte)2, new String[] {
				I18n.translateToLocal("pe.darkpick.mode1"), I18n.translateToLocal("pe.darkpick.mode2"),
				I18n.translateToLocal("pe.darkpick.mode3"), I18n.translateToLocal("pe.darkpick.mode4")});
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
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (world.isRemote)
		{
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}

		if (ProjectEConfig.pickaxeAoeVeinMining)
		{
			mineOreVeinsInAOE(stack, player);
		}
		else
		{
			RayTraceResult mop = this.getMovingObjectPositionFromPlayer(world, player, false);
			if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				if (ItemHelper.isOre(world.getBlockState(mop.getBlockPos())))
				{
					tryVeinMine(stack, player, mop);
				}
			}
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase eLiving)
	{
		digBasedOnMode(stack, world, state.getBlock(), pos, eLiving);
		return true;
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state)
	{
		Block block = state.getBlock();
		if ((block == ObjHandler.matterBlock && state.getValue(MatterBlock.TIER_PROP) == MatterBlock.EnumMatterType.DARK_MATTER) || block == ObjHandler.dmFurnaceOff || block == ObjHandler.dmFurnaceOn)
		{
			return 1200000.0F;
		}
		
		return super.getStrVsBlock(stack, state);
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
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		if (slot != EntityEquipmentSlot.MAINHAND) return super.getAttributeModifiers(slot, stack);
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", this instanceof RedPick ? 8 : 7, 0));
		return multimap;
	}
}
