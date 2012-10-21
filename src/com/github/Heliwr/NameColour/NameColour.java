package com.github.Heliwr.NameColour;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
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
import org.bukkit.event.player.PlayerLoginEvent;
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
	public void onPlayerJoin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		logger.info("[NameColour] " + player.getName() + " logged in to " + event.getHostname() + " from " + event.getAddress().getHostName());
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
		        	this.reloadConfig();
		        	assignAll();
	        	}
        	} else {
        		logger.info("[NameColour] Command access denied for " + player.getName());
        	}
    		return true;
        } else if (cmdname.equals("fakeop") && args.length > 0) {
        	if (player == null || player.isOp() || player.hasPermission("namecolour.admin")) {
        		List<Player> fakeopslist = this.getServer().matchPlayer(args[0]);
				if (fakeopslist.size() > 1) {
					player.sendMessage(ChatColor.RED + "Too Many Players Found");
					return false;
				} else if(fakeopslist.size() == 0) {
					player.sendMessage(ChatColor.RED + "Player" + args[0] + "Not Found");
					return false;
				}
    			Player r = fakeopslist.get(0);
        		if (player != null) {
        			player.sendMessage("[NameColour] Assigning fakeop to " + r.getName() + ".");
            		logger.info("[NameColour] Fakeop assigned to " + r.getName() + " by " + player.getName() + ".");
        		} else {
            		logger.info("[NameColour] Fakeop assigned to " + r.getName() + " by console.");
        		}
        		r.setDisplayName(ChatColor.RED + "[Op] " + r.getName() + ChatColor.WHITE);
        	} else {
        		logger.info("[NameColour] Command access denied for " + player.getName());
        	}
    		return true;
        } else if (cmdname.equals("nick") && args.length > 0) {
        	if (player == null || player.isOp() || player.hasPermission("namecolour.admin")) {
        		List<Player> nicklist = this.getServer().matchPlayer(args[0]);
				if (nicklist.size() > 1) {
					player.sendMessage(ChatColor.RED + "Too Many Players Found");
					return false;
				} else if(nicklist.size() == 0) {
					player.sendMessage(ChatColor.RED + "Player" + args[0] + "Not Found");
					return false;
				}
    			Player r = nicklist.get(0);
    			if(args.length > 1) {
        			if (player != null) {
            			player.sendMessage("[NameColour] Assigning nick " + args[1] + " to " + r.getName() + ".");
                		logger.info("[NameColour] Nick " + args[1] + " assigned to " + r.getName() + " by " + player.getName() + ".");
            		} else {
                		logger.info("[NameColour] Nick " + args[1] + " assigned to " + r.getName() + " by console.");
            		}
            		r.setDisplayName(args[1]);
            		setColour(r);    				
    			} else {
        			if (player != null) {
            			player.sendMessage("[NameColour] Removing nick for " + r.getName() + ".");
                		logger.info("[NameColour] " + player.getName() + " removing nick for " + r.getName() + ".");
            		} else {
                		logger.info("[NameColour] Console removing nick for " + r.getName() + ".");
            		}
            		r.setDisplayName(r.getName());
            		setColour(r);
    			}
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
    		r.setDisplayName(r.getName());
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
			player.setDisplayName(ChatColor.BLACK + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("DARK_BLUE")) {
			player.setDisplayName(ChatColor.DARK_BLUE + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("DARK_GREEN")) {
			player.setDisplayName(ChatColor.DARK_GREEN + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("DARK_AQUA")) {
			player.setDisplayName(ChatColor.DARK_AQUA + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("DARK_RED")) {
			player.setDisplayName(ChatColor.DARK_RED + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("DARK_PURPLE")) {
			player.setDisplayName(ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("GOLD")) {
			player.setDisplayName(ChatColor.GOLD + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("GRAY")) {
			player.setDisplayName(ChatColor.GRAY + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("DARK_GRAY")) {
			player.setDisplayName(ChatColor.DARK_GRAY + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("BLUE")) {
			player.setDisplayName(ChatColor.BLUE + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("GREEN")) {
			player.setDisplayName(ChatColor.GREEN + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("AQUA")) {
			player.setDisplayName(ChatColor.AQUA + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("RED")) {
			player.setDisplayName(ChatColor.RED + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("LIGHT_PURPLE")) {
			player.setDisplayName(ChatColor.LIGHT_PURPLE + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("YELLOW")) {
			player.setDisplayName(ChatColor.YELLOW + player.getDisplayName() + ChatColor.WHITE);
		} else if(colour.equals("WHITE")) {
			player.setDisplayName(ChatColor.WHITE + player.getDisplayName() + ChatColor.WHITE);
		}
    }
}
   