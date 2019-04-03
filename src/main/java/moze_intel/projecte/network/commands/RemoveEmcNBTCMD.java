package moze_intel.projecte.network.commands;

import java.util.ArrayList;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.json.NSSItemWithNBT;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.command.*;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;

public class RemoveEmcNBTCMD extends CommandBase
{
	@Nonnull
	@Override
	public String getName()
	{
		return "removeEMCNBT";
	}

	@Nonnull
	@Override
	public String getUsage(@Nonnull ICommandSender sender)
	{
		return "pe.command.remove.usage";
	}
	
	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException
	{
		ItemStack heldItem = getCommandSenderAsPlayer(sender).getHeldItem(EnumHand.MAIN_HAND);
		if (heldItem.isEmpty())
		{
			heldItem = getCommandSenderAsPlayer(sender).getHeldItem(EnumHand.OFF_HAND);
		}
		if (heldItem.isEmpty())
		{
			throw new WrongUsageException(getUsage(sender));
		}

		String[] ignores = NSSItemWithNBT.NO_IGNORES;
		if(params.length > 1){
			if(params[1].equalsIgnoreCase("ignore:")){
				ArrayList<String> ignoresList = new ArrayList<>();
				for(int k = 2; k < params.length; k++){
					if(params[k].equalsIgnoreCase("$Damage")){
						heldItem = heldItem.copy();
						heldItem.setItemDamage(0);
					}
					ignoresList.add(params[k]);
				}
				ignores = (String[]) ignoresList.toArray(); 
			}
		}
		if(CustomEMCParser.removeWithNBTFromFile(heldItem, ignores)){
			sender.sendMessage(new TextComponentTranslation("pe.command.remove.success", heldItem.getItem().getRegistryName().getPath()));
			sender.sendMessage(new TextComponentTranslation("pe.command.reload.notice"));
		}
	}
}
