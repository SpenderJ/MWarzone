package fr.reizam.mwarzone;

import java.lang.reflect.Modifier;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Lists;

import fr.reizam.mwarzone.commands.MWarzoneCmd;
import fr.reizam.mwarzone.listeners.MWarzoneListeners;
import fr.reizam.mwarzone.manager.MWarzoneManager;
import fr.reizam.mwarzone.utils.cmd.CommandFramework;
import fr.reizam.mwarzone.utils.json.EnumTypeAdapter;
import fr.reizam.mwarzone.utils.json.ItemStackAdapter;
import fr.reizam.mwarzone.utils.json.JsonPersist;
import fr.reizam.mwarzone.utils.json.LocationAdapter;
import fr.reizam.mwarzone.utils.json.PotionEffectAdapter;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;

public class MWarzone extends JavaPlugin{
	
	private Gson gson;
	private List<JsonPersist> persistances = Lists.newArrayList();
	private CommandFramework framework;
	
	private MWarzoneManager manager;
	
	static MWarzone instance;
	
	public void onEnable() {
		instance = this;
		
		this.manager = new MWarzoneManager(this);
		this.manager.init();
		
		getDataFolder().mkdir();
				
		this.persistances.add(manager);
		
		this.gson = this.getGsonBuilder().create();
		this.persistances.forEach(p -> p.loadData());
		
		this.framework = new CommandFramework(this);
		
		this.framework.registerCommands(new MWarzoneCmd());
		
		Bukkit.getPluginManager().registerEvents(new MWarzoneListeners(), this);
	}
	
	public void onDisable(){
		this.persistances.forEach(p -> p.saveData(true));
		
		if(manager.getS() != null) {
			this.manager.getS().remove();
		}
		
	}
	
	public CommandFramework getFramework() {
		return framework;
	}
	
	public Gson getGson() {
		return gson;
	}

	public List<JsonPersist> getPersistances() {
		return persistances;
	}
	
	private GsonBuilder getGsonBuilder() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE).registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY).registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter()).registerTypeAdapter(PotionEffect.class, new PotionEffectAdapter()).registerTypeAdapter(Location.class, new LocationAdapter());
    }

	public MWarzoneManager getManager() {
		return manager;
	}
	
	public static MWarzone getInstance() {
		return instance;
	}

}
