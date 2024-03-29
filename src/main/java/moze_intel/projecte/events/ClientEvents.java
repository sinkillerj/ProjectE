package moze_intel.projecte.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.sound.MovingSoundSWRG;
import moze_intel.projecte.network.commands.client.DumpMissingEmc;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class ClientEvents {

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (event.getEntity() instanceof EntitySWRGProjectile projectile && mc.mouseHandler.isMouseGrabbed()) {
			mc.getSoundManager().play(new MovingSoundSWRG(projectile, event.getLevel().getRandom()));
		}
	}

	@SubscribeEvent
	public static void registerClientCommands(RegisterClientCommandsEvent event) {
		CommandBuildContext context = event.getBuildContext();
		//Note: We can use projecte as the base command here as it will merge the trees properly
		event.getDispatcher().register(Commands.literal("projecte")
				.then(DumpMissingEmc.register(context))
		);
	}
}