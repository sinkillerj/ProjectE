package moze_intel.projecte.gameObjs.gui;

import java.util.Map.Entry;

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
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

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

			
	        this.buttonList.add(new PageTurnButton(0, i + 210, 160, true));
	        this.buttonList.add(new PageTurnButton(1, i + 16, 160, false));
	        this.buttonList.add(new TocButton(2, (this.width/2)-(TocButton.width/2), 192, 30, 15));
	        
	    	int offset = 3;
	    	int xOffset = 30;
	    	int yOffset = 0;
	    	int yValue = -1;
	    	int charHeight = Math.round(9/2.5f);
	    	
	    	for(Entry<Integer, String> entry : ManualPageHandler.pageIndexes.entrySet()){
	    		int id = entry.getKey() + offset;
	    		yValue++; //for yOffset
	    		
	    		// Note that the pixel numbers are way off due to glScale'ing done in the invisbutton class.
	    		if(yOffset >= 130){
	    			xOffset += 50;
	    			yValue = -1;
	    			
	    		}
	    		
	    		yOffset = yValue * charHeight + 25;
	    		
	    		String text = StatCollector.translateToLocal(entry.getValue()+".name");
	    		buttonList.add(new InvisButton(id, ((this.width-256)/2)+xOffset, yOffset, Math.round(mc.fontRenderer.getStringWidth(text)/2.5f), charHeight, text));

	    	}
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
		    			this.fontRendererObj.drawString(ManualPageHandler.pages.get(currentPage).getItemStackName(), k + 39, 27, 0, false);
		    	}else{
		    		this.fontRendererObj.drawString(ManualPageHandler.pages.get(currentPage).getTitle(), k + 39, 27, 0, false);
		    	}
		    	if(ManualPageHandler.pages.get(currentPage).getType()==type.IMAGEPAGE){
		    		drawImage(ManualPageHandler.pages.get(currentPage).getResource(),(scaledresolution.getScaledWidth()+256)/2,80);
		    	}else{
		    		this.fontRendererObj.drawSplitString(ManualPageHandler.pages.get(currentPage).getHelpInfo(), k + 18, 45, 225, 0);
		    	}
		    }else{
		    	//this.fontRendererObj.drawString("Index", k + 39, 27, 0, false);
		    }
		    //Back and forth buttons, adjust to account for more buttons in buttonList(second list for index?)
		    for(int i = 0; i < this.buttonList.size(); i++){
	            ((GuiButton) this.buttonList.get(i)).drawButton(this.mc, par1, par2);
	        }
		    
		    // Item icon
		    if(this.currentPage > -1 && ManualPageHandler.pages.get(currentPage).getItemStack() != null){
		    	drawItemStackToGui(mc, ManualPageHandler.pages.get(currentPage).getItemStack(), k + 19, 22, !(ManualPageHandler.pages.get(currentPage).getItemStack().getItem() instanceof ItemBlock));
		    }
		    
		    this.updateButtons();
		}
		
		@Override
		public void onGuiClosed(){
			super.onGuiClosed();
		}
		
	    @Override
		protected void actionPerformed(GuiButton par1GuiButton){
	    	if(par1GuiButton.id == 0){
	    		this.currentPage++;
	    	}else if(par1GuiButton.id == 1){
	    		this.currentPage--;
	    	}else if(par1GuiButton.id == 2){
	    		this.currentPage = -1;
	    	}else{
	    		this.currentPage = par1GuiButton.id - 3;
	    	}
	    	
	    	this.updateButtons();
	    }
		
	    private void updateButtons(){
	    	if(this.currentPage == -1){
	    		((PageTurnButton) this.buttonList.get(0)).visible = true;
	    		((PageTurnButton) this.buttonList.get(1)).visible = false;
	    		((TocButton) this.buttonList.get(2)).visible = false;
	    		for(int i=3;i<this.buttonList.size();i++){
	    			((InvisButton)this.buttonList.get(i)).visible=true;
	    		}
	    	}else if(this.currentPage == ManualPageHandler.pages.size() - 1){
	    		((PageTurnButton) this.buttonList.get(0)).visible = false;
	    		((PageTurnButton) this.buttonList.get(1)).visible = true;
	    		((TocButton) this.buttonList.get(2)).visible = true;
	    		for(int i=3;i<this.buttonList.size();i++){
	    			((InvisButton)this.buttonList.get(i)).visible=false;
	    		}
	    	}else{
	    		((PageTurnButton) this.buttonList.get(0)).visible = true;
	    		((PageTurnButton) this.buttonList.get(1)).visible = true;
	    		((TocButton) this.buttonList.get(2)).visible = true;
	    		for(int i=3;i<this.buttonList.size();i++){
	    			((InvisButton)this.buttonList.get(i)).visible=false;
	    		}
	    	}
	    }
	
	    @SideOnly(Side.CLIENT)
		static class TocButton extends GuiButton{
	    	static int width = 0;
			
			public TocButton(int ID, int xPos, int yPos, int bWidth, int bHeight){
				super(ID,xPos,yPos, bWidth,bHeight, "ToC");
				this.width = bWidth;
			}
			
	    }
	    
	    @SideOnly(Side.CLIENT)
	    static class InvisButton extends GuiButton{
	    	
	    	public InvisButton(int par1, int par2, int par3, int par4, int par5, String par6){
	    		super(par1, par2, par3, par4, par5, par6);
	    	}
	    	
	    	@Override
	    	public void drawButton(Minecraft mc, int par2, int par3){
	    	GL11.glScaled(0.4f, 0.4f, 0.4f);
	    	drawRect(xPosition, yPosition,(xPosition + width), (yPosition + height), 0);
	    	mc.fontRenderer.drawString(displayString, Math.round(xPosition*2.5f), yPosition*3, 0);

	    	GL11.glScaled(2.5f, 2.5f, 2.5f);
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
		
		public static void drawItemStackToGui(Minecraft mc, ItemStack item, int x, int y, boolean fixLighting){
			if(fixLighting){
				GL11.glEnable(GL11.GL_LIGHTING);
			}
			
	        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	        itemRender.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), item, x, y);
	        
	        if(fixLighting){
	        	GL11.glDisable(GL11.GL_LIGHTING);
	        }
	        
	        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}
		
		public static void drawItemStackToGui(Minecraft mc, Block block, int x, int y, boolean fixLighting){
			drawItemStackToGui(mc, new ItemStack(block), x, y, fixLighting);
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
