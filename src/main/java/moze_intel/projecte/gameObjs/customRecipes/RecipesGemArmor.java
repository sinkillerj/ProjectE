package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.Constants;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public abstract class RecipesGemArmor implements IRecipe
{
    protected ItemStack output;

    public static class Helm extends RecipesGemArmor
    {
        @Override
        public boolean matches(InventoryCrafting inv, World world)
        {
            boolean foundKlein = false;
            boolean foundArmor = false;
            boolean foundEvertide = false;
            boolean foundSoul = false;

            for (int i = 0; i < inv.getSizeInventory(); i++)
            {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack == null)
                {
                    continue;
                }

                if (stack.getItem() == ObjHandler.kleinStars
                        && stack.getItemDamage() == 5
                        && stack.getTagCompound().getDouble("StoredEMC") == Constants.MAX_KLEIN_EMC[5])
                {
                    foundKlein = true;
                }

                if (stack.getItem() == ObjHandler.rmHelmet)
                {
                    foundArmor = true;
                }

                if (stack.getItem() == ObjHandler.soulStone)
                {
                    foundSoul = true;
                }

                if (stack.getItem() == ObjHandler.everTide)
                {
                    foundEvertide = true;
                }
            }
            boolean flag = foundArmor && foundKlein && foundEvertide && foundSoul && countNonnull(inv) == 4;
            if (flag)
            {
                output = new ItemStack(ObjHandler.gemHelmet);
            }
            return flag;
        }

    }

    public static class Chest extends RecipesGemArmor
    {
        @Override
        public boolean matches(InventoryCrafting inv, World world)
        {
            boolean foundArmor = false;
            boolean foundKlein = false;
            boolean foundBody = false;
            boolean foundVolcanite = false;

            for (int i = 0; i < inv.getSizeInventory(); i++)
            {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack == null)
                {
                    continue;
                }

                if (stack.getItem() == ObjHandler.kleinStars
                        && stack.getItemDamage() == 5
                        && stack.getTagCompound().getDouble("StoredEMC") == Constants.MAX_KLEIN_EMC[5])
                {
                    foundKlein = true;
                }

                if (stack.getItem() == ObjHandler.rmChest)
                {
                    foundArmor = true;
                }

                if (stack.getItem() == ObjHandler.bodyStone)
                {
                    foundBody = true;
                }

                if (stack.getItem() == ObjHandler.volcanite)
                {
                    foundVolcanite = true;
                }
            }
            boolean flag = foundArmor && foundKlein && foundVolcanite && foundBody && countNonnull(inv) == 4;
            if (flag)
            {
                output = new ItemStack(ObjHandler.gemChest);
            }
            return flag;
        }
    }

    public static class Legs extends RecipesGemArmor
    {
        @Override
        public boolean matches(InventoryCrafting inv, World world)
        {
            boolean foundArmor = false;
            boolean foundKlein = false;
            boolean foundGoed = false;
            boolean foundTimewatch = false;

            for (int i = 0; i < inv.getSizeInventory(); i++)
            {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack == null)
                {
                    continue;
                }

                if (stack.getItem() == ObjHandler.kleinStars
                        && stack.getItemDamage() == 5
                        && stack.getTagCompound().getDouble("StoredEMC") == Constants.MAX_KLEIN_EMC[5])
                {
                    foundKlein = true;
                }

                if (stack.getItem() == ObjHandler.rmLegs)
                {
                    foundArmor = true;
                }

                if (stack.getItem() == ObjHandler.timeWatch)
                {
                    foundTimewatch = true;
                }

                if (stack.getItem() == ObjHandler.eternalDensity)
                {
                    foundGoed = true;
                }
            }
            boolean flag = foundArmor && foundKlein && foundTimewatch && foundGoed && countNonnull(inv) == 4;
            if (flag)
            {
                output = new ItemStack(ObjHandler.gemLegs);
            }
            return flag;
        }
    }

    public static class Feet extends RecipesGemArmor
    {
        @Override
        public boolean matches(InventoryCrafting inv, World world)
        {
            boolean foundArmor = false;
            boolean foundKlein = false;
            int swrgCount = 0;

            for (int i = 0; i < inv.getSizeInventory(); i++)
            {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack == null)
                {
                    continue;
                }

                if (stack.getItem() == ObjHandler.kleinStars
                        && stack.getItemDamage() == 5
                        && stack.getTagCompound().getDouble("StoredEMC") == Constants.MAX_KLEIN_EMC[5])
                {
                    foundKlein = true;
                }

                if (stack.getItem() == ObjHandler.rmFeet)
                {
                    foundArmor = true;
                }

                if (stack.getItem() == ObjHandler.swrg)
                {
                    swrgCount++;
                }
            }
            boolean flag = foundArmor && foundKlein && swrgCount == 2 && countNonnull(inv) == 4;
            if (flag)
            {
                output = new ItemStack(ObjHandler.gemFeet);
            }
            return flag;
        }
    }


    @Override
    public abstract boolean matches(InventoryCrafting p_77569_1_, World p_77569_2_);

    @Override
    public ItemStack getCraftingResult(InventoryCrafting p_77572_1_)
    {
        return output.copy();
    }

    @Override
    public int getRecipeSize()
    {
        return 4;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return output;
    }

    // Count nonnull stacks to verify all the other slots are blank.
    protected int countNonnull(IInventory inv)
    {
        int counter = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            if (inv.getStackInSlot(i) != null)
            {
                counter++;
            }
        }
        return counter;
    }
}
