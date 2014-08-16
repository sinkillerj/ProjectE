package moze_intel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import moze_intel.MozeCore;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

/**
*	Checks for updates and notifies the client/server if an update is available.
*	Contributions from condorcraft110, thanks buddy!
*/
public class ThreadCheckUpdate extends Thread
{
	private boolean isServerSide;
	
	public ThreadCheckUpdate(boolean isServer) 
	{
		this.isServerSide = isServer;
	}
	
	@Override
	public void run()
	{
		HttpURLConnection connection = null;
		BufferedReader reader = null; 
		
		try
		{
			connection = (HttpURLConnection) new URL("https://www.dropbox.com/sh/0re51mv4tp5l0xx/AAD9pmU4zOSQDSVgjVCtB5IJa").openConnection();
			connection.connect();
			
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line;
			String latestVersion = null;
			
			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("$j") && line.contains("ProjectE-Alpha"))
				{
					latestVersion = line.substring(line.indexOf("ProjectE-") + 9, line.indexOf(".jar"));
				}
			}
			
			if (!MozeCore.VERSION.equals(latestVersion))
			{
				MozeCore.logger.logInfo("Mod is outdated! Check https://github.com/MozeIntel/ProjectE to get the latest version ("+latestVersion+").");
				
				if (!isServerSide)
				{
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("New update for Project-E is available! Version: "+latestVersion+" https://github.com/MozeIntel/ProjectE"));
				}
			}
			else
			{
				MozeCore.logger.logInfo("Mod is updated.");
			}
			
			reader.close();
			connection.disconnect();
		}
		catch(Exception e)
		{
			MozeCore.logger.logFatal("Caught exception in Update Checker thread!");
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
					MozeCore.logger.logFatal("Caught exception in Update Checker thread!");
					e.printStackTrace();
				}
			}
			
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}
}