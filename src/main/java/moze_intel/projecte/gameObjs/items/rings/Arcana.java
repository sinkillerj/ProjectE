package moze_intel.projecte.gameObjs.items.rings;

//Need to finish implementation
/*public class Arcana extends ItemPE implements IModeChanger
{
	private final String[] MODES = new String[] {"Zero", "Ignition", "Harvest", "SWRG"};
	
	@SideOnly(Side.CLIENT)
	private IIcon[] icons = new IIcon[4];
	
	public Arcana()
	{
		super();
		this.setUnlocalizedName("arcana_ring");
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isHeld) 
	{
		if (stack.stackTagCompound == null)
		{
			stack.stackTagCompound = new NBTTagCompound();
		}
		
		if (world.isRemote || slotIndex > 8 || !stack.stackTagCompound.getBoolean("Active") || !(entity instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		EntityPlayerMP playerMP = (EntityPlayerMP) entity;
		
		if (player.capabilities.isCreativeMode)
		{
			return;
		}
		
		if (!playerMP.capabilities.allowFlying)
		{
			Utils.setPlayerFlight(playerMP, true);
			PlayerChecksEvent.addPlayerFlyChecks(playerMP);
		}
		
		if (!player.isImmuneToFire())
		{
			Utils.setPlayerFireImmunity(player, true);
			PlayerChecksEvent.addPlayerFireChecks((EntityPlayerMP) player);
		}
		
		switch (stack.getItemDamage())
		{
			case 0:
				Utils.freezeNearby(world, entity);
				break;
			case 1:
				Utils.igniteNearby(world, entity);
				break;
			case 2:
				Utils.growNearbyRandomly(true, world, entity);
				break;
			case 3:
				Utils.repellEntities(player);
				break;
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			if (stack.stackTagCompound.getBoolean("Active"))
			{
				stack.stackTagCompound.setBoolean("Active", false);
			}
			else
			{
				stack.stackTagCompound.setBoolean("Active", true);
			}
		}
		
		return stack;
	}
	

	@Override
	public void changeMode(EntityPlayer player, ItemStack stack) 
	{
		if (stack.stackTagCompound.getBoolean("Active"))
		{
			int dmg = stack.getItemDamage();
			
			if (dmg < 3)
			{
				stack.setItemDamage(++dmg);
			}
			else
			{
				stack.setItemDamage(0);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int dmg)
	{
		return icons[MathHelper.clamp_int(dmg, 0, 4)];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		for (int i = 0; i < 4; i++)
		{
			icons[i] = register.registerIcon(this.getTexture("rings", "arcana_" + i));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) 
	{
		if (stack.hasTagCompound())
		{
			if (!stack.stackTagCompound.getBoolean("Active"))
			{
				list.add(EnumChatFormatting.RED+"Not active!");
			}
			else
			{
				list.add("Mode: "+EnumChatFormatting.AQUA+MODES[stack.getItemDamage()]);
			}
		}
	}
}*/
