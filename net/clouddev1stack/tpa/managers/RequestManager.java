package net.clouddev1stack.tpa.managers;

import net.clouddev1stack.tpa.BladeTpaPlugin;
import net.clouddev1stack.tpa.models.RequestType;
import net.clouddev1stack.tpa.models.TeleportRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RequestManager {
   private final BladeTpaPlugin plugin;
   private final List<TeleportRequest> requests = new ArrayList();

   public RequestManager(BladeTpaPlugin p) {
      this.plugin = p;
      Bukkit.getScheduler().runTaskTimer(p, this::expire, 20L, 20L);
   }

   public void add(Player s, Player t, RequestType type) {
      this.requests.removeIf((r) -> r.getSender().equals(s.getUniqueId()) && r.getTarget().equals(t.getUniqueId()));
      this.requests.add(new TeleportRequest(s.getUniqueId(), t.getUniqueId(), type, (long)this.plugin.getConfig().getInt("request-expire-seconds", 60) * 1000L));
   }

   public List<TeleportRequest> forTarget(UUID target) {
      this.expire();
      List<TeleportRequest> list = (List)this.requests.stream().filter((r) -> r.getTarget().equals(target)).collect(Collectors.toList());
      if (this.plugin.getConfig().getBoolean("latest-request-first", true)) {
         Collections.reverse(list);
      }

      return list;
   }

   public TeleportRequest find(UUID target, String senderName) {
      for(TeleportRequest r : this.forTarget(target)) {
         Player p = Bukkit.getPlayer(r.getSender());
         if (p != null && p.getName().equalsIgnoreCase(senderName)) {
            return r;
         }
      }

      return null;
   }

   public void remove(TeleportRequest r) {
      this.requests.remove(r);
   }

   public void expire() {
      Iterator<TeleportRequest> it = this.requests.iterator();

      while(it.hasNext()) {
         TeleportRequest r = (TeleportRequest)it.next();
         if (r.expired()) {
            it.remove();
            Player s = Bukkit.getPlayer(r.getSender());
            Player t = Bukkit.getPlayer(r.getTarget());
            if (s != null) {
               this.plugin.messages().send(s, "request-expired-sender", "%target%", t != null ? t.getName() : "player");
            }

            if (t != null) {
               this.plugin.messages().send(t, "request-expired-target", "%sender%", s != null ? s.getName() : "player");
            }
         }
      }

   }
}
