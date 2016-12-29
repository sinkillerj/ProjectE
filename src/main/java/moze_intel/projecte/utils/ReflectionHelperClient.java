package moze_intel.projecte.utils;

import com.google.common.base.Throwables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

@SideOnly(Side.CLIENT)
public class ReflectionHelperClient
{
	private static final String[] renderItemModelIntoGUINames = { "renderItemModelIntoGUI", "func_184390_a", "a" };

	private static final MethodHandle renderItemModelIntoGUI;

	static {
		try {
			Method m = net.minecraftforge.fml.relauncher.ReflectionHelper.findMethod(RenderItem.class, null, renderItemModelIntoGUINames, ItemStack.class, int.class, int.class, IBakedModel.class);
			m.setAccessible(true);
			renderItemModelIntoGUI = MethodHandles.publicLookup().unreflect(m);
		}  catch (IllegalAccessException e) {
			throw Throwables.propagate(e);
		}
	}

	public static void renderBakedModelIntoGUI(ItemStack dummyStack, int x, int y, IBakedModel model)
	{
		try {
			renderItemModelIntoGUI.invokeExact(Minecraft.getMinecraft().getRenderItem(), dummyStack, x, y, model);
		} catch (Throwable ignored) {}
	}
}
