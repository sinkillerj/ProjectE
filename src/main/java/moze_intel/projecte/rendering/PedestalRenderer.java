package moze_intel.projecte.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import moze_intel.projecte.gameObjs.block_entities.DMPedestalBlockEntity;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PedestalRenderer implements BlockEntityRenderer<DMPedestalBlockEntity, PedestalRenderState> {

	private final ItemModelResolver itemModelResolver;

	public PedestalRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	@Override
	public @NotNull PedestalRenderState createRenderState() {
		return new PedestalRenderState();
	}

	@Override
	public void extractRenderState(@NotNull DMPedestalBlockEntity pedestal, @NotNull PedestalRenderState state, float partialTick, @NotNull Vec3 cameraPos,
			@Nullable net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderState.extractBase(pedestal, state, crumblingOverlay);
		state.item = pedestal.getInventory().getStackInSlot(0);
		state.partialTick = partialTick;
		state.gameTime = pedestal.getLevel() != null ? pedestal.getLevel().getGameTime() : 0;
		state.effectBounds = pedestal.getEffectBounds();
	}

	@Override
	public void submit(@NotNull PedestalRenderState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, @NotNull CameraRenderState camera) {
		ItemStack stack = state.item;
		if (stack.isEmpty()) {
			return;
		}
		poseStack.pushPose();
		poseStack.translate(0.5, 0.7, 0.5);
		float bob = Mth.sin((state.gameTime + state.partialTick) / 10.0F) * 0.1F + 0.1F;
		poseStack.translate(0, bob, 0);
		poseStack.scale(0.75F, 0.75F, 0.75F);
		float angle = (state.gameTime + state.partialTick) / SharedConstants.TICKS_PER_SECOND;
		poseStack.rotateDegrees(Axis.YP, angle * (180.0F / (float) Math.PI));

		ItemStackRenderState itemRenderState = new ItemStackRenderState();
		var level = Minecraft.getInstance().level;
		itemModelResolver.updateForTopItem(itemRenderState, stack, ItemDisplayContext.GROUND, level, null, (int) state.blockPos.asLong());
		itemRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
	}
}
