package net.clouddev1stack.tpa.managers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class IgnoreManager {
   private final Map<UUID, Set<UUID>> ignored = new HashMap();
   private final Set<UUID> all = new HashSet();

   public boolean toggle(UUID owner, UUID target) {
      Set<UUID> s = (Set)this.ignored.computeIfAbsent(owner, (k) -> new HashSet());
      if (s.contains(target)) {
         s.remove(target);
         return false;
      } else {
         s.add(target);
         return true;
      }
   }

   public boolean ignores(UUID owner, UUID sender) {
      return this.all.contains(owner) || ((Set)this.ignored.getOrDefault(owner, Collections.emptySet())).contains(sender);
   }

   public boolean toggleAll(UUID u) {
      if (this.all.contains(u)) {
         this.all.remove(u);
         return false;
      } else {
         this.all.add(u);
         return true;
      }
   }
}
