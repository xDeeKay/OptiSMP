package Nick3306.github.io.OptiSMP.Commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import Nick3306.github.io.OptiSMP.Main;
import Nick3306.github.io.OptiSMP.Components.OptiProtect.ProtectUtilities;
import Nick3306.github.io.OptiSMP.Components.OptiProtect.ProtectionField;
import Nick3306.github.io.OptiSMP.Utilities.GeneralUtilities;
import Nick3306.github.io.OptiSMP.Utilities.SMPplayer;

public class PField implements CommandExecutor
{
	private Main plugin;
	private ProtectUtilities proUtil;
	private GeneralUtilities util;
	public HashMap<String, ProtectionField> waitingResponse = new HashMap<String, ProtectionField>();
	public PField(Main plugin)
	{
	   this.plugin = plugin;
	   this.util =  this.plugin.util;		   
	   this.proUtil = this.plugin.protectUtil;
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2lable, String[] args) 
	{
		Player player = null;
		if(sender instanceof Player)
		{
			player = (Player) sender;
		}
		if(cmd.getName().equalsIgnoreCase("pfield"))
		{
			if(args.length == 0)
			{
				player.sendMessage(ChatColor.RED + "                      OptiProtect");
				player.sendMessage(ChatColor.RED + "_____________________________________________________");
				player.sendMessage(ChatColor.GREEN + "/pfield create <fieldname>: Start creation of a pfield");
				player.sendMessage(ChatColor.GREEN + "/pfield create <fieldname> radius <radius>: Start creation of a pfield that uses a radius from one block");
				player.sendMessage(ChatColor.GREEN + "/pfield remove: Remove the pfield you are standing in");
				player.sendMessage(ChatColor.GREEN + "/pfield info: Get info about the pfield you are standing in");
				player.sendMessage(ChatColor.GREEN + "/pfield info <fieldname>: Get info about a specific pfield you own");
				player.sendMessage(ChatColor.GREEN + "/pfield addmember <playername> <fieldname>: Add specified player to the pfield you are standing in");
				player.sendMessage(ChatColor.GREEN + "/pfield removemember <playername> <fieldname>: Remove specified player from the pfield you are standing in");
				player.sendMessage(ChatColor.GREEN + "/pfield list: Get a list of your current protection fields");
				player.sendMessage(ChatColor.GREEN + "/pfield togglemessages: Toggle on and off pfield entrance messages");
				
				return true;
			}
			if(args[0].equalsIgnoreCase("Create"))
			{
				if(args.length != 2 && args.length != 4)
				{
					player.sendMessage(ChatColor.RED + "Incorrect usage: /pfield create <fieldname>");
					return false;
				}
				if(args[1].length() > 25)
				{
					player.sendMessage(ChatColor.RED + "Field name must be less than 25 characters");
					return false;
				}
				if(args.length == 2)
				{
					
					ProtectionField newField = new ProtectionField(player.getWorld(),null, null, player.getUniqueId(), args[1], "cuboid");
					if(!proUtil.duplicateName(newField))
					{
						plugin.newFields.add(newField);					
						player.sendMessage(ChatColor.GREEN + "Place the first block to define the field");
						return true;
					}
					else
					{
						player.sendMessage(ChatColor.RED + "You already have a field with this name!");
						return false;
					}

				}
				else if(args.length == 4)
				{
					if(args[2].equalsIgnoreCase("radius"))
					{
						if(StringUtils.isNumeric(args[3])) 
						{
							ProtectionField newField = new ProtectionField(player.getWorld(),null, null, player.getUniqueId(), args[1], "radius");
							if(!proUtil.duplicateName(newField))
							{
								newField.setRadius(Integer.parseInt(args[3]));
								plugin.newFields.add(newField);					
								player.sendMessage(ChatColor.GREEN + "Place a block to define the field with radius " + args[3]);
								return true;
							}
							else
							{
								player.sendMessage(ChatColor.RED + "You already have a field with this name!");
								return false;
							}
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You entered a radius that was not a number");
						}
					}
						
				}
			}
			if(args[0].equalsIgnoreCase("info"))
			{
				if(args.length == 1)
				{
					Location loc = player.getLocation();			
					ProtectionField field = proUtil.getPField(loc);
					if(field != null)
					{
						proUtil.sendPfieldInfo(player, field);
						proUtil.highlightField(field, player);
					}
					else
					{
						player.sendMessage(ChatColor.RED + "You are not in a field currently!");
						return false;
					}
				}
				if(args.length == 2)
				{
					ProtectionField field = proUtil.getPFieldByName(player.getUniqueId(), args[1]);
					if(field != null)
					{
						proUtil.sendPfieldInfo(player, field);
						proUtil.highlightField(field, player);
					}
					else
					{
						player.sendMessage(ChatColor.RED + "You do not own a field by that name!");
						return false;
					}
				}
				
			}
			if(args[0].equalsIgnoreCase("addmember"))
			{
				if(args.length != 3)
				{
					player.sendMessage(ChatColor.RED + "Incorrect usage: /pfield addmember <membername> <fieldname>");
					return false;
				}
				if(args.length == 3)
				{
					ProtectionField field = proUtil.getPFieldByName(player.getUniqueId(), args[2]);			
					if(field == null)
					{
						player.sendMessage(ChatColor.RED + "You do not have a field by that name!");
						return false;
					}
					else
					{
							if(plugin.getServer().getOfflinePlayer(args[1]) != null)
							{
								OfflinePlayer playerToAdd = plugin.getServer().getOfflinePlayer(args[1]);								
								
								if(!field.members.contains(playerToAdd.getUniqueId()))
								{
									plugin.sql.addMember(field, playerToAdd);
									field.members.add(playerToAdd.getUniqueId());
									player.sendMessage(ChatColor.GREEN + "Player added");
									return true;
								}
								else
								{
									player.sendMessage(ChatColor.RED + "This person is already added to your field");
									return false;
								}
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Player not found!");
								return false;
							}
						}
					
					}				
			}
			if(args[0].equalsIgnoreCase("removemember"))
			{
				if(args.length !=3)
				{
					player.sendMessage(ChatColor.RED + "Incorrect usage: /pfield removemember <membername> <fieldname>");
					return false;
				}
				if(args.length == 3)
				{
					ProtectionField field = proUtil.getPFieldByName(player.getUniqueId(), args[2]);
					if(field == null)
					{
						player.sendMessage(ChatColor.RED + "You do not have a field by this name!");
						return false;
					}
					else
					{						
							if(plugin.getServer().getOfflinePlayer(args[1]) != null)
							{
								OfflinePlayer playerToRemove = plugin.getServer().getOfflinePlayer(args[1]);
								if(field.members.contains(playerToRemove.getUniqueId()))
								{
									field.members.remove(playerToRemove.getUniqueId());
									plugin.sql.removeMember(field, playerToRemove); player.sendMessage(ChatColor.GREEN + "Player removed");
									return true;
								}
								else
								{
									player.sendMessage(ChatColor.RED + "This person is not a member of that field");
									return false;
								}								
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Player not found!");
								return false;
							}																
					}
				}
			}
			if(args[0].equalsIgnoreCase("remove"))
			{
				if(args.length > 2)
				{
					player.sendMessage(ChatColor.RED + "Incorrect usage: /pfield remove <fieldname Optional>");
					return false;
				}
				
				ProtectionField field = null;
				if(args.length == 1) {
					Location loc = player.getLocation();			
					field = proUtil.getPField(loc);
				}
				else if(args.length == 2)
				{
					field = proUtil.getPFieldByName(player.getUniqueId(), args[1]);
				}
					if(field != null)
					{
						if(field.getOwner().toString().equals(player.getUniqueId().toString()) || player.hasPermission("optiSMP.protect.staff"))
						{
							waitingResponse.put(player.getName(), field);
							player.sendMessage(ChatColor.RED + "WARNING: You are about to delete the pfield " + field.getName() + ". Type '/pfield yes' to confirm or '/pfield no' to cancel");
							return true;
						}
						else
						{
							player.sendMessage(ChatColor.RED + "You are not the owner of this field!");
							return false;
						}
					}
					else
					{
						if(args.length == 1)
						{
							player.sendMessage(ChatColor.RED + "You are not standing in a field!");							
							return false;
						}
						if(args.length == 2) 
						{
							player.sendMessage(ChatColor.RED + "You do not have a field by that name!");
							return false;
						}
					}				

			}			
			if(args[0].equalsIgnoreCase("yes"))
			{
				if(waitingResponse.get(player.getName()) != null)
				{
					ProtectionField fieldToRemove = waitingResponse.get(player.getName());
					
					SMPplayer smpPlayer = util.getSMPPlayer(player);
					//give the player back the number of protected blocks for the field removed
					smpPlayer.setProtectionBlocksLeft(smpPlayer.getProtectionBlocksLeft() + fieldToRemove.getArea());
					plugin.sql.savePlayer(smpPlayer);
					
					//remove field from database
					plugin.sql.removeField(fieldToRemove);
					//remove field from plugins list of total fields
					//log created field
					String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					proUtil.logFieldAction(date + ": Player " + player.getName() + " DELETED the field " + fieldToRemove.getName() + " getting back " + fieldToRemove.getArea() + " blocks. They have " + smpPlayer.getProtectionBlocksLeft() + " blocks left."  );
					proUtil.removeField(fieldToRemove);	
					waitingResponse.remove(player.getName());
					player.sendMessage(ChatColor.GREEN + "Field Removed");
					return true;
				}
				else
				{
					player.sendMessage(ChatColor.RED + "Pfield is not waiting for a response from you");
					return false;
				}
				
			}
			if(args[0].equalsIgnoreCase("no"))
			{
				if(waitingResponse.get(player.getName()) != null)
				{
					player.sendMessage("Remove canceled");
					waitingResponse.remove(player.getName());
					return true;
				}
				else
				{
					player.sendMessage(ChatColor.RED + "Pfield is not waiting for a response from you");
					return false;
				}
			}
			if(args[0].equalsIgnoreCase("visualize"))
			{
				ProtectionField field = proUtil.getPField(player.getLocation());
				if(field != null)
				{
					proUtil.highlightField(field, player);
					player.sendMessage(ChatColor.GREEN + "Field is now visualized");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "You are not in a protection field!");
					return false;
				}
			}
			if(args[0].equalsIgnoreCase("list"))
			{	
				player.sendMessage(ChatColor.GREEN + "Fields:");
				//SMPplayer smpPlayer = util.getSMPPlayer(player);
				//ArrayList<ProtectionField> playerFields = smpPlayer.getPFields();
				int count = 1;
				for(ProtectionField field : plugin.fields)
				{
					if(field.getOwner().equals(player.getUniqueId()))
					{
						player.sendMessage(ChatColor.GREEN + Integer.toString(count) + ". " + field.getName());
						count++;
					}
				}	
				return true;
			}
			if(args[0].equalsIgnoreCase("members"))
			{
				if(args.length == 2)
				{
					ProtectionField field = proUtil.getPFieldByName(player.getUniqueId(), args[1]);					
					if(field != null)
					{
						if(field.members.size() != 0)
						{
							for(UUID uuid : field.members)
							{
								player.sendMessage(ChatColor.GREEN + Bukkit.getOfflinePlayer(uuid).getName());
							}
							return true;
						}						
						else
						{
							player.sendMessage(ChatColor.RED + "That field has no members!");
							return false;
						}
					}
					else
					{
						player.sendMessage(ChatColor.RED + "You do not have a field by that name!");
						return false;
					}
				}
				else
				{
					player.sendMessage(ChatColor.RED + "Incorrect usage! /pfield members <fieldname>");
					return false;
				}
			}
		}
		if(args[0].equalsIgnoreCase("flag"))
		{
			
		}
		if(args[0].equalsIgnoreCase("togglemessages"))
		{
			if(args.length == 1)
			{
				SMPplayer smpPlayer = util.getSMPPlayer(player);
				smpPlayer.toggleRegionMessages();
				player.sendMessage("Field messages are now " + smpPlayer.getRegionMessages());
				return true;
			}
			else
			{
				player.sendMessage(ChatColor.RED + "Incorrect usage /pfield togglemessages");
				return false;
			}
		}
		if(args[0].equalsIgnoreCase("give"))
		{
			if (sender instanceof ConsoleCommandSender)
			{
				if(args.length == 3)
				{
					OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(args[1]);
					if(offlinePlayer.hasPlayedBefore()) // Player has played on this server before
					{
						plugin.sql.addPFieldBlocks((Player) offlinePlayer, Integer.parseInt(args[2]));
					}
				}
				else
				{
					Bukkit.getLogger().info("Incorrect usage of /pfield give");
				}
			}
			else
			{
				Bukkit.getLogger().info("Player tired to use /pfield give");
			}
			return false;
			
		}
		
		return false;
	}
	
}
