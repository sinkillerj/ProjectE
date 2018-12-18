package moze_intel.projecte.manual;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemPage extends AbstractPage
{
    private final ItemStack stack;
    private final String body;

    protected ItemPage(ItemStack stack, PageCategory category, String body)
    {
        super(category);
        this.stack = stack;
        this.body = body;
    }

    public ItemStack getItemStack()
    {
        return stack.copy();
    }

    @Override
    public String getHeaderText()
    {
        return I18n.format(stack.getTranslationKey() + ".name");
    }

    @Override
    public String getBodyText()
    {
        return body;
    }
}
