package moze_intel.projecte.network;

import moze_intel.projecte.PECore;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ComparableVersion;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class ThreadCheckUpdate extends Thread
{
	private static final String curseURL = "https://minecraft.curseforge.com/projects/projecte/files";
	private static volatile ComparableVersion target = null;
	private static volatile boolean hasSentMessage = false;
	
	public ThreadCheckUpdate()
	{
		this.setName("ProjectE Update Checker Notifier");
	}
	
	@Override
	public void run()
	{
		IModInfo info = ModList.get().getModContainerById(PECore.MODID).get().getModInfo();
		VersionChecker.CheckResult result = null;

		int tries = 0;
		do {
			VersionChecker.CheckResult res = VersionChecker.getResult(info);
			if (res.status != VersionChecker.Status.PENDING)
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

		if (result.status == VersionChecker.Status.OUTDATED)
		{
			target = result.target;
		}
	}

	@SubscribeEvent
	public static void worldLoad(EntityJoinWorldEvent evt)
	{
		if (evt.getEntity() instanceof ClientPlayerEntity && target != null && !hasSentMessage)
		{
			hasSentMessage = true;
			evt.getEntity().sendMessage(new TranslationTextComponent("pe.update.available", target));
			evt.getEntity().sendMessage(new TranslationTextComponent("pe.update.getit"));

			ITextComponent link = new StringTextComponent(curseURL);
			link.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, curseURL));
			evt.getEntity().sendMessage(link);
		}
	}
}
