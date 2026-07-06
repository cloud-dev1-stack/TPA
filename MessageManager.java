package net.clouddev1stack.tpa.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
   private final Map<UUID, Long> cd = new HashMap();

   public boolean active(UUID u, int seconds) {
      return this.left(u, seconds) > 0L;
   }

   public long left(UUID u, int seconds) {
      Long t = (Long)this.cd.get(u);
      return t == null ? 0L : Math.max(0L, (long)seconds - (System.currentTimeMillis() - t) / 1000L);
   }

   public void mark(UUID u) {
      this.cd.put(u, System.currentTimeMillis());
   }

   public void clear() {
      this.cd.clear();
   }
}
