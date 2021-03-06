package Nick3306.github.io.OptiSMP.Components.PVP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import Nick3306.github.io.OptiSMP.Main;

public class WorldController
{
	ArrayList<World> worlds = new ArrayList<World>();

	public WorldController(Main plugin)
	{
		
	}
	
	public void unloadWorld(World world) 
	{
	    if(world != null) {
	        Bukkit.getServer().unloadWorld(world, true);
	    }
	}
	public void deleteWorld(World world)
	{
		this.worlds.remove(world);
		
		File path = world.getWorldFolder();
		deleteWorldFiles(path);
	}
	
	public boolean deleteWorldFiles(File path) 
	{
		if(path.exists()) 
		{
			File files[] = path.listFiles();
	        for(int i=0; i<files.length; i++) 
	        {
	        	if(files[i].isDirectory()) 
	        	{
	                  deleteWorldFiles(files[i]);
	            } 
	        	else 
	        	{
	                  files[i].delete();
	            }
	        }
	    }
		
	    return(path.delete());
	}
	
	public void copyWorldFiles(File source, File target)
	{
	    try 
	    {
	        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
	        if(!ignore.contains(source.getName())) 
	        {
	            if(source.isDirectory()) 
	            {
	                if(!target.exists())
	                target.mkdirs();
	                String files[] = source.list();
	                for (String file : files) 
	                {
	                	File srcFile = new File(source, file);
	                    File destFile = new File(target, file);
	                    copyWorldFiles(srcFile, destFile);
	                }
	            } 
	            else 
	            {
	                InputStream in = new FileInputStream(source);
	                OutputStream out = new FileOutputStream(target);
	                byte[] buffer = new byte[1024];
	                int length;
	                while ((length = in.read(buffer)) > 0)
	                    out.write(buffer, 0, length);
	                in.close();
	                out.close();
	            }
	        }
	    } 
	    catch (IOException e) 
	    {
	 
	    }
	}
	
	public void createWorld(String name, String templateName)
	{
		WorldCreator creator = new WorldCreator(name);
		World world = creator.createWorld();
		this.worlds.add(world);
		
		//get file paths for both new world and template world to copy files
		File newWorld = world.getWorldFolder();
		File template = Bukkit.getServer().getWorld(templateName).getWorldFolder();
		//Copy files from template to new world
		copyWorldFiles(template, newWorld);
		
	}
	
	
	
}
