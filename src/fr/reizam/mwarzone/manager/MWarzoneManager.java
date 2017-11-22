package fr.reizam.mwarzone.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import fr.reizam.mwarzone.MWarzone;
import fr.reizam.mwarzone.utils.ConfigCreator;
import fr.reizam.mwarzone.utils.LocationUtils;
import fr.reizam.mwarzone.utils.json.DiscUtil;
import fr.reizam.mwarzone.utils.json.JsonPersist;
import net.minecraft.util.com.google.common.reflect.TypeToken;

public class MWarzoneManager implements Runnable, JsonPersist {

	private Date d = new Date();

	private Villager s = null;

	private ConfigCreator config;
	private BukkitTask task;

	private HashMap<String, GuiEchange> echanges = new HashMap<>();
	private ArrayList<ItemStack> loots = new ArrayList<>();

	private MWarzone instance;

	public MWarzoneManager(MWarzone instance) {
		this.instance = instance;
	}

	public MWarzone getInstance() {
		return instance;
	}

	public HashMap<String, GuiEchange> getEchanges() {
		return echanges;
	}

	public void init() {
		this.config = new ConfigCreator("config", instance);
		if (this.config.getStringList("chestCoords") == null) {
			this.config.set("chestCoords", Arrays.asList(""));
		}
		if (this.config.getString("pngLocation") == null) {
			this.config.set("pngLocation", "null");
		} else {
			this.spawnPNG();
		}
		this.config.save();

		this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, this, 20L, 20L);
	}

	@SuppressWarnings("deprecation")
	public void spawnPNG() {
		if (s != null) {
			s.remove();
			s = null;
		}
		Location loc = stringToloc(this.config.getString("pngLocation"));
		s = (Villager) loc.getWorld().spawnCreature(loc, EntityType.VILLAGER);
		s.setCustomName("§e• Un échange ?");
		s.setCustomNameVisible(true);
		s.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999999, 999999999));
	}

	public String locToString(Location loc) {
		return loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ":" + loc.getWorld().getName() + ":"
				+ loc.getPitch() + ":" + loc.getYaw();
	}

	public Location stringToloc(String loc) {
		String[] args = loc.split(":");
		return new Location(Bukkit.getWorld(args[3]), Integer.parseInt(args[0]), Integer.parseInt(args[1]),
				Integer.parseInt(args[2]), Float.parseFloat(args[5]), Float.parseFloat(args[4]));
	}

	public Villager getS() {
		return s;
	}

	public ConfigCreator getConfig() {
		return config;
	}

	public BukkitTask getTask() {
		return task;
	}

	public ArrayList<ItemStack> getLoots() {
		return loots;
	}

	@SuppressWarnings("deprecation")
	public void spawnChest() {
		if (config.getStringList("chestCoords").isEmpty())
			return;
		int blockPoint = (Bukkit.getOnlinePlayers().length / 5 < 10 ? 10
				: ((int) (Bukkit.getOnlinePlayers().length / 5) > 80 ? 80
						: ((int) (Bukkit.getOnlinePlayers().length / 5))));
		Random r = new Random();
		List<String> rLoc = config.getStringList("chestCoords");
		
		for (String a : rLoc) {
			Location loc = LocationUtils.stringtoLoc(a);
			loc.getBlock().setType(Material.CHEST);
			Chest c = (Chest) loc.getBlock().getState();

			c.getBlockInventory().setItem(r.nextInt(c.getBlockInventory().getSize()),
					loots.get(r.nextInt(loots.size())));
			c.getBlockInventory().setItem(r.nextInt(c.getBlockInventory().getSize()),
					loots.get(r.nextInt(loots.size())));
			c.getBlockInventory().setItem(r.nextInt(c.getBlockInventory().getSize()),
					loots.get(r.nextInt(loots.size())));
			
		}
		for(int i = 0; i < blockPoint;i++) {
			Location loc = LocationUtils.stringtoLoc(rLoc.get(new Random().nextInt(rLoc.size())));
			loc.getBlock().setType(Material.CHEST);
			Chest c = (Chest) loc.getBlock().getState();
			c.getBlockInventory().addItem(new ItemStack(Material.BEDROCK,1));
		}
		Bukkit.broadcastMessage("§c§m-------------------------------------------");
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage("§6► §6Les coffres de la mine se sont regénérés ! ◄");
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage("§c§m-------------------------------------------");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if (d.getMinutes() == 15 && d.getSeconds() == 0) {
			if (config.getStringList("chestCoords").isEmpty())
				return;
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mwarzone spawn");
		}
		if (this.config.getString("pngLocation") != null && s != null) {
			Location loc = stringToloc(this.config.getString("pngLocation"));
			if (getDistance(loc, s.getLocation()) < 3) {
				s.teleport(loc);
			}
		}
	}

	public int getDistance(Location shooter, Location target) {
		double deltaX = shooter.getX() - target.getX();
		double deltaY = shooter.getY() - target.getY();
		double deltaZ = shooter.getZ() - target.getZ();
		return (int) Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
	}

	@Override
	public File getFile() {
		return new File(MWarzone.getInstance().getDataFolder(), "loots.json");
	}

	@Override
	public void loadData() {
		String content = DiscUtil.readCatch(getFile());
		if (content == null)
			return;

		@SuppressWarnings("serial")
		ArrayList<ItemStack> map = JsonPersist.gson.fromJson(content, new TypeToken<ArrayList<ItemStack>>() {
		}.getType());

		loots.clear();
		loots.addAll(map);
	}

	@Override
	public void saveData(boolean sync) {
		DiscUtil.writeCatch(getFile(), JsonPersist.gson.toJson(loots), sync);
	}

}
