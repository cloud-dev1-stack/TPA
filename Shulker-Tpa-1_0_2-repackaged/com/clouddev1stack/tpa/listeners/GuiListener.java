package com.clouddev1stack.tpa.listeners;

import com.clouddev1stack.tpa.ShulkerTpaPlugin;
import com.clouddev1stack.tpa.commands.ShulkerTpaCommand;
import com.clouddev1stack.tpa.managers.GuiManager;
import com.clouddev1stack.tpa.models.RequestType;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class GuiListener implements Listener {
   private final ShulkerTpaPlugin plugin;

   public GuiListener(ShulkerTpaPlugin plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void click(InventoryClickEvent event) {
      InventoryHolder rawHolder = event.getInventory().getHolder();
      if (rawHolder instanceof GuiManager.MenuHolder) {
         event.setCancelled(true);
         if (event.getWhoClicked() instanceof Player) {
            Player player = (Player)event.getWhoClicked();
            if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getInventory())) {
               GuiManager.MenuHolder holder = (GuiManager.MenuHolder)rawHolder;
               int slot = event.getRawSlot();
               ClickType click = event.getClick();
               UUID requestSender = (UUID)holder.requestSenders().get(slot);
               String action;
               if (requestSender != null) {
                  action = this.plugin.gui().actionForRequest(holder.menuPath(), click.isLeftClick(), click.isRightClick(), click.isShiftClick(), click == ClickType.MIDDLE);
               } else {
                  GuiManager.ClickActions clickActions = (GuiManager.ClickActions)holder.actions().get(slot);
                  action = clickActions == null ? null : clickActions.pick(click.isLeftClick(), click.isRightClick(), click.isShiftClick(), click == ClickType.MIDDLE);
               }

               if (action != null && !action.trim().isEmpty()) {
                  this.runAction(player, holder, action, requestSender);
               }
            }
         }
      }
   }

   private void runAction(Player player, GuiManager.MenuHolder holder, String rawAction, UUID requestSender) {
      String action = rawAction.trim();
      if (!action.equalsIgnoreCase("CLOSE") && !action.equalsIgnoreCase("CANCEL")) {
         if (action.equalsIgnoreCase("CONFIRM")) {
            Player target = holder.target() == null ? null : Bukkit.getPlayer(holder.target());
            RequestType type = holder.requestType();
            player.closeInventory();
            if (target != null && type != null) {
               this.play(player, "gui-sounds.confirm");
               (new ShulkerTpaCommand(this.plugin)).create(player, target, type);
            } else {
               this.plugin.messages().send(player, "player-not-found");
            }
         } else if (action.equalsIgnoreCase("ACCEPT")) {
            player.closeInventory();
            Player sender = requestSender == null ? null : Bukkit.getPlayer(requestSender);
            Bukkit.dispatchCommand(player, sender == null ? "tpaccept" : "tpaccept " + sender.getName());
            this.play(player, "gui-sounds.accept");
         } else if (action.equalsIgnoreCase("DENY")) {
            player.closeInventory();
            Player sender = requestSender == null ? null : Bukkit.getPlayer(requestSender);
            Bukkit.dispatchCommand(player, sender == null ? "tpdeny" : "tpdeny " + sender.getName());
            this.play(player, "gui-sounds.deny");
         } else if (action.toUpperCase().startsWith("COMMAND:")) {
            String command = action.substring("COMMAND:".length()).trim().replace("%player%", player.getName());
            player.closeInventory();
            if (command.startsWith("/")) {
               command = command.substring(1);
            }

            Bukkit.dispatchCommand(player, command);
         } else {
            if (action.toUpperCase().startsWith("CONSOLE:")) {
               String command = action.substring("CONSOLE:".length()).trim().replace("%player%", player.getName());
               player.closeInventory();
               if (command.startsWith("/")) {
                  command = command.substring(1);
               }

               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }

         }
      } else {
         player.closeInventory();
         this.play(player, "gui-sounds.close");
      }
   }

   private void play(Player player, String path) {
      if (this.plugin.getGui().getBoolean("gui-sounds.enabled", true)) {
         try {
            player.playSound(player.getLocation(), Sound.valueOf(this.plugin.getGui().getString(path, "UI_BUTTON_CLICK")), 1.0F, 1.0F);
         } catch (Exception var4) {
         }

      }
   }
}
