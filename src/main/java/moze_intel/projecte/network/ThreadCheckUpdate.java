package moze_intel.projecte.network;

import moze_intel.projecte.PECore;
import moze_intel.projecte.network.commands.ChangelogCMD;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ThreadCheckUpdate extends Thread
{
	private static boolean hasRunServer = false;
	private static boolean hasRunClient = false;
	private final String changelogURL = "https://raw.githubusercontent.com/MozeIntel/ProjectE/master/Changelog.txt";
	private final String changelogDevURL = "https://raw.githubusercontent.com/MozeIntel/ProjectE/master/ChangelogDev.txt";
	private final String githubURL = "https://github.com/MozeIntel/ProjectE";
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
			if (PECore.VERSION.contains("dev"))
			{
				connection = (HttpURLConnection) new URL(changelogDevURL).openConnection();
			}
			else
			{
				connection = (HttpURLConnection) new URL(changelogURL).openConnection();
			}

			connection.connect();
			
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line = reader.readLine();
			
			if (line == null)
			{
				PELogger.logFatal("Update check failed!");
				throw new IOException("No data from github changelog!");
			}
			
			String latestVersion = null;
			List<String> changes = new ArrayList<String>();
			
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
				PELogger.logInfo("Mod is outdated! Check " + githubURL + " to get the latest version (" + latestVersion + ").");
				
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
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("New update for Project-E is available! Version: " + latestVersion));
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Get it at " + githubURL));
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Use /projecte_log for update notes."));
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