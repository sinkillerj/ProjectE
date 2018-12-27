package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RecipesCovalenceRepair implements IRecipe
{
	public static final ResourceLocation ID = new ResourceLocation(PECore.MODID, "covalence_repair");
	private ItemStack output = ItemStack.EMPTY;

	@Override
	public boolean matches(@Nonnull IInventory inv, @Nonnull World world)
	{
		List<ItemStack> dust = new ArrayList<>();
		ItemStack tool = ItemStack.EMPTY;
		boolean foundItem = false;
		int dustEmc = 0;
		int emcPerDurability = 0;
		
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack input = inv.getStackInSlot(i);
			
			if (input.isEmpty())
			{
				continue;
			}
			
			if (ItemHelper.isItemRepairable(input))
			{
				if (!foundItem)
				{
					tool = input;
					foundItem = true;
					emcPerDurability = EMCHelper.getEMCPerDurability(tool);
				}
				else
				{
					return false;
				}
			}
			else if (input.getItem() == ObjHandler.covalence)
			{
				dust.add(input);
			}
		}
		
		if (tool.isEmpty() || !foundItem || dust.size() == 0)
		{
			return false;
		}

		for (ItemStack stack : dust) {
			dustEmc += EMCHelper.getEmcValue(stack);
		}
		if(dustEmc < emcPerDurability){
			return false;
		}
		
		output = tool.copy();
		output.setDamage(Math.max(tool.getDamage() - dustEmc / emcPerDurability, 0));
		return true;
	}
	
	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull IInventory var1)
	{
		return output.copy();
	}

	@Override
	public boolean canFit(int width, int height) {
		return width > 1 || height > 1;
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() 
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isDynamic()
	{
		return true;
	}

	@Override
	public ResourceLocation getId()
	{
		return ID;
	}

	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		// todo 1.13
		return null;
	}
}
