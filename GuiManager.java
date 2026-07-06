package net.clouddev1stack.tpa.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class CombatManager {
   private final Map<UUID, Long> tagged = new HashMap();

   public void tag(Player p) {
      this.tagged.put(p.getUniqueId(), System.currentTimeMillis());
   }

   public boolean inCombat(Player p, int seconds) {
      Long t = (Long)this.tagged.get(p.getUniqueId());
      if (t == null) {
         return false;
      } else if (System.currentTimeMillis() - t > (long)seconds * 1000L) {
         this.tagged.remove(p.getUniqueId());
         return false;
      } else {
         return true;
      }
   }
}
