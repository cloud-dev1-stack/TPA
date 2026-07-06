package com.clouddev1stack.tpa;

import com.clouddev1stack.tpa.commands.ShulkerTpaCommand;
import com.clouddev1stack.tpa.listeners.GuiListener;
import com.clouddev1stack.tpa.listeners.PlayerListener;
import com.clouddev1stack.tpa.managers.CombatManager;
import com.clouddev1stack.tpa.managers.CooldownManager;
import com.clouddev1stack.tpa.managers.GuiManager;
import com.clouddev1stack.tpa.managers.IgnoreManager;
import com.clouddev1stack.tpa.managers.MessageManager;
import com.clouddev1stack.tpa.managers.RequestManager;
import com.clouddev1stack.tpa.managers.TeleportManager;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ShulkerTpaPlugin extends JavaPlugin {
   private FileConfiguration messages;
   private FileConfiguration gui;
   private MessageManager mm;
   private RequestManager rm;
   private CooldownManager cm;
   private IgnoreManager im;
   private TeleportManager tm;
   private CombatManager combat;
   private GuiManager gm;

   public void onEnable() {
      this.saveDefaultConfig();
      this.saveResource("messages.yml", false);
      this.saveResource("gui.yml", false);
      this.reloadFiles();
      this.mm = new MessageManager(this);
      this.cm = new CooldownManager();
      this.im = new IgnoreManager();
      this.combat = new CombatManager();
      this.tm = new TeleportManager(this);
      this.rm = new RequestManager(this);
      this.gm = new GuiManager(this);
      ShulkerTpaCommand cmd = new ShulkerTpaCommand(this);
      this.getCommand("tpa").setExecutor(cmd);
      this.getCommand("tpahere").setExecutor(cmd);
      this.getCommand("tpahereall").setExecutor(cmd);
      this.getCommand("tpaccept").setExecutor(cmd);
      this.getCommand("tpdeny").setExecutor(cmd);
      this.getCommand("tpalist").setExecutor(cmd);
      this.getCommand("tpaignore").setExecutor(cmd);
      this.getCommand("tpaignoreall").setExecutor(cmd);
      this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this);
   }

   public void reloadAll() {
      this.reloadConfig();
      this.reloadFiles();
   }

   private void reloadFiles() {
      this.messages = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "messages.yml"));
      this.gui = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "gui.yml"));
   }

   public FileConfiguration getMessages() {
      return this.messages;
   }

   public FileConfiguration getGui() {
      return this.gui;
   }

   public MessageManager messages() {
      return this.mm;
   }

   public RequestManager requests() {
      return this.rm;
   }

   public CooldownManager cooldowns() {
      return this.cm;
   }

   public IgnoreManager ignores() {
      return this.im;
   }

   public TeleportManager teleports() {
      return this.tm;
   }

   public CombatManager combat() {
      return this.combat;
   }

   public GuiManager gui() {
      return this.gm;
   }
}
