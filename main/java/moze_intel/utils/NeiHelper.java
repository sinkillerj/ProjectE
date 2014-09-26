package moze_intel.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NeiHelper {
	public static boolean haveNei = false;
	private static Field fldSearchField;
	private static Method mtdText;

	public static void init() {
		try {
			Class<?> clsLayoutManager = Class.forName("codechicken.nei.LayoutManager");
			Class<?> clsTextField = Class.forName("codechicken.nei.TextField");
			fldSearchField = clsLayoutManager.getField("searchField");
			mtdText = clsTextField.getMethod("text");
			haveNei = true;
			MozeLogger.logInfo("NEI helper loaded");
		} catch (Throwable e) {
			MozeLogger.logWarn("NEI helper not loaded: " + e.toString());
		}
	}

	public static String getSearchText() {
		if (haveNei) {
			try {
				return (String) mtdText.invoke(fldSearchField.get(null));
			} catch (Throwable e) {
				e.printStackTrace();
				return "";
			}
		} else return "";
	}
}
