package moze_intel.projecte.gameObjs.gui;

import moze_intel.projecte.manual.ManualPageHandler;
import moze_intel.projecte.manual.PEManualPage.type;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GUIManual extends GuiScreen {
	
	private ResourceLocation bookTexture = new ResourceLocation("projecte:textures/gui/bookTexture.png");
	private ResourceLocation tocTexture = new ResourceLocation("projecte:textures/gui/bookTexture.png");
	private static ResourceLocation bookGui = new ResourceLocation("textures/gui/book.png");
	
	private int currentPage = -1;
	
		public GUIManual(){
			super();
		}
		
		@Override
		public void initGui(){
			
			int i = (this.width - 256) / 2;
			byte b0 = 2;
			
			PageTurnButton nextButton = new PageTurnButton(1, i + 210, b0 + 158, true);
			PageTurnButton prevButton = new PageTurnButton(2, i + 16, b0 + 158, false);
			TocButton tocButton = new TocButton(3, (this.width/2)-(TocButton.bWidth/2), b0+190);

	        this.buttonList.add(nextButton);
	        this.buttonList.add(prevButton);
	        this.buttonList.add(tocButton);
			
		}
		
		@Override
		public void drawScreen(int par1, int par2, float par3){		
			int yPos = 50;
			int xPos = 100;
			
			ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
				width = scaledresolution.getScaledWidth();

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			if(this.currentPage == -1){
		    	this.mc.getTextureManager().bindTexture(tocTexture);
			}else{
		    	this.mc.getTextureManager().bindTexture(bookTexture);
			}
			
		    int k = (this.width - 256) / 2;
		    this.drawTexturedModalRect(k, 5, 0, 0, 256, 180);
		    
		    if(this.currentPage > -1){
		    	if(ManualPageHandler.pages.get(currentPage).getType()==type.ITEMPAGE){
		    		this.fontRendererObj.drawString(ManualPageHandler.pages.get(currentPage).getItemName(), k + 39, 27, 0, false);
		    	}else{
		    		this.fontRendererObj.drawString(ManualPageHandler.pages.get(currentPage).getTitle(), k + 39, 27, 0, false);
		    	}
		    	if(ManualPageHandler.pages.get(currentPage).getType()==type.IMAGEPAGE){
		    		drawImage(ManualPageHandler.pages.get(currentPage).getResource(),(scaledresolution.getScaledWidth()+256)/2,80);
		    	}else{
		    		this.fontRendererObj.drawSplitString(ManualPageHandler.pages.get(currentPage).getHelpInfo(), k + 18, 45, 225, 0);
		    	}
		    }
		    
		    for(int i = 0; i < this.buttonList.size(); i++){
	            ((GuiButton) this.buttonList.get(i)).drawButton(this.mc, par1, par2);
	        }
		    
		    if(this.currentPage > -1 && ManualPageHandler.pages.get(currentPage).getItem() != null){
		    	drawItemStackToGui(mc, ManualPageHandler.pages.get(currentPage).getItem(), k + 19, 22, !(ManualPageHandler.pages.get(currentPage).getItem() instanceof ItemBlock));
		    }
		    
		    this.updateButtons();
		}
		
		@Override
		public void onGuiClosed(){
			super.onGuiClosed();
		}
		
	    @Override
		protected void actionPerformed(GuiButton par1GuiButton){
	    	if(par1GuiButton.id == 1){
	    		this.currentPage++;
	    	}else if(par1GuiButton.id == 2){
	    		this.currentPage--;
	    	}else if(par1GuiButton.id == 3){
	    		this.currentPage = -1;
	    	}
	    	
	    	this.updateButtons();
	    }
		
	    private void updateButtons(){
	    	if(this.currentPage == -1){
	    		((PageTurnButton) this.buttonList.get(0)).visible = true;
	    		((PageTurnButton) this.buttonList.get(1)).visible = false;
	    		((TocButton) this.buttonList.get(2)).visible = false;
	    	}else if(this.currentPage == ManualPageHandler.pages.size() - 1){
	    		((PageTurnButton) this.buttonList.get(0)).visible = false;
	    		((PageTurnButton) this.buttonList.get(1)).visible = true;
	    		((TocButton) this.buttonList.get(2)).visible = true;
	    	}else{
	    		((PageTurnButton) this.buttonList.get(0)).visible = true;
	    		((PageTurnButton) this.buttonList.get(1)).visible = true;
	    		((TocButton) this.buttonList.get(2)).visible = true;
	    	}
	    }
	
	    @SideOnly(Side.CLIENT)
		static class TocButton extends GuiButton{
			public static int bWidth = 30;
			public static int bHeight = 15;
			
			public TocButton(int ID, int xPos, int yPos){
				super(ID,xPos,yPos, bWidth,bHeight, "ToC");
			}
	    }
	    
		@SideOnly(Side.CLIENT)
		static class PageTurnButton extends GuiButton {
			public static int bWidth = 23;
			public static int bHeight = 13;
			private boolean bool;
			
			public PageTurnButton(int ID, int xPos, int yPos, boolean par4){
				super(ID, xPos, yPos, bWidth, bHeight, "");
				bool = par4;
			}
			
			
			@Override
			public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_){
				if(this.visible){
					boolean flag = p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width && p_146112_3_ < this.yPosition + this.height;
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					p_146112_1_.getTextureManager().bindTexture(bookGui);
					int u = 0;
					int v = 192;

					if(flag){
						u += bWidth;
					}

					if(!this.bool){
						v += bHeight;
					}
					
					this.drawTexturedModalRect(this.xPosition, this.yPosition, u, v, bWidth, bHeight);
				}
			}
			
		}
		
		public static void drawItemStackToGui(Minecraft mc, Item item, int x, int y, boolean fixLighting){
			if(fixLighting){
				GL11.glEnable(GL11.GL_LIGHTING);
			}
			
	        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	        itemRender.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), new ItemStack(item), x, y);
	        
	        if(fixLighting){
	        	GL11.glDisable(GL11.GL_LIGHTING);
	        }
	        
	        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}
		
		public static void drawItemStackToGui(Minecraft mc, Block block, int x, int y, boolean fixLighting){
			drawItemStackToGui(mc, Item.getItemFromBlock(block), x, y, fixLighting);
		}
		
		@Override
		public boolean doesGuiPauseGame(){	
			return false;
		}
		
		public void drawImage(ResourceLocation resource, int x, int y){
			TextureManager render = Minecraft.getMinecraft().renderEngine;
			render.bindTexture(resource);
			
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			GL11.glScalef(0.5F, 0.5F, 1F);
			this.drawTexturedModalRect(x, y, 0, 0, 256, 256);
			GL11.glScalef(2F, 2F, 1F);
			GL11.glDisable(GL11.GL_BLEND);

			
		}

		
}
