package me.capit.Townshend.town;

import java.io.File;
import java.io.IOException;
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
	
	// New Town
	public Town(TownshendPlugin plugin, UUID owner, String name) throws TownCreationException, IOException{
		ID = TownshendPlugin.getNextID();
		File file = new File(plugin.getDataFolder()+File.separator+"towns"+File.separator+ID+".yml");
		file.createNewFile();
		this.owner = owner;
		this.name = name;
		FILE = file;
		
		save();
	}
	
	// From Config
	public Town(TownshendPlugin plugin, String name) throws TownCreationException{
		File file = new File(plugin.getDataFolder()+File.separator+"towns"+File.separator+name);
		if (file.exists()){
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			this.ID = config.getInt("ID");
			this.name = config.getString("name");
			try {
				this.owner = UUID.fromString(config.getString("owner"));
			} catch (IllegalArgumentException e){
				this.owner = null;
			}
			
			FILE = file;
			
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
	
	public void save(){
		YamlConfiguration config = YamlConfiguration.loadConfiguration(FILE);
		config.set("ID", ID);
		config.set("name", name);
		if (owner!=null){
			config.set("owner", owner.toString());
		} else {
			config.set("owner", "");
		}
		try {
			config.save(FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
