package fr.reizam.mwarzone.manager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.reizam.mwarzone.MWarzone;
import fr.reizam.mwarzone.utils.ItemBuilder;

public class GuiEchange {

	private Inventory inv;

	private boolean all = true;
	private boolean two = false;
	private Player p;
	private int m;
	
	public GuiEchange(Player p) {
		
		this.p = p;
		if(MWarzone.getInstance().getManager().getEchanges().containsKey(p.getName())) {
			MWarzone.getInstance().getManager().getEchanges().remove(p.getName());
		} 
		MWarzone.getInstance().getManager().getEchanges().put(p.getName(), this);
		
		inv = Bukkit.createInventory(null, 27, "§cMWarzone §6"+p.getName());
		
		int m = getHowMany(Material.BEDROCK, p);
		if(m % 2 > 0L) {
			m -= 1;
		}
		this.m = m;
		
		inv.setItem(10, new ItemBuilder(Material.MELON_BLOCK,m).setName("§eVous avez §c"+m+" §ebloc(s) de point(s) !").toItemStack());
		
		inv.setItem(12, new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)5)).setName("§eVendre §cdeux blocs").setLore("§eVendre seulement 2 blocs de points !"," ", "§e• §cOFF").toItemStack());
		inv.setItem(13, new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14)).setName("§eVendre §cinventaire complet").setLore("§eVendre tous tes blocs de points !"," ","§e• §6ON").toItemStack());
		
		inv.setItem(16, new ItemBuilder(Material.MAGMA_CREAM).setName("§e§lFinaliser l'échange !").toItemStack());
		
		for(int i = 0;i < 27;i++) {
			if(inv.getItem(i) == null) {
				inv.setItem(i, new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)7)).setName(" ").toItemStack());
			}
		}
		
		p.openInventory(inv);
	}

	public boolean isAll() {
		return all;
	}
	
	public void setAll(boolean all) {
		this.all = all;
		this.two = all == true ? false : true;
		if(all == false) {
			inv.setItem(13, new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)5)).setName("§eVendre §cinventaire complet").setLore("§eVendre tous tes blocs de points !"," ","§e• §cOFF").toItemStack());
		} else {
			inv.setItem(13, new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14)).setName("§eVendre §cinventaire complet").setLore("§eVendre tous tes blocs de points !"," ","§e• §6ON").toItemStack());
		}
		if(two == false) {
			inv.setItem(12, new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)5)).setName("§eVendre §cdeux blocs").setLore("§eVendre seulement 2 blocs de points !"," ", "§e• §cOFF").toItemStack());
		} else {
			inv.setItem(12, new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)14)).setName("§eVendre §cdeux blocs").setLore("§eVendre seulement 2 blocs de points !"," ", "§e• §6ON").toItemStack());
		}
	}
	
	public int getHowMany(Material m, Player p) {
		int i = 0;
		for(ItemStack is : p.getInventory().getContents()) {
			if(is != null && is.getType().equals(m)) {
				i += is.getAmount();
			}
		}
		return i;
	}
	
	public void finalize() {
		MWarzone.getInstance().getManager().getEchanges().remove(p.getName());
		if(all == true) {
			renome(p,Material.BEDROCK);
			p.getInventory().removeItem(new ItemStack(Material.BEDROCK,m));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "classement addbonus jeSaisaps "+(m/2)); // Nom faction
		} else if(two == true) {
			renome(p,Material.BEDROCK);
			p.getInventory().removeItem(new ItemStack(Material.BEDROCK,2));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "classement addbonus jeSaisaps 2"); // Nom faction
		}
	}
	
	public void renome(Player p, Material m) {
		for (ItemStack is : p.getInventory().getContents()) {
			if (is != null && is.getType().equals(m)) {
				is.setItemMeta(null);
			}
		}
	}
	
}
