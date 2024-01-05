package moze_intel.projecte.network;

import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.text.PELang;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.VersionChecker;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.fml.loading.FMLConfig.ConfigValue;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ComparableVersion;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class ThreadCheckUpdate extends Thread {

	private static final String curseURL = "https://www.curseforge.com/minecraft/mc-mods/projecte/files";
	private static volatile ComparableVersion target = null;
	private static volatile boolean hasSentMessage = false;

	public ThreadCheckUpdate() {
		this.setName("ProjectE Update Checker Notifier");
	}

	@Override
	public void run() {
		if (!FMLConfig.getBoolConfigValue(ConfigValue.VERSION_CHECK)) {
			//Forge update checker disabled, just exit
			return;
		}
		IModInfo info = PECore.MOD_CONTAINER.getModInfo();
		VersionChecker.CheckResult result = null;

		int tries = 0;
		do {
			VersionChecker.CheckResult res = VersionChecker.getResult(info);
			if (res.status() != VersionChecker.Status.PENDING) {
				result = res;
			}
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException ignored) {
			}
			tries++;
		} while (result == null && tries < 10);

		if (result == null) {
			PECore.LOGGER.info("Update check failed.");
			return;
		}

		if (result.status() == VersionChecker.Status.OUTDATED) {
			target = result.target();
		}
	}

	@SubscribeEvent
	public static void worldLoad(EntityJoinLevelEvent evt) {
		if (evt.getEntity() instanceof LocalPlayer player && target != null && !hasSentMessage) {
			hasSentMessage = true;
			player.sendSystemMessage(PELang.UPDATE_AVAILABLE.translate(target));
			player.sendSystemMessage(PELang.UPDATE_GET_IT.translate());
			player.sendSystemMessage(TextComponentUtil.build(new ClickEvent(ClickEvent.Action.OPEN_URL, curseURL), curseURL));
		}
	}
}