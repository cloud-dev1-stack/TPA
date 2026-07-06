package com.clouddev1stack.tpa.managers;

import com.clouddev1stack.tpa.ShulkerTpaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class TeleportManager {
   private final ShulkerTpaPlugin plugin;
   private final Map<UUID, Integer> tasks = new HashMap();
   private final Map<UUID, Location> start = new HashMap();

   public TeleportManager(ShulkerTpaPlugin p) {
      this.plugin = p;
   }

   public boolean isTeleporting(UUID u) {
      return this.tasks.containsKey(u);
   }

   public void cancel(Player p, String key) {
      Integer id = (Integer)this.tasks.remove(p.getUniqueId());
      if (id != null) {
         Bukkit.getScheduler().cancelTask(id);
         this.start.remove(p.getUniqueId());
         this.plugin.messages().send(p, key);
      }

   }

   public void warmup(Player mover, Player destination) {
      int warm = mover.hasPermission("shulkertpa.warmup.bypass") ? 0 : this.plugin.getConfig().getInt("warmup-seconds", 5);
      if (warm <= 0) {
         this.doTp(mover, destination);
      } else {
         this.plugin.messages().send(mover, "teleport-starting", "%warmup%", String.valueOf(warm));
         this.start.put(mover.getUniqueId(), mover.getLocation().getBlock().getLocation());
         int[] left = new int[]{warm};
         int id = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            if (mover.isOnline() && destination.isOnline()) {
               if (this.plugin.getConfig().getBoolean("actionbar-enabled", true)) {
                  mover.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(this.plugin.messages().msg("teleport-actionbar", "%time%", String.valueOf(left[0]))));
               }

               int var10003 = left[0];
               int var10000 = left[0];
               left[0] = var10003 - 1;
               if (var10000 <= 0) {
                  Integer tid = (Integer)this.tasks.remove(mover.getUniqueId());
                  if (tid != null) {
                     Bukkit.getScheduler().cancelTask(tid);
                  }

                  this.start.remove(mover.getUniqueId());
                  this.doTp(mover, destination);
               }

            } else {
               this.cancel(mover, "teleport-cancelled-move");
            }
         }, 0L, 20L).getTaskId();
         this.tasks.put(mover.getUniqueId(), id);
      }
   }

   private void doTp(Player p, Player dest) {
      p.teleport(dest.getLocation());
      this.plugin.messages().send(p, "teleport-success");
      if (this.plugin.getConfig().getBoolean("sounds-enabled", true)) {
         p.playSound(p.getLocation(), Sound.valueOf(this.plugin.getConfig().getString("sound.teleport", "ENTITY_ENDERMAN_TELEPORT")), 1.0F, 1.0F);
      }

      if (this.plugin.getConfig().getBoolean("particles-enabled", true)) {
         p.getWorld().spawnParticle(Particle.valueOf(this.plugin.getConfig().getString("particle", "PORTAL")), p.getLocation(), 40);
      }

   }

   public Location startLoc(UUID u) {
      return (Location)this.start.get(u);
   }
}
