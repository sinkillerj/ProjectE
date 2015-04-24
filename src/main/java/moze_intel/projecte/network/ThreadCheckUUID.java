package moze_intel.projecte.network;

import com.google.common.collect.Lists;
import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.PELogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ThreadCheckUUID extends Thread
{
	private static boolean hasRunServer = false;
	private static boolean hasRunClient = false;
	private final String uuidURL = "https://raw.githubusercontent.com/sinkillerj/ProjectE/master/haUUID.txt";
	private final String githubURL = "https://github.com/sinkillerj/ProjectE";
	private boolean isServerSide;
	
	public ThreadCheckUUID(boolean isServer) 
	{
		this.isServerSide = isServer;
		this.setName("ProjectE UUID Checker " + (isServer ? "Server" : "Client"));
	}
	
	@Override
	public void run()
	{
		HttpURLConnection connection = null;
		BufferedReader reader = null; 
		
		try
		{
			connection = (HttpURLConnection) new URL(uuidURL).openConnection();

			connection.connect();
			
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line = reader.readLine();
			
			if (line == null)
			{
				PELogger.logFatal("UUID check failed!");
				throw new IOException("No data from github UUID list!");
			}

			List<String> uuids = Lists.newArrayList();
					
			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("###UUID"))
				{
					break;
				}
						
				if (!line.isEmpty())
				{
					uuids.add(line);
				}
			}

			PECore.uuids.addAll(uuids);
		}
		catch(Exception e)
		{
			PELogger.logFatal("Caught exception in UUID Checker thread!");
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
					PELogger.logFatal("Caught exception in UUID Checker thread!");
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
