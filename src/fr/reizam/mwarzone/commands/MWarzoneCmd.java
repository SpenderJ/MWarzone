package fr.reizam.mwarzone.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import fr.reizam.mwarzone.MWarzone;
import fr.reizam.mwarzone.utils.LocationUtils;
import fr.reizam.mwarzone.utils.cmd.Args;
import fr.reizam.mwarzone.utils.cmd.Command;

public class MWarzoneCmd {

	@Command(name = { "mwarzone" })
	public void onCommand(Args info) {
			String[] args = info.getArgs();
			Player p = info.getPlayer();
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("setpnj") && p.isOp()) {
					String loc = MWarzone.getInstance().getManager().locToString(p.getLocation());
					MWarzone.getInstance().getManager().getConfig().set("pngLocation", loc);
					MWarzone.getInstance().getManager().getConfig().save();
					MWarzone.getInstance().getManager().spawnPNG();
					p.sendMessage("§e• Vous avez définit la position de spawn du §cPNJ §e!");
				} else if (args[0].equalsIgnoreCase("loot")) {
					Inventory inv = Bukkit.createInventory(null, 45, "§e§lChoisissez les loots !");
					for(int i = 0; i < MWarzone.getInstance().getManager().getLoots().size();i++) {
						inv.setItem(i, MWarzone.getInstance().getManager().getLoots().get(i));
					}
					p.openInventory(inv);
				} else if(args[0].equalsIgnoreCase("spawn") && p.isOp()) {
					MWarzone.getInstance().getManager().spawnChest();
				} else if (args[0].equalsIgnoreCase("setchest") && p.isOp()) {
					WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager()
							.getPlugin("WorldEdit");
					Selection selection = worldEdit.getSelection(info.getPlayer());
					if (selection == null) {
						p.sendMessage("§cVeuillez faire une selection WorldEdit !");
						return;
					}
					String loc = LocationUtils.loctoString(selection.getMinimumPoint());
					List<String> lists = MWarzone.getInstance().getManager().getConfig().getStringList("chestCoords");
					lists.add(loc);
					MWarzone.getInstance().getManager().getConfig().set("chestCoords",lists);
					MWarzone.getInstance().getManager().getConfig().save();
					p.sendMessage("§e• Vous venez d'ajouter un coffre spawnable !");
				} else if (args[0].equalsIgnoreCase("removechest") && p.isOp()) {
					WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager()
							.getPlugin("WorldEdit");
					Selection selection = worldEdit.getSelection(info.getPlayer());
					if (selection == null) {
						p.sendMessage("§cVeuillez faire une selection WorldEdit !");
						return;
					}
					String loc = LocationUtils.loctoString(selection.getMinimumPoint());
					List<String> lists = MWarzone.getInstance().getManager().getConfig().getStringList("chestCoords");
					if(lists.contains(loc)) {
						lists.remove(loc);
						p.sendMessage("§e• Suppression du chest effectué !");
						MWarzone.getInstance().getManager().getConfig().set("chestCoords", lists);
					} else {
						p.sendMessage("§cIl n'y pas de chest définit ici !");
					}
				} else if (args[0].equalsIgnoreCase("listchest")) {
					p.sendMessage("§6§m-------------------------");
					p.sendMessage(" ");
					for (String str : MWarzone.getInstance().getManager().getConfig().getStringList("chestCoords")) {
						p.sendMessage("§6• Coffre en §cx:" + str.split(":")[0]+" y:"+str.split(":")[1]+" z:"+str.split(":")[2]);
					}
					p.sendMessage(" ");
					p.sendMessage("§6§m-------------------------");
				} else {
					this.sendHelp(info.getSender());
				}
			} else {
				this.sendHelp(info.getSender());
			}
	}

	private void sendHelp(CommandSender sender) {
		sender.sendMessage("§e§m-------------------------");
		sender.sendMessage(" ");
		sender.sendMessage("§e• /mwarzone loot");
		sender.sendMessage("§e• /mwarzone setchest");
		sender.sendMessage("§e• /mwarzone setpng");
		sender.sendMessage("§e• /mwarzone removechest");
		sender.sendMessage("§e• /mwarzone listchest");
		sender.sendMessage("§e• /mwarzone spawn");
		sender.sendMessage(" ");
		sender.sendMessage("§e§m-------------------------");
	}

}
