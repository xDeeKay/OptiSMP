package Nick3306.github.io.OptiSMP.Listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import Nick3306.github.io.OptiSMP.Main;
import Nick3306.github.io.OptiSMP.Components.OptiProtect.ProtectUtilities;
import Nick3306.github.io.OptiSMP.Utilities.GeneralUtilities;
import Nick3306.github.io.OptiSMP.Utilities.SMPplayer;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PlayerJoinListener implements Listener
{
	private Main plugin;
	private ProtectUtilities proUtil;
	private GeneralUtilities util;
	public PlayerJoinListener(Main plugin)
	{
	   this.plugin = plugin;
	   this.proUtil = this.plugin.protectUtil;
	   this.util = plugin.util;
	   
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) 
	{
		Player player = event.getPlayer();
		plugin.sql.getPlayer(player);					
	}
}
