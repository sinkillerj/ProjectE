package moze_intel.projecte.network;

import moze_intel.projecte.PECore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class ThreadCheckUpdate extends Thread
{
	private static volatile boolean hasRun = false;
	private static final String curseURL = "https://minecraft.curseforge.com/projects/projecte/files";
	
	public ThreadCheckUpdate()
	{
		this.setName("ProjectE Update Checker Notifier");
	}
	
	@Override
	public void run()
	{
		hasRun = true;
		ModContainer container = Loader.instance().getIndexedModList().get(PECore.MODID);
		ForgeVersion.CheckResult result = null;

		int tries = 0;
		do {
			ForgeVersion.CheckResult res = ForgeVersion.getResult(container);
			if (res.status != ForgeVersion.Status.PENDING)
			{
				result = res;
			}
			try
			{
				Thread.sleep(1000L);
			} catch (InterruptedException ignored) {}
			tries++;
		} while (result == null && tries < 10);

		if (result == null)
		{
			PECore.LOGGER.info("Update check failed.");
			return;
		}

		if (result.status == ForgeVersion.Status.UP_TO_DATE)
		{
			PECore.LOGGER.info("Mod is updated.");
		} else if (result.status == ForgeVersion.Status.OUTDATED)
		{
			PECore.LOGGER.info("Mod is outdated! Check {} to get the latest version ({}).", curseURL, result.target);
			final ForgeVersion.CheckResult res = result;

			Minecraft.getMinecraft().addScheduledTask(() -> {
				Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("pe.update.available", res.target));
				Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("pe.update.getit"));

				ITextComponent link = new TextComponentString(curseURL);
				link.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, curseURL));
				Minecraft.getMinecraft().player.sendMessage(link);
			});
		}
	}

	public static boolean hasRun()
	{
		return hasRun;
	}
}
