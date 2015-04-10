package moze_intel.projecte.network;

import com.google.common.collect.Lists;
import moze_intel.projecte.PECore;
import moze_intel.projecte.network.commands.ChangelogCMD;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ThreadCheckUpdate extends Thread
{
	private static boolean hasRunServer = false;
	private static boolean hasRunClient = false;
	private final String changelogURL = "https://raw.githubusercontent.com/sinkillerj/ProjectE/master/Changelog.txt";
	private final String changelogDevURL = "https://raw.githubusercontent.com/sinkillerj/ProjectE/master/ChangelogDev.txt";
	private final String githubURL = "https://github.com/sinkillerj/ProjectE";
	private final String curseURL = "http://minecraft.curseforge.com/mc-mods/226410-projecte/files";
	private boolean isServerSide;
	
	public ThreadCheckUpdate(boolean isServer) 
	{
		this.isServerSide = isServer;
		this.setName("ProjectE Update Checker " + (isServer ? "Server" : "Client"));
	}
	
	@Override
	public void run()
	{
		HttpURLConnection connection = null;
		BufferedReader reader = null; 
		
		try
		{
			connection = (HttpURLConnection) new URL(changelogURL).openConnection();

			connection.connect();
			
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line = reader.readLine();
			
			if (line == null)
			{
				PELogger.logFatal("Update check failed!");
				throw new IOException("No data from github changelog!");
			}
			
			String latestVersion;
			List<String> changes = Lists.newArrayList();
			
			latestVersion = line.substring(11);
			latestVersion = latestVersion.trim();
					
			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("###Version"))
				{
					break;
				}
						
				if (!line.isEmpty())
				{
					line = line.substring(1).trim();
					changes.add(line);
				}
			}
			
			if (!PECore.VERSION.equals(latestVersion))
			{
				PELogger.logInfo("Mod is outdated! Check " + curseURL + " to get the latest version (" + latestVersion + ").");
				
				for (String s : changes)
				{
					PELogger.logInfo(s);
				}
				
				if (isServerSide)
				{
					ChangelogCMD.changelog.addAll(changes);
				}
				else
				{
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(String.format(StatCollector.translateToLocal("pe.update.available"), latestVersion)));
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("pe.update.getit")));

					IChatComponent link = new ChatComponentText(curseURL);
					link.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, curseURL));
					Minecraft.getMinecraft().thePlayer.addChatMessage(link);

					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("pe.update.changelog")));
				}
			}
			else
			{
				PELogger.logInfo("Mod is updated.");
			}
		}
		catch(Exception e)
		{
			PELogger.logFatal("Caught exception in Update Checker thread!");
			e.printStackTrace();
		}
		finally
		{
			if (reader != null)
			{
				try 
				{
					reader.close();
				} 
				catch (IOException e) 
				{
					PELogger.logFatal("Caught exception in Update Checker thread!");
					e.printStackTrace();
				}
			}
			
			if (connection != null)
			{
				connection.disconnect();
			}
			
			if (isServerSide)
			{
				hasRunServer = true;
			}
			else
			{
				hasRunClient = true;
			}
		}
	}
	
	public static boolean hasRunServer()
	{
		return hasRunServer;
	}
	
	public static boolean hasRunClient()
	{
		return hasRunClient;
	}
}
