package net.clouddev1stack.tpa.commands;

import net.clouddev1stack.tpa.ShulkerTpaPlugin;
import net.clouddev1stack.tpa.models.RequestType;
import net.clouddev1stack.tpa.models.TeleportRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ShulkerTpaCommand implements CommandExecutor, TabCompleter {
   private final ShulkerTpaPlugin p;

   public ShulkerTpaCommand(ShulkerTpaPlugin p) {
      this.p = p;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      String c = cmd.getName().toLowerCase();
      if (c.equals("tpa") && args.length > 0 && args[0].equalsIgnoreCase("reload")) {
         if (!sender.hasPermission("shulkertpa.reload")) {
            this.p.messages().send(sender, "no-permission");
            return true;
         } else {
            this.p.reloadAll();
            this.p.messages().send(sender, "reload-success");
            return true;
         }
      } else if (!(sender instanceof Player)) {
         this.p.messages().send(sender, "only-player");
         return true;
      } else {
         Player pl = (Player)sender;
         switch (c) {
            case "tpa" -> {
               return this.sendReq(pl, args, RequestType.TPA);
            }
            case "tpahere" -> {
               return this.sendReq(pl, args, RequestType.TPAHERE);
            }
            case "tpahereall" -> {
               return this.hereAll(pl);
            }
            case "tpaccept" -> {
               return this.accept(pl, args);
            }
            case "tpdeny" -> {
               return this.deny(pl, args);
            }
            case "tpalist" -> {
               return this.list(pl);
            }
            case "tpaignore" -> {
               return this.ignore(pl, args);
            }
            case "tpaignoreall" -> {
               return this.ignoreAll(pl);
            }
            default -> {
               return true;
            }
         }
      }
   }

   private boolean sendReq(Player s, String[] args, RequestType type) {
      String perm = type == RequestType.TPA ? "shulkertpa.tpa" : "shulkertpa.tpahere";
      if (!s.hasPermission(perm)) {
         this.p.messages().send(s, "no-permission");
         return true;
      } else if (args.length < 1) {
         this.p.messages().send(s, type == RequestType.TPA ? "usage-tpa" : "usage-tpahere");
         return true;
      } else {
         Player t = Bukkit.getPlayer(args[0]);
         if (t == null) {
            this.p.messages().send(s, "player-not-found");
            return true;
         } else if (t.equals(s)) {
            this.p.messages().send(s, "cannot-self");
            return true;
         } else if (!this.blockCombat(s) && !this.blockCombat(t)) {
            if (this.p.ignores().ignores(t.getUniqueId(), s.getUniqueId()) && !s.hasPermission("shulkertpa.ignore.bypass")) {
               this.p.messages().send(s, "ignored-by-target");
               return true;
            } else {
               int cd = this.p.getConfig().getInt("cooldown-seconds", 15);
               if (!s.hasPermission("shulkertpa.cooldown.bypass") && this.p.cooldowns().active(s.getUniqueId(), cd)) {
                  this.p.messages().send(s, "cooldown-active", "%cooldown%", String.valueOf(this.p.cooldowns().left(s.getUniqueId(), cd)));
                  return true;
               } else if (this.p.getConfig().getBoolean("gui-enabled", true)) {
                  this.p.gui().openConfirm(s, t, type);
                  return true;
               } else {
                  this.create(s, t, type);
                  return true;
               }
            }
         } else {
            return true;
         }
      }
   }

   public void create(Player s, Player t, RequestType type) {
      this.p.requests().add(s, t, type);
      this.p.cooldowns().mark(s.getUniqueId());
      this.p.messages().send(s, "request-sent", "%target%", t.getName());
      this.p.messages().send(t, type == RequestType.TPA ? "request-received-tpa" : "request-received-tpahere", "%sender%", s.getName());
      if (this.p.getConfig().getBoolean("clickable-chat-enabled", true)) {
         TextComponent a = new TextComponent(this.p.messages().msg("click-accept"));
         a.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/tpaccept " + s.getName()));
         TextComponent space = new TextComponent(" ");
         TextComponent d = new TextComponent(this.p.messages().msg("click-deny"));
         d.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/tpdeny " + s.getName()));
         t.spigot().sendMessage(new BaseComponent[]{a, space, d});
      }

      this.play(t, "sound.request");
   }

   private boolean accept(Player pl, String[] args) {
      if (!pl.hasPermission("shulkertpa.accept")) {
         this.p.messages().send(pl, "no-permission");
         return true;
      } else if (args.length == 0 && this.p.getConfig().getBoolean("gui-enabled", true) && this.p.requests().forTarget(pl.getUniqueId()).size() > 1) {
         this.p.gui().openRequests(pl, "accept-menu");
         return true;
      } else {
         TeleportRequest r = args.length > 0 ? this.p.requests().find(pl.getUniqueId(), args[0]) : this.first(pl);
         if (r == null) {
            this.p.messages().send(pl, "no-requests");
            if (this.p.getConfig().getBoolean("gui-enabled", true)) {
               this.p.gui().openRequests(pl, "accept-menu");
            }

            return true;
         } else {
            Player sender = Bukkit.getPlayer(r.getSender());
            if (sender == null) {
               this.p.requests().remove(r);
               this.p.messages().send(pl, "player-not-found");
               return true;
            } else {
               this.p.requests().remove(r);
               this.p.messages().send(pl, "request-accepted");
               this.p.messages().send(sender, "request-accepted");
               this.play(pl, "sound.accept");
               if (r.getType() == RequestType.TPA) {
                  this.p.teleports().warmup(sender, pl);
               } else {
                  this.p.teleports().warmup(pl, sender);
               }

               return true;
            }
         }
      }
   }

   private boolean deny(Player pl, String[] args) {
      if (!pl.hasPermission("shulkertpa.deny")) {
         this.p.messages().send(pl, "no-permission");
         return true;
      } else if (args.length == 0 && this.p.getConfig().getBoolean("gui-enabled", true) && this.p.requests().forTarget(pl.getUniqueId()).size() > 1) {
         this.p.gui().openRequests(pl, "deny-menu");
         return true;
      } else {
         TeleportRequest r = args.length > 0 ? this.p.requests().find(pl.getUniqueId(), args[0]) : this.first(pl);
         if (r == null) {
            this.p.messages().send(pl, "no-requests");
            if (this.p.getConfig().getBoolean("gui-enabled", true)) {
               this.p.gui().openRequests(pl, "deny-menu");
            }

            return true;
         } else {
            Player s = Bukkit.getPlayer(r.getSender());
            this.p.requests().remove(r);
            this.p.messages().send(pl, "request-denied");
            if (s != null) {
               this.p.messages().send(s, "request-denied-target", "%target%", pl.getName());
            }

            this.play(pl, "sound.deny");
            return true;
         }
      }
   }

   private boolean list(Player pl) {
      if (!pl.hasPermission("shulkertpa.list")) {
         this.p.messages().send(pl, "no-permission");
         return true;
      } else {
         List<TeleportRequest> list = this.p.requests().forTarget(pl.getUniqueId());
         if (list.isEmpty()) {
            this.p.messages().send(pl, "no-requests");
            return true;
         } else if (this.p.getConfig().getBoolean("gui-enabled", true)) {
            this.p.gui().openRequests(pl, "list-menu");
            return true;
         } else {
            this.p.messages().send(pl, "request-list-header");

            for(TeleportRequest r : list) {
               Player s = Bukkit.getPlayer(r.getSender());
               pl.sendMessage(this.p.messages().msg("request-list-line", "%sender%", s != null ? s.getName() : "Offline", "%type%", r.getType().name(), "%time_left%", String.valueOf(r.secondsLeft())));
            }

            return true;
         }
      }
   }

   private boolean ignore(Player pl, String[] args) {
      if (!pl.hasPermission("shulkertpa.ignore")) {
         this.p.messages().send(pl, "no-permission");
         return true;
      } else if (args.length < 1) {
         this.p.messages().send(pl, "usage-ignore");
         return true;
      } else {
         Player t = Bukkit.getPlayer(args[0]);
         if (t == null) {
            this.p.messages().send(pl, "player-not-found");
            return true;
         } else {
            boolean on = this.p.ignores().toggle(pl.getUniqueId(), t.getUniqueId());
            this.p.messages().send(pl, on ? "ignored-player" : "unignored-player", "%target%", t.getName());
            return true;
         }
      }
   }

   private boolean ignoreAll(Player pl) {
      if (!pl.hasPermission("shulkertpa.ignoreall")) {
         this.p.messages().send(pl, "no-permission");
         return true;
      } else {
         boolean on = this.p.ignores().toggleAll(pl.getUniqueId());
         this.p.messages().send(pl, on ? "ignore-all-enabled" : "ignore-all-disabled");
         return true;
      }
   }

   private boolean hereAll(Player pl) {
      if (!pl.hasPermission("shulkertpa.tpahereall")) {
         this.p.messages().send(pl, "no-permission");
         return true;
      } else {
         for(Player t : Bukkit.getOnlinePlayers()) {
            if (!t.equals(pl)) {
               this.create(pl, t, RequestType.TPAHERE);
            }
         }

         return true;
      }
   }

   private TeleportRequest first(Player p1) {
      List<TeleportRequest> l = this.p.requests().forTarget(p1.getUniqueId());
      return l.isEmpty() ? null : (TeleportRequest)l.get(0);
   }

   private boolean blockCombat(Player pl) {
      if (this.p.getConfig().getBoolean("combat-block", true) && !pl.hasPermission("shulkertpa.combat.bypass") && this.p.combat().inCombat(pl, this.p.getConfig().getInt("combat-tag-seconds", 15))) {
         this.p.messages().send(pl, "teleport-cancelled-combat");
         return true;
      } else {
         return false;
      }
   }

   private void play(Player pl, String path) {
      if (this.p.getConfig().getBoolean("sounds-enabled", true)) {
         try {
            pl.playSound(pl.getLocation(), Sound.valueOf(this.p.getConfig().getString(path, "ENTITY_EXPERIENCE_ORB_PICKUP")), 1.0F, 1.0F);
         } catch (Exception var4) {
         }
      }

   }

   public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
      if (args.length != 1) {
         return Collections.emptyList();
      } else {
         List<String> out = new ArrayList();
         if (c.getName().equalsIgnoreCase("tpa")) {
            out.add("reload");
         }

         for(Player p : Bukkit.getOnlinePlayers()) {
            out.add(p.getName());
         }

         return out;
      }
   }
}
