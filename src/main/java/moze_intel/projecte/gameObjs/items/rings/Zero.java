package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.IModeChanger;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.ItemCharge;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.CoordinateBox;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class Zero extends ItemCharge implements IModeChanger, IBauble, IPedestalItem {
	@SideOnly(Side.CLIENT)
	private IIcon ringOff;
	@SideOnly(Side.CLIENT)
	private IIcon ringOn;
	private int coolCooldown;

	public Zero() {
		super("zero_ring", (byte) 4);
		this.setContainerItem(this);
		this.setNoRepair();
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
		super.onUpdate(stack, world, entity, par4, par5);

		if (world.isRemote || par4 > 8 || stack.getItemDamage() == 0) {
			return;
		}

		CoordinateBox box = new CoordinateBox(entity.posX - 3, entity.posY - 3, entity.posZ - 3, entity.posX + 3, entity.posY + 3, entity.posZ + 3);
		freezeInCoordinateBox(world, box);

	}

	public void freezeInCoordinateBox(World world, CoordinateBox box) {
		for (int x = (int) box.minX; x <= box.maxX; x++) {
			for (int y = (int) box.minY; y <= box.maxY; y++) {
				for (int z = (int) box.minZ; z <= box.maxZ; z++) {
					Block b = world.getBlock(x, y, z);

					if (b == Blocks.water || b == Blocks.flowing_water) {
						world.setBlock(x, y, z, Blocks.ice);
					} else if (b.isSideSolid(world, x, y, z, ForgeDirection.UP)) {
						Block b2 = world.getBlock(x, y + 1, z);

						if (b2 == Blocks.air) {
							world.setBlock(x, y + 1, z, Blocks.snow_layer);
						}
					}
				}
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			CoordinateBox box = new CoordinateBox(player.boundingBox);
			int offset = 3 + this.getCharge(stack);
			box.expand(offset, offset, offset);
			freezeInCoordinateBox(world, box);
		}

		return stack;
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
		return false;
	}

	@Override
	public byte getMode(ItemStack stack) {
		return (byte) stack.getItemDamage();
	}

	@Override
	public void changeMode(EntityPlayer player, ItemStack stack) {
		stack.setItemDamage(stack.getItemDamage() == 0 ? 1 : 0);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int dmg) {
		return dmg == 0 ? ringOff : ringOn;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		ringOn = register.registerIcon(this.getTexture("rings", "zero_on"));
		ringOff = register.registerIcon(this.getTexture("rings", "zero_off"));
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.RING;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		this.onUpdate(stack, player.worldObj, player, 0, false);
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	public void updateInPedestal(World world, int x, int y, int z) {
		if (!world.isRemote && ProjectEConfig.zeroPedCooldown != -1) {
			if (coolCooldown == 0) {
				TileEntity tile = world.getTileEntity(x, y, z);
				AxisAlignedBB aabb = ((DMPedestalTile) tile).getEffectBounds();
				freezeInCoordinateBox(world, CoordinateBox.fromAABB(aabb));
				List<Entity> list = world.getEntitiesWithinAABB(Entity.class, aabb);
				for (Entity ent : list) {
					if (ent.isBurning()) {
						ent.extinguish();
					}
				}
				coolCooldown = ProjectEConfig.zeroPedCooldown;
			} else {
				coolCooldown--;
			}
		}
	}

	@Override
	public List<String> getPedestalDescription() {
		List<String> list = new ArrayList<String>();
		if (ProjectEConfig.zeroPedCooldown != -1) {
			list.add(EnumChatFormatting.BLUE + "Extinguishes nearby entities");
			list.add(EnumChatFormatting.BLUE + "Freezes surroundings");
			list.add(EnumChatFormatting.BLUE + "Activates every " + Utils.tickToSecFormatted(ProjectEConfig.zeroPedCooldown));
		}
		return list;
	}
}
