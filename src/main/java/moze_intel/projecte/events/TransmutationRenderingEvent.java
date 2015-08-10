package moze_intel.projecte.events;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemMode;
import moze_intel.projecte.utils.MetaBlock;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class TransmutationRenderingEvent 
{
	private Minecraft mc = Minecraft.getMinecraft();
	private final List<AxisAlignedBB> renderList = Lists.newArrayList();
	private double playerX;
	private double playerY;
	private double playerZ;
	private MetaBlock transmutationResult;

	@SubscribeEvent
	public void preDrawHud(RenderGameOverlayEvent.Pre event)
	{
		if (event.type == ElementType.CROSSHAIRS)
		{
			if (transmutationResult != null)
			{
				RenderItem.getInstance().renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), transmutationResult.toItemStack(), 0, 0);
			}
		}
	}
	
	@SubscribeEvent
	public void onOverlay(DrawBlockHighlightEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		World world = player.worldObj;
		ItemStack stack = player.getHeldItem();
		
		if (stack == null || stack.getItem() != ObjHandler.philosStone)
		{
			if (transmutationResult != null)
			{
				transmutationResult = null;
			}
			
			return;
		}
		
		playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) event.partialTicks;
		playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) event.partialTicks;
		playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) event.partialTicks;
		
		MovingObjectPosition mop = event.target;
		
		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK)
		{
			ForgeDirection orientation = ForgeDirection.getOrientation(mop.sideHit);
			MetaBlock current = new MetaBlock(world, mop.blockX, mop.blockY, mop.blockZ);
			transmutationResult = WorldTransmutations.getWorldTransmutation(current, player.isSneaking());

			if (transmutationResult != null)
			{
				byte charge = ((ItemMode) stack.getItem()).getCharge(stack);

				switch (((ItemMode) stack.getItem()).getMode(stack))
				{
					case 0:
					{
						for (int x = mop.blockX - charge; x <= mop.blockX + charge; x++)
							for (int y = mop.blockY - charge; y <= mop.blockY + charge; y++)
								for (int z = mop.blockZ - charge; z <= mop.blockZ + charge; z++)
								{
									addBlockToRenderList(world, current, x, y, z);
								}
						
						break;
					}
					case 1:
					{
						int side = orientation.offsetY != 0 ? 0 : orientation.offsetX != 0 ? 1 : 2;
						
						if (side == 0)
						{
							for (int x = mop.blockX - charge; x <= mop.blockX + charge; x++)
								for (int z = mop.blockZ - charge; z <= mop.blockZ + charge; z++)
								{
									addBlockToRenderList(world, current, x, mop.blockY, z);
								}
						}
						else if (side == 1)
						{
							for (int y = mop.blockY - charge; y <= mop.blockY + charge; y++)
								for (int z = mop.blockZ - charge; z <= mop.blockZ + charge; z++)
								{
									addBlockToRenderList(world, current, mop.blockX, y, z);
								}
						}
						else
						{
							for (int x = mop.blockX - charge; x <= mop.blockX + charge; x++)
								for (int y = mop.blockY - charge; y <= mop.blockY + charge; y++)
								{
									addBlockToRenderList(world, current, x, y, mop.blockZ);
								}
						}
						
						break;
					}
					case 2:
					{
						String dir = Direction.directions[MathHelper.floor_double((double)((player.rotationYaw * 4F) / 360F) + 0.5D) & 3];
						int side = orientation.offsetX != 0 ? 0 : orientation.offsetZ != 0 ? 1 : dir.equals("NORTH") || dir.equals("SOUTH") ? 0 : 1;
						
						if (side == 0)
						{
							for (int z = mop.blockZ - charge; z <= mop.blockZ + charge; z++)
							{
								addBlockToRenderList(world, current, mop.blockX, mop.blockY, z);
							}
						}
						else 
						{
							for (int x = mop.blockX - charge; x <= mop.blockX + charge; x++)
							{
								addBlockToRenderList(world, current, x, mop.blockY, mop.blockZ);
							}
						}
						
						break;
					}
				}
				
				drawAll();
				renderList.clear();
			}
		}
		else if (transmutationResult != null)
		{
			transmutationResult = null;
		}
	}
	
	private void drawAll()
	{
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, ProjectEConfig.pulsatingOverlay ? getPulseProportion() * 0.60f : 0.35f);
		
		Tessellator tessellator = Tessellator.instance;
		
		for (AxisAlignedBB b : renderList)
		{
			//Top
			tessellator.startDrawingQuads();
			tessellator.addVertex(b.minX, b.maxY, b.minZ);
			tessellator.addVertex(b.maxX, b.maxY, b.minZ);
			tessellator.addVertex(b.maxX, b.maxY, b.maxZ);
			tessellator.addVertex(b.minX, b.maxY, b.maxZ);
			tessellator.draw();
			
			//Bottom 
			tessellator.startDrawingQuads();
			tessellator.addVertex(b.minX, b.minY, b.minZ);
			tessellator.addVertex(b.maxX, b.minY, b.minZ);
			tessellator.addVertex(b.maxX, b.minY, b.maxZ);
			tessellator.addVertex(b.minX, b.minY, b.maxZ);
			tessellator.draw();
			
			//Front
			tessellator.startDrawingQuads();
			tessellator.addVertex(b.maxX, b.maxY, b.maxZ);
			tessellator.addVertex(b.minX, b.maxY, b.maxZ);
			tessellator.addVertex(b.minX, b.minY, b.maxZ);
			tessellator.addVertex(b.maxX, b.minY, b.maxZ);
			tessellator.draw();
			
			//Back
			tessellator.startDrawingQuads();
			tessellator.addVertex(b.maxX, b.minY, b.minZ);
			tessellator.addVertex(b.minX, b.minY, b.minZ);
			tessellator.addVertex(b.minX, b.maxY, b.minZ);
			tessellator.addVertex(b.maxX, b.maxY, b.minZ);
			tessellator.draw();
			
			//Left
			tessellator.startDrawingQuads();
			tessellator.addVertex(b.minX, b.maxY, b.maxZ);
			tessellator.addVertex(b.minX, b.maxY, b.minZ);
			tessellator.addVertex(b.minX, b.minY, b.minZ);
			tessellator.addVertex(b.minX, b.minY, b.maxZ);
			tessellator.draw();
			
			//Right
			tessellator.startDrawingQuads();
			tessellator.addVertex(b.maxX, b.maxY, b.maxZ);
			tessellator.addVertex(b.maxX, b.maxY, b.minZ);
			tessellator.addVertex(b.maxX, b.minY, b.minZ);
			tessellator.addVertex(b.maxX, b.minY, b.maxZ);
			tessellator.draw();
		}

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private void addBlockToRenderList(World world, MetaBlock current, int x, int y, int z)
	{
		if (new MetaBlock(world, x, y, z).equals(current))
		{
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x - 0.02f, y - 0.02f, z - 0.02f, x + 1.02f, y + 1.02f, z + 1.02f);
			box = box.offset(-playerX, -playerY, -playerZ);
			renderList.add(box);
		}
	}

	private float getPulseProportion()
	{
		return (float) (0.5F * Math.sin(System.currentTimeMillis() / 350.0) + 0.5F);
	}
}
