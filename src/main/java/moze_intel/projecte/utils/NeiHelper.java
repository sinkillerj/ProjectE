package moze_intel.projecte.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import scala.actors.threadpool.Arrays;

public final class NeiHelper 
{
	public static boolean haveNei = false;
	private static Field fldSearchField;
	private static Method mtdText;
	private static Method mtdSetText;
	private static Method mtdGetFilter;

	public static void init() 
	{
		try 
		{
			Class<?> clsLayoutManager = Class.forName("codechicken.nei.LayoutManager");
			Class<?> clsTextField = Class.forName("codechicken.nei.TextField");
			Class<?> clsSearchField = Class.forName("codechicken.nei.SearchField");
			fldSearchField = clsLayoutManager.getField("searchField");
			mtdText = clsTextField.getMethod("text");
			mtdSetText = clsTextField.getMethod("setText", String.class);
			mtdGetFilter = clsSearchField.getMethod("getFilter");
			haveNei = true;
			PELogger.logInfo("NEI helper loaded!");
			
		}
		catch (Exception e) 
		{
			PELogger.logWarn("NEI failed to load: " + e.toString());
		}
	}

	private static Object neiItemFilter = null;
	private static Method neiItemFilterMatchesMethod;
	public static void getItemFilter() {
		if (haveNei) {
			try {
				neiItemFilter = mtdGetFilter.invoke(fldSearchField.get(null));
				neiItemFilterMatchesMethod = neiItemFilter.getClass().getMethod("matches", ItemStack.class);
			} catch (Throwable t) {
				t.printStackTrace();
				neiItemFilter = null;
			}
		}
	}
	public static boolean itemFilterMatches(ItemStack stack) {
		if (neiItemFilter == null || neiItemFilterMatchesMethod == null) return true;
		try {
			Object result = neiItemFilterMatchesMethod.invoke(neiItemFilter, stack);
			if (result instanceof Boolean)
				return (Boolean)result;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return false;
	}

	public static String getSearchText() 
	{
		if (haveNei) 
		{
			try 
			{
				return (String) mtdText.invoke(fldSearchField.get(null));
			}
			catch (Throwable e) 
			{
				e.printStackTrace();
				return "";
			}
		}
		else
		{
			 return "";
		}
	}
	
	public static void resetSearchBar()
	{
		if (haveNei)
		{
			try
			{
				mtdSetText.invoke(fldSearchField.get(null), "");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
