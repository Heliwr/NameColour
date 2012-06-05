package com.github.Heliwr.NameColour;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NameColour extends JavaPlugin implements Listener {
	
	public static final Logger logger = Logger.getLogger("Minecraft.NameColour");
	protected static YamlConfiguration conf;
	protected File f;
	
    public void onDisable() {
    	PluginDescriptionFile pdfFile = this.getDescription();
		logger.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!" );
    }

    public void onEnable() {
        try {
            checkConfig();
            loadConfig();
        }
        catch(FileNotFoundException ex) {
            logger.log(Level.SEVERE, "NameColour: No config file found!", ex);
            return;
        }
        catch(IOException ex) {
            logger.log(Level.SEVERE, "NameColour: Error while reading config!", ex);
            return;
        }
        catch(InvalidConfigurationException ex) {
            logger.log(Level.SEVERE, "NameColour: Error while parsing config!", ex);
            return;
        }
    	
    	PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        
        PluginDescriptionFile pdfFile = this.getDescription();
		logger.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		setColour(player);
	}

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args ) {
    	String cmdname = cmd.getName().toLowerCase();
        Player player = null;
        if (sender instanceof Player) {
        	player = (Player)sender;
        }
        
        if (cmdname.equals("namecolour") && args.length > 0) {
        	if (player == null || player.isOp() || player.hasPermission("namecolour.admin")) {
	        	if (args[0].equalsIgnoreCase("reload")) {
	        		if (player != null) {
	        			player.sendMessage("[NameColour] Assigning displayname colours according to group.");
		        		logger.info("[NameColour] Displayname colours reassigned by " + player.getName());
	        		} else {
		        		logger.info("[NameColour] Displayname colours reassigned from server console");
	        		}
	        	}
	        	this.reloadConfig();
	        	assignAll();
        	} else {
        		logger.info("[NameColour] Command access denied for " + player.getName());
        	}
    		return true;
        }
        
        return false;
    }

    public void checkConfig() throws FileNotFoundException, IOException, InvalidConfigurationException {
    	f = new File(this.getDataFolder(), "config.yml");
    	conf = new YamlConfiguration();

    	if(!f.exists()) {
    		conf.set("group.owner", "DARK_BLUE");
    		conf.set("group.admin", "DARK_BLUE");
    		conf.set("group.mod", "RED");
    		conf.set("group.vip", "GREEN");
    		conf.set("group.sponsor", "GREEN");
    		conf.set("group.su", "AQUA");
    		conf.set("group.donor", "AQUA");
    		conf.set("group.user", "YELLOW");
    		conf.set("group.default", "LIGHT_PURPLE");
    		conf.save(f);
    	}
    }
    
    private void loadConfig() throws FileNotFoundException, IOException, InvalidConfigurationException {
    	conf.load(f);
    }

    private void assignAll() {
    	for (Player r : this.getServer().getOnlinePlayers()) {
    		setColour(r);
		}
    }
    
    private void setColour(Player player) {
    	String colour = "LIGHT_PURPLE";
		for(String key : conf.getConfigurationSection("group").getKeys(true)){
			if(player.hasPermission("group." + key)) {
				colour = this.getConfig().getString("group." + key);
			}
		}
    	if(colour.equals("BLACK")) {
			player.setDisplayName(ChatColor.BLACK + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("DARK_BLUE")) {
			player.setDisplayName(ChatColor.DARK_BLUE + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("DARK_GREEN")) {
			player.setDisplayName(ChatColor.DARK_GREEN + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("DARK_AQUA")) {
			player.setDisplayName(ChatColor.DARK_AQUA + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("DARK_RED")) {
			player.setDisplayName(ChatColor.DARK_RED + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("DARK_PURPLE")) {
			player.setDisplayName(ChatColor.DARK_PURPLE + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("GOLD")) {
			player.setDisplayName(ChatColor.GOLD + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("GRAY")) {
			player.setDisplayName(ChatColor.GRAY + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("DARK_GRAY")) {
			player.setDisplayName(ChatColor.DARK_GRAY + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("BLUE")) {
			player.setDisplayName(ChatColor.BLUE + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("GREEN")) {
			player.setDisplayName(ChatColor.GREEN + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("AQUA")) {
			player.setDisplayName(ChatColor.AQUA + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("RED")) {
			player.setDisplayName(ChatColor.RED + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("LIGHT_PURPLE")) {
			player.setDisplayName(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("YELLOW")) {
			player.setDisplayName(ChatColor.YELLOW + player.getName() + ChatColor.WHITE);
		} else if(colour.equals("WHITE")) {
			player.setDisplayName(ChatColor.WHITE + player.getName() + ChatColor.WHITE);
		}
    }
}
   