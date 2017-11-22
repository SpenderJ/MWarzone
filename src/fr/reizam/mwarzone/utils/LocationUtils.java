package fr.reizam.mwarzone.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {

	public static String loctoString(Location loc) {
		return loc.getBlockX()+":"+loc.getBlockY()+":"+loc.getBlockZ()+":"+loc.getWorld().getName();
	}
	
	public static Location stringtoLoc(String loc) {
		String[] args = loc.split(":");
		String x = args[0];
		String y = args[1];
		String z = args[2];
		String world = args[3];
		return new Location(Bukkit.getWorld(world), Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z));
	}
}
