package com.clouddev1stack.tpa.listeners;

import com.clouddev1stack.tpa.ShulkerTpaPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {
   private final ShulkerTpaPlugin p;

   public PlayerListener(ShulkerTpaPlugin p) {
      this.p = p;
   }

   @EventHandler
   public void move(PlayerMoveEvent e) {
      if (this.p.getConfig().getBoolean("cancel-on-move", true)) {
         Player pl = e.getPlayer();
         if (this.p.teleports().isTeleporting(pl.getUniqueId())) {
            Location s = this.p.teleports().startLoc(pl.getUniqueId());
            if (s != null && e.getTo() != null && (s.getBlockX() != e.getTo().getBlockX() || s.getBlockY() != e.getTo().getBlockY() || s.getBlockZ() != e.getTo().getBlockZ())) {
               this.p.teleports().cancel(pl, "teleport-cancelled-move");
            }

         }
      }
   }

   @EventHandler
   public void damage(EntityDamageEvent e) {
      if (e.getEntity() instanceof Player) {
         Player pl = (Player)e.getEntity();
         if (this.p.getConfig().getBoolean("cancel-on-damage", true) && this.p.teleports().isTeleporting(pl.getUniqueId())) {
            this.p.teleports().cancel(pl, "teleport-cancelled-damage");
         }
      }

      if (e instanceof EntityDamageByEntityEvent ev) {
         if (ev.getEntity() instanceof Player) {
            this.p.combat().tag((Player)ev.getEntity());
         }

         if (ev.getDamager() instanceof Player) {
            this.p.combat().tag((Player)ev.getDamager());
         }
      }

   }
}
