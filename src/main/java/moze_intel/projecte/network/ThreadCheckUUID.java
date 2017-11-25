package moze_intel.projecte.network;

import moze_intel.projecte.PECore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ThreadCheckUUID extends Thread
{
	private static boolean hasRunServer = false;
	private static boolean hasRunClient = false;
	private static final String uuidURL = "https://raw.githubusercontent.com/sinkillerj/ProjectE/mc1.12.x/haUUID.txt";
	private final boolean isServerSide;
	
	public ThreadCheckUUID(boolean isServer) 
	{
		this.isServerSide = isServer;
		this.setName("ProjectE UUID Checker " + (isServer ? "Server" : "Client"));
	}
	
	@Override
	public void run()
	{
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(uuidURL).openStream())))
		{
			String line = reader.readLine();
			
			if (line == null)
			{
				PECore.LOGGER.fatal("UUID check failed!");
				throw new IOException("No data from github UUID list!");
			}

			List<String> uuids = new ArrayList<>();
					
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
		catch(IOException e)
		{
			PECore.LOGGER.fatal("Caught exception in UUID Checker thread!");
			e.printStackTrace();
		}
		finally
		{
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
