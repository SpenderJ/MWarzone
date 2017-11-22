package fr.reizam.mwarzone.listeners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import fr.reizam.mwarzone.MWarzone;
import fr.reizam.mwarzone.manager.GuiEchange;

public class MWarzoneListeners implements Listener {

	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		if (e.getInventory().getTitle().equalsIgnoreCase("§e§lChoisissez les loots !")) {
			ArrayList<ItemStack> loots = new ArrayList<>();
			for (ItemStack is : e.getInventory().getContents()) {
				if (is != null) {
					loots.add(is);
				}
			}
			MWarzone.getInstance().getManager().getLoots().clear();
			MWarzone.getInstance().getManager().getLoots().addAll(loots);
			((Player) e.getPlayer()).sendMessage("§e• Vous avez bien définit les loots des coffres !");
		} else if (e.getInventory().getTitle().equalsIgnoreCase("§cMWarzone §6"+e.getPlayer().getName())) {
			if(MWarzone.getInstance().getManager().getEchanges().containsKey(e.getPlayer().getName())) {
				MWarzone.getInstance().getManager().getEchanges().remove(e.getPlayer().getName());
				((Player)e.getPlayer()).sendMessage("§e• L'échange à été §cannulé §e!");
			} 
		}
	}

	
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getClickedInventory() == null)
			return;
		Player p = (Player) e.getWhoClicked();
		if(p.getOpenInventory().getTopInventory().getTitle().startsWith("§cMWarzone")) {
			e.setCancelled(true);
		}
		if(e.getClickedInventory().getTitle().startsWith("§cMWarzone")) {
			GuiEchange gui = MWarzone.getInstance().getManager().getEchanges().get(ChatColor.stripColor(e.getClickedInventory().getTitle().split(" ")[1]));
			if(e.getCurrentItem().getType().equals(Material.MAGMA_CREAM)) {
				gui.finalize();
				p.closeInventory();
				p.sendMessage("§e• L'échange s'est déroulée avec §6succés §e!");
				return;
			} else if(e.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE) && (e.getCurrentItem().getData().getData() == ((byte)5) || e.getCurrentItem().getData().getData() == ((byte)14))) {
				if(gui.isAll()) {
					gui.setAll(false);
				} else {
					gui.setAll(true);
				}
				return;
			}
		} else return;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntityType().equals(EntityType.VILLAGER)) {
			Villager s = (Villager) e.getEntity();
			if (s.getCustomName().equalsIgnoreCase("§e• Un échange ?")) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		Entity ent = e.getRightClicked();
		if (ent.getType().equals(EntityType.VILLAGER)) {
			Villager v = (Villager) ent;
			if (v.getCustomName().equalsIgnoreCase("§e• Un échange ?")) {
				e.setCancelled(true);
				if(!p.getInventory().contains(Material.BEDROCK) || (getHowMany(Material.BEDROCK, p) < 2)) {
					p.sendMessage("§e• Erreur, vous n'avez pas au moins §c2 blocs de points §e!");
					return;
				}
				new GuiEchange(p);
				return;
			}
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

}
