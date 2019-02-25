package moze_intel.projecte.integration.jei.mappers;

import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.integration.jei.collectors.CollectorRecipeCategory;
import moze_intel.projecte.integration.jei.collectors.FuelUpgradeRecipe;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.item.ItemStack;

public class JEIFuelMapper extends JEICompatMapper<FuelUpgradeRecipe>
{
    public JEIFuelMapper()
    {
        super(CollectorRecipeCategory.UID);
    }

    public void refresh()
    {
        clear();
        for(SimpleStack stack : FuelMapper.getFuelMap())
        {
            ItemStack fuelUpgrade = FuelMapper.getFuelUpgrade(stack.toItemStack());
            if (EMCHelper.getEmcValue(stack.toItemStack()) <= EMCHelper.getEmcValue(fuelUpgrade))
            {
                addRecipe(new FuelUpgradeRecipe(stack.toItemStack(), fuelUpgrade));
            }
        }
    }
}