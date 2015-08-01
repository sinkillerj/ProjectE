package mapeper.projecte.neirecipecollector.commands;

import moze_intel.projecte.emc.mappers.customConversions.CustomConversionMapper;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversion;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversionFile;

import mapeper.projecte.neirecipecollector.ChatUtils;
import mapeper.projecte.neirecipecollector.NEIRecipeCollector;
import net.minecraft.command.ICommandSender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class StoreSubCommand implements ISubCommand
{
	@Override
	public String getCommandName()
	{
		return "store";
	}

	@Override
	public List<String> addTabCompletionOptions(List<String> params)
	{
		return null;
	}

	@Override
	public void processCommand(String previousCommands, ICommandSender sender, List<String> params)
	{
		if (params.size() == 0) {
			ChatUtils.addChatError(sender, "Usage: %s <filename> [overwrite|merge]", previousCommands);
			ChatUtils.addChatError(sender, "Options when file is already present:");
			ChatUtils.addChatError(sender, "overwrite    Overwrite the file with the current buffer.");
//			ChatUtils.addChatError(sender, "merge        Try to merge the file with the current buffer.");
			return;
		}
		if (params.size() > 2) {
			ChatUtils.addChatError(sender, "Too many arguments...");
			return;
		}
		String filename = params.get(0);
		if (!filename.toLowerCase().endsWith(".json")) {
			filename = filename + ".json";
		}
		File f = new File(CustomConversionMapper.getCustomConversionFolder(), filename);

		boolean overwrite = false;
//		boolean merge = false;
		if (f.exists()) {
			if (params.size() == 1) {
				ChatUtils.addChatError(sender, "File %s already present. Specify 'overwrite' or 'merge' to handle this", f);
				return;
			} else if (params.size() == 2) {
				if (params.get(1).equalsIgnoreCase("overwrite")) {
					overwrite = true;
//				} else if (params.get(1).equalsIgnoreCase("merge")) {
//					merge = true;
				} else {
					ChatUtils.addChatError(sender, "'%s' is neither 'merge' nor 'overwrite'", params.get(1));
					return;
				}
			}
			//TODO merge files
		}
		if (!f.exists() || overwrite) {
			CustomConversionFile buffer = NEIRecipeCollector.getInstance().getBufferFile();
			try
			{
				buffer.write(f);
			} catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}

