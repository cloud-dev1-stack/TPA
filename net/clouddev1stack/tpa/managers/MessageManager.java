package net.clouddev1stack.tpa.managers;

import net.clouddev1stack.tpa.BladeTpaPlugin;
import net.clouddev1stack.tpa.utils.ChatUtil;
import org.bukkit.command.CommandSender;

public class MessageManager {
   private final BladeTpaPlugin plugin;

   public MessageManager(BladeTpaPlugin plugin) {
      this.plugin = plugin;
   }

   public String raw(String key) {
      return this.plugin.getMessages().getString(key, key);
   }

   public String msg(String key, String... repl) {
      String s = this.raw(key).replace("%prefix%", this.plugin.getConfig().getString("prefix", ""));

      for(int i = 0; i + 1 < repl.length; i += 2) {
         s = s.replace(repl[i], repl[i + 1]);
      }

      return ChatUtil.color(s);
   }

   public void send(CommandSender s, String key, String... repl) {
      s.sendMessage(this.msg(key, repl));
   }
}
