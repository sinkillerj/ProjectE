package moze_intel.projecte.gameObjs.items.armor;

import java.util.List;

import org.lwjgl.input.Keyboard;

import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;
import moze_intel.projecte.events.PlayerChecksEvent;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.StepHeightPKT;
import moze_intel.projecte.utils.KeyBinds;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;


public class GemArmor extends ItemArmor implements ISpecialArmor, IGoggles, IRevealer
{
	public GemArmor(int armorType)
	{
		super(ArmorMaterial.DIAMOND, 0, armorType);
		this.setCreativeTab(ObjHandler.cTab);
		this.setUnlocalizedName("gem_armor_"+armorType);
		this.setHasSubtypes(false);
		this.setMaxDamage(0);
	}

    @Override
    public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
        if (itemstack != null && itemstack.getItem() == ObjHandler.gemHelmet)
             return true;
        return false;
    }

    @Override
    public boolean showNodes(ItemStack itemstack, EntityLivingBase player)
    {
        if (itemstack != null && itemstack.getItem() == ObjHandler.gemHelmet)
            return true;
        return false;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
	{
		if (world.isRemote)
		{
			if (stack.getItem() == ObjHandler.gemChest)
			{
				int x = (int) Math.floor(player.posX);
				int y = (int) (player.posY - player.getYOffset());
				int z = (int) Math.floor(player.posZ);
				
				Block b = world.getBlock(x, y - 1, z);
		
				if ((b.equals(Blocks.water) || b.equals(Blocks.flowing_water) || b.equals(Blocks.lava) || b.equals(Blocks.flowing_lava)) && world.getBlock(x, y, z).equals(Blocks.air))
				{
					if (!player.isSneaking())
					{
						player.motionY = 0.0d;
						player.fallDistance = 0.0f;
						player.onGround = true;
					}
				}
			}
		}
		else
		{
			Item armor = stack.getItem();
			EntityPlayerMP playerMP = (EntityPlayerMP) player;
			
			if (armor == ObjHandler.gemFeet)
			{
				if (!playerMP.capabilities.allowFlying)
				{
					enableFlight(playerMP);
				}
				
				if (isStepAssistEnabled(stack) && !PlayerChecksEvent.isPlayerCheckedForStep(playerMP.getCommandSenderName()))
				{
					PacketHandler.sendTo(new StepHeightPKT(1.0f), playerMP);
					PlayerChecksEvent.addPlayerStepChecks(playerMP);
				}
			}
			else if (armor == ObjHandler.gemLegs)
			{
				player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 1, 4));
				
				if (!player.isSneaking())
				{
					player.addPotionEffect(new PotionEffect(Potion.jump.id, 1, 4));
				}
			}
			else if (armor == ObjHandler.gemChest)
			{
				if (!stack.hasTagCompound())
				{
					stack.stackTagCompound = new NBTTagCompound();
				}
				
				byte coolDown = stack.stackTagCompound.getByte("Cooldown");
				
				if (coolDown > 0)
				{
					stack.stackTagCompound.setByte("Cooldown", (byte) (coolDown - 1));
				}
				else if (player.getFoodStats().needFood())
				{
					player.getFoodStats().addStats(2, 10);
					stack.stackTagCompound.setByte("Cooldown", (byte) 19);
				}
				
				if (!player.isImmuneToFire())
				{
					Utils.setPlayerFireImmunity(player, true);
					PlayerChecksEvent.addPlayerFireChecks(playerMP);
				}
			}
			else if (armor == ObjHandler.gemHelmet)
			{
				if (!stack.hasTagCompound())
				{
					stack.stackTagCompound = new NBTTagCompound();
				}
				
				byte coolDown = stack.stackTagCompound.getByte("Cooldown");
				
				if (coolDown > 0)
				{
					stack.stackTagCompound.setByte("Cooldown", (byte) (coolDown - 1));
				}
				else if (player.getHealth() < player.getMaxHealth())
				{
					player.setHealth(player.getHealth() + 2);
					stack.stackTagCompound.setByte("Cooldown", (byte) 19);
				}
				
				if (isNightVisionEnabled(stack))
				{
					if (world.getBlockLightValue((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ)) < 10)
					{
						player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 220, 0));
					}
					else if (player.isPotionActive(Potion.nightVision.id))
					{
						player.removePotionEffect(Potion.nightVision.id);
					}
				}
				else if (player.isPotionActive(Potion.nightVision.id))
				{
					player.removePotionEffect(Potion.nightVision.id);
				}
				
				if (player.isInWater())
				{
					player.setAir(300);
				}
			}
		}
	}
	
	public void enableFlight(EntityPlayerMP playerMP)
	{
		if (playerMP.capabilities.isCreativeMode)
		{
			return;
		}
		
		Utils.setPlayerFlight(playerMP, true);
		PlayerChecksEvent.addPlayerFlyChecks(playerMP);
	}
	
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) 
	{
		if (slot == 0 && source == DamageSource.fall)
		{
			return new ArmorProperties(1, 1.0D, 15);
		}
		
		if (slot == 0 || slot == 3)
		{
			return new ArmorProperties(0, 0.2D, 250);
		}

		return new ArmorProperties(0, 0.3D, 375);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) 
	{
		if (slot == 0 || slot == 3)
		{
	      	return 4;
		}
		
		return 6;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) 
	{
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister par1IconRegister)
    {
		String type = null;
		
		switch (this.armorType)
		{
			case 0:
				type = "head";
				break;
			case 1:
				type = "chest";
				break;
			case 2:
				type = "legs";
				break;
			case 3:
				type = "feet";
				break;
		}
		
        this.itemIcon = par1IconRegister.registerIcon("projecte:gem_armor/"+type);
    }
	
	public static void toggleStepAssist(ItemStack boots, EntityPlayer player)
	{
		if (!boots.hasTagCompound())
		{
			boots.setTagCompound(new NBTTagCompound());
		}
		
		boolean value;
		
		if (boots.stackTagCompound.hasKey("StepAssist"))
		{
			boots.stackTagCompound.setBoolean("StepAssist", !boots.stackTagCompound.getBoolean("StepAssist"));
			value = boots.stackTagCompound.getBoolean("StepAssist");
		}
		else
		{
			boots.stackTagCompound.setBoolean("StepAssist", false);
			value = false;
		}
		
		player.addChatMessage(new ChatComponentText("Step Assist: " + (value ? (EnumChatFormatting.GREEN + "enabled") : (EnumChatFormatting.RED + "disabled"))));
	}
	
	public static void toggleNightVision(ItemStack helm, EntityPlayer player)
	{
		if (!helm.hasTagCompound())
		{
			helm.setTagCompound(new NBTTagCompound());
		}
		
		boolean value;
		
		if (helm.stackTagCompound.hasKey("NightVision"))
		{
			helm.stackTagCompound.setBoolean("NightVision", !helm.stackTagCompound.getBoolean("NightVision"));
			value = helm.stackTagCompound.getBoolean("NightVision");
		}
		else
		{
			helm.stackTagCompound.setBoolean("NightVision", false);
			value = false;
		}
		
		player.addChatMessage(new ChatComponentText("Night Vision: " + (value ? (EnumChatFormatting.GREEN + "enabled") : (EnumChatFormatting.RED + "disabled"))));
	}
	
	public static boolean isStepAssistEnabled(ItemStack boots)
	{
		if (!boots.hasTagCompound() || !boots.stackTagCompound.hasKey("StepAssist"))
		{
			return true;
		}
		
		return boots.stackTagCompound.getBoolean("StepAssist");
	}
	
	public static boolean isNightVisionEnabled(ItemStack helm)
	{
		if (!helm.hasTagCompound() || !helm.stackTagCompound.hasKey("NightVision"))
		{
			return true;
		}
		
		return helm.stackTagCompound.getBoolean("NightVision");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) 
	{
		if (stack != null)
		{
			if (stack.getItem() == ObjHandler.gemFeet)
			{
				if (KeyBinds.getArmorEffectsKeyCode() >= 0 && KeyBinds.getArmorEffectsKeyCode() < Keyboard.getKeyCount())
				{
					list.add("Press " + Keyboard.getKeyName(KeyBinds.getArmorEffectsKeyCode()) + " to toggle step assist");
				}
				
				list.add("Step assist: " + (isStepAssistEnabled(stack) ? "enabled" : "disabled"));
			}
			else if (stack.getItem() == ObjHandler.gemHelmet)
			{
				if (KeyBinds.getArmorEffectsKeyCode() >= 0 && KeyBinds.getArmorEffectsKeyCode() < Keyboard.getKeyCount())
				{
					list.add("Press Shift+" + Keyboard.getKeyName(KeyBinds.getArmorEffectsKeyCode()) + " to toggle night vision");
				}
				
				list.add("Night Vision: " + (isNightVisionEnabled(stack) ? "enabled" : "disabled"));
			}
		}
	}

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, String type)
    {
    	char index = this.armorType == 2 ? '2' : '1';
        return "projecte:textures/armor/gem_"+index+".png";
    }
}