package me.capit.Townshend.town;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import me.capit.Townshend.TownshendPlugin;

public class Town {
	public class TownCreationException extends Exception {
		private static final long serialVersionUID = 7251697839256241835L;
		private final String msg;
		
		public TownCreationException(String msg){
			this.msg = msg;
		}
		
		@Override
		public String getLocalizedMessage(){
			return msg;
		}
	}
	
	public final int ID;
	public final File FILE;
	
	private UUID owner;
	private String name;
	private String desc;
	
	private List<UUID> players = new ArrayList<UUID>();
	private List<UUID> mods = new ArrayList<UUID>();
	
	private List<String> invites = new ArrayList<String>();
	
	// New Town
	public Town(TownshendPlugin plugin, UUID owner, String name) throws TownCreationException, IOException{
		ID = TownshendPlugin.getNextID();
		File file = new File(plugin.getDataFolder()+File.separator+"towns"+File.separator+ID+".yml");
		file.createNewFile();
		this.owner = owner;
		this.name = name;
		this.desc = plugin.getConfig().getString("TOWNS.DEFAULT_DESCRIPTION");
		FILE = file;
		
		players.add(owner);
		
		save();
	}
	
	// From Config
	public Town(TownshendPlugin plugin, String name) throws TownCreationException{
		File file = new File(plugin.getDataFolder()+File.separator+"towns"+File.separator+name);
		if (file.exists()){
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			this.ID = config.getInt("ID");
			this.name = config.getString("name");
			this.desc = config.getString("desc")!=null ? config.getString("desc") : 
				plugin.getConfig().getString("TOWNS.DEFAULT_DESCRIPTION");
			this.invites = config.getStringList("invites");
			try {
				this.owner = UUID.fromString(config.getString("owner"));
			} catch (IllegalArgumentException e){
				this.owner = null;
			}
			
			FILE = file;
			
			for (String str : config.getStringList("players")){
				players.add(UUID.fromString(str));
			}
			for (String str : config.getStringList("mods")){
				mods.add(UUID.fromString(str));
			}
			
			save();
		} else {
			throw new TownCreationException("Could not load spcified town '"+name+"'");
		}
	}
	
	public UUID getOwner(){
		return owner;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDesc(){
		return desc;
	}
	
	public void setDesc(String desc){
		this.desc = desc;
	}
	
	public List<String> getInvites(){
		return invites;
	}
	
	public boolean isInvited(String pname){
		return invites.contains(pname);
	}
	
	public void invite(String pname){
		if (!isInvited(pname)) invites.add(pname);
	}
	
	public void uninvite(String pname){
		invites.remove(pname);
	}
	
	public boolean playerInTown(UUID player){
		return players.contains(player);
	}
	
	public void addPlayer(UUID player){
		if (!playerInTown(player)) players.add(player);
	}
	
	public void removePlayer(UUID player){
		players.remove(player);
	}
	
	public void save(){
		YamlConfiguration config = YamlConfiguration.loadConfiguration(FILE);
		config.set("ID", ID);
		config.set("name", name);
		config.set("desc", desc);
		config.set("invites", invites);
		if (owner!=null){
			config.set("owner", owner.toString());
		} else {
			config.set("owner", "");
		}
		
		List<String> pls = new ArrayList<String>();
		for (UUID id : players){
			pls.add(id.toString());
		}
		config.set("players", pls);
		
		List<String> mds = new ArrayList<String>();
		for (UUID id : mods){
			mds.add(id.toString());
		}
		config.set("mods", mds);
		
		try {
			config.save(FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasPlayer(UUID player){
		for (UUID p : players){
			if (p.equals(player)) return true;
		}
		return false;
	}
	
	public boolean isMod(UUID player){
		if (isOwner(player)) return true;
		for (UUID m : mods){
			if (m.equals(player)) return true;
		}
		return false;
	}
	
	public List<UUID> getPlayers(){
		return players;
	}
	
	public List<UUID> getMods(){
		return mods;
	}
	
	public boolean isOwner(UUID player){
		return owner.equals(player);
	}
	
}
