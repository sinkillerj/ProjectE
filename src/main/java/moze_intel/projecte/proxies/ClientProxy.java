package moze_intel.projecte.proxies;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.rendering.ChestRenderer;
import moze_intel.projecte.rendering.CondenserMK2Renderer;
import moze_intel.projecte.rendering.CondenserRenderer;
import moze_intel.projecte.rendering.LayerYue;
import moze_intel.projecte.rendering.NovaCataclysmRenderer;
import moze_intel.projecte.rendering.NovaCatalystRenderer;
import moze_intel.projecte.rendering.PedestalRenderer;
import moze_intel.projecte.utils.ClientKeyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderSprite;
import net.minecraft.client.renderer.entity.RenderTippedArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class ClientProxy implements IProxy
{
	// These three following methods are here to prevent a strange crash in the dedicated server whenever packets are received
	// and the wrapped methods are called directly.

	@Override
	public void clearClientKnowledge()
	{
		Minecraft.getInstance().player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(IKnowledgeProvider::clearKnowledge);
	}

	@Override
	public IKnowledgeProvider getClientTransmutationProps()
	{
		return Minecraft.getInstance().player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).orElseThrow(NullPointerException::new);
	}

	@Override
	public IAlchBagProvider getClientBagProps()
	{
		return Minecraft.getInstance().player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY).orElseThrow(NullPointerException::new);
	}

	@Override
	public void registerKeyBinds()
	{
		ClientKeyHelper.registerMCBindings();
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent evt)
	{
		// Tile Entity
		ClientRegistry.bindTileEntitySpecialRenderer(AlchChestTile.class, new ChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CondenserTile.class, new CondenserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(CondenserMK2Tile.class, new CondenserMK2Renderer());
		ClientRegistry.bindTileEntitySpecialRenderer(DMPedestalTile.class, new PedestalRenderer());

		//Entities
		RenderingRegistry.registerEntityRenderingHandler(EntityWaterProjectile.class, createRenderFactoryForSnowball(ObjHandler.waterOrb));
		RenderingRegistry.registerEntityRenderingHandler(EntityLavaProjectile.class, createRenderFactoryForSnowball(ObjHandler.lavaOrb));
		RenderingRegistry.registerEntityRenderingHandler(EntityMobRandomizer.class, createRenderFactoryForSnowball(ObjHandler.mobRandomizer));
		RenderingRegistry.registerEntityRenderingHandler(EntityLensProjectile.class, createRenderFactoryForSnowball(ObjHandler.lensExplosive));
		RenderingRegistry.registerEntityRenderingHandler(EntityFireProjectile.class, createRenderFactoryForSnowball(ObjHandler.fireProjectile));
		RenderingRegistry.registerEntityRenderingHandler(EntitySWRGProjectile.class, createRenderFactoryForSnowball(ObjHandler.windProjectile));
		RenderingRegistry.registerEntityRenderingHandler(EntityNovaCatalystPrimed.class, NovaCatalystRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityNovaCataclysmPrimed.class, NovaCataclysmRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityHomingArrow.class, RenderTippedArrow::new);
	}

	@Override
	public void registerLayerRenderers()
	{
		Map<String, RenderPlayer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
		RenderPlayer render = skinMap.get("default");
		render.addLayer(new LayerYue(render));
		render = skinMap.get("slim");
		render.addLayer(new LayerYue(render));
	}

	private static <T extends Entity> IRenderFactory<T> createRenderFactoryForSnowball(final Item itemToRender)
	{
		return manager -> new RenderSprite<>(manager, itemToRender, Minecraft.getInstance().getItemRenderer());
	}

	@Override
	public EntityPlayer getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}

	@Override
	public boolean isJumpPressed()
	{
		return Minecraft.getInstance().gameSettings.keyBindJump.isKeyDown();
	}
}

