package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import moze_intel.projecte.api.item.IExtraFunction;
import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class Arcana extends ItemPE implements IBauble, IModeChanger, IFlightProvider, IFireProtector, IExtraFunction, IProjectileShooter
{
	private IIcon[] icons = new IIcon[4];
	private IIcon[] iconsOn = new IIcon[4];
	
	public Arcana()
	{
		super();
		setUnlocalizedName("arcana_ring");
		setMaxStackSize(1);
		setNoRepair();
		setContainerItem(this);
	}
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack)
	{
		return false;
	}

	@Override
	public byte getMode(ItemStack stack)
	{
		return (byte)stack.getItemDamage();
	}

	@Override
	public void changeMode(EntityPlayer player, ItemStack stack)
	{
		stack.setItemDamage((stack.getItemDamage() + 1) % 4);
	}
	
	private void tick(ItemStack stack, World world, EntityPlayerMP player)
	{
		if(stack.getTagCompound().getBoolean("Active"))
		{
			switch(stack.getItemDamage())
			{
				case 0:
					WorldHelper.freezeInBoundingBox(world, player.boundingBox.expand(5, 5, 5), player, true);
					break;
				case 1:
					WorldHelper.igniteNearby(world, player);
					break;
				case 2:
					WorldHelper.growNearbyRandomly(true, world, player.posX, player.posY, player.posZ, player);
					break;
				case 3:
					WorldHelper.repelEntitiesInAABBFromPoint(world, player.boundingBox.expand(5, 5, 5), player.posX, player.posY, player.posZ, true);
					break;
			}
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held)
	{
		if(stack.stackTagCompound == null) stack.setTagCompound(new NBTTagCompound());
		
		if(world.isRemote || slot > 8 || !(entity instanceof EntityPlayerMP)) return;
		
		tick(stack, world, (EntityPlayerMP)entity);
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public BaubleType getBaubleType(ItemStack stack)
	{
		return BaubleType.RING;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase entity)
	{
		if(stack.stackTagCompound == null) stack.setTagCompound(new NBTTagCompound());
		
		if(entity.worldObj.isRemote || !(entity instanceof EntityPlayerMP)) return;
		
		tick(stack, entity.worldObj, (EntityPlayerMP)entity);
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onEquipped(ItemStack stack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onUnequipped(ItemStack stack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canEquip(ItemStack stack, EntityLivingBase player)
	{
		return true;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canUnequip(ItemStack stack, EntityLivingBase player)
	{
		return true;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass)
	{
		return getIconIndex(stack);
	}

	@Override
	public IIcon getIconIndex(ItemStack stack)
	{
		boolean active = stack.hasTagCompound() && stack.getTagCompound().getBoolean("Active");
		return (active ? iconsOn : icons)[MathHelper.clamp_int(stack.getItemDamage(), 0, 3)];
	}

	@Override
	public void registerIcons(IIconRegister register)
	{
		for(int i = 0; i < 4; i++)
		{
			icons[i] = register.registerIcon(this.getTexture("rings", "arcana_" + i));
		}
		
		for(int i = 0; i < 4; i++)
		{
			iconsOn[i] = register.registerIcon(this.getTexture("rings", "arcana_" + i + "_on"));
		}
		
		itemIcon = icons[0];
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
	{
		if(stack.hasTagCompound())
		{
			if(!stack.stackTagCompound.getBoolean("Active"))
			{
				list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("pe.arcana.inactive"));
			}
			else
			{
				list.add(StatCollector.translateToLocal("pe.arcana.mode") + EnumChatFormatting.AQUA + StatCollector.translateToLocal("pe.arcana.mode." + stack.getItemDamage()));
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(!world.isRemote)
		{
			NBTTagCompound compound = stack.getTagCompound();
			
			compound.setBoolean("Active", !compound.getBoolean("Active"));
		}
		
		return stack;
	}

	@Override
	public void doExtraFunction(ItemStack stack, EntityPlayer player) // GIANT FIRE ROW OF DEATH
	{
		World world = player.worldObj;
		
		if(world.isRemote) return;
		
		switch(stack.getItemDamage())
		{
			case 1: // ignition
				switch(MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5) & 3)
				{
					case 0: // south, -z
					case 2: // north, +z
						for(int x = (int) (player.posX - 30); x <= player.posX + 30; x++)
							for(int y = (int) (player.posY - 5); y <= player.posY + 5; y++)
								for(int z = (int) (player.posZ - 3); z <= player.posZ + 3; z++)
									if(world.isAirBlock(x, y, z))
									{
										PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), x, y, z, Blocks.fire, 0);
									}
						break;
					case 1: // west, -x
					case 3: // east, +x
						for(int x = (int) (player.posX - 3); x <= player.posX + 3; x++)
							for(int y = (int) (player.posY - 5); y <= player.posY + 5; y++)
								for(int z = (int) (player.posZ - 30); z <= player.posZ + 30; z++)
								{
									if(world.isAirBlock(x, y, z))
									{
										PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), x, y, z, Blocks.fire, 0);
									}
								}
						break;
				}
				break;
		}
	}

	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack)
	{
		World world = player.worldObj;
		
		if(world.isRemote) return false;
		
		switch(stack.getItemDamage())
		{
			case 0: // zero
				EntitySnowball snowball = new EntitySnowball(world, player);
				world.spawnEntityInWorld(snowball);
				world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F);
				break;
			case 1: // ignition
				EntityFireProjectile fire = new EntityFireProjectile(world, player);
				world.spawnEntityInWorld(fire);
				world.playSoundAtEntity(player, "projecte:item.pepower", 1.0F, 1.0F);
				break;
			case 3: // swrg
				EntitySWRGProjectile lightning = new EntitySWRGProjectile(world, player);
				world.spawnEntityInWorld(lightning);
				// world.playSoundAtEntity(player, "projecte:item.pewindmagic", 1.0F, 1.0F);
				break;
		}
		
		return true;
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, EntityPlayerMP player)
	{
		return true;
	}

	@Override
	public boolean canProvideFlight(ItemStack stack, EntityPlayerMP player)
	{
		return true;
	}
}
