package team.chisel.api.block;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Recipe for Chisel Blocks
 */
public class ChiselRecipe implements IRecipe {

    //Rows then collumns
    private CraftingComponent[][] values = new CraftingComponent[3][3];

    private ItemStack result;

    public ChiselRecipe(ItemStack result){
        this.result = result;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn){
        boolean noMatch = false;
        for (int i = 0 ; i < 3 ; i++){
            for (int j = 0 ; j < 3 ; j++){
                if (values[i][j] == null){
                    continue;
                }
                CraftingComponent comp = values[i][j];
                if (!comp.matches(inv.getStackInRowAndColumn(i, j))){
                    noMatch = true;
                }
            }
        }
        return !noMatch;
    }

    public ChiselRecipe withItemAt(int row, int collumn, Item item){
        if (!(row > 2 || row < 0 || collumn > 2 || collumn < 0)){
            values[row][collumn] = new CraftingComponent(Item.itemRegistry.getNameForObject(item), false);
        }
        return this;
    }

    public ChiselRecipe withBlockAt(int row, int collumn, Block block){
        if (!(row > 2 || row < 0 || collumn > 2 || collumn < 0)){
            values[row][collumn] = new CraftingComponent(Block.blockRegistry.getNameForObject(block), false);
        }
        return this;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv){
        return this.result;
    }

    @Override
    public int getRecipeSize(){
        return 3;
    }

    @Override
    public ItemStack getRecipeOutput(){
        return this.result;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv){
        return new ItemStack[0];
    }


    private class CraftingComponent {

        public boolean isBlock;

        public String domain;

        public String value;

        public CraftingComponent(String domain, String value, boolean isBlock){
            this.domain = domain;
            this.value = value;
            this.isBlock = isBlock;
        }

        public CraftingComponent(ResourceLocation loc, boolean isBlock){
            this(loc.getResourceDomain(), loc.getResourcePath(), isBlock);
        }

        public boolean matches(ItemStack stack){
            if (this.isBlock){
                return Item.getItemFromBlock(GameRegistry.findBlock(domain, value)) == stack.getItem();
            }
            else {
                return GameRegistry.findItem(domain, value) == stack.getItem();
            }
        }
    }
}
