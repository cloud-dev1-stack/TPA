package net.clouddev1stack.tpa.managers;

import net.clouddev1stack.tpa.BladeTpaPlugin;
import net.clouddev1stack.tpa.models.RequestType;
import net.clouddev1stack.tpa.models.TeleportRequest;
import net.clouddev1stack.tpa.utils.ChatUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiManager {
   private final BladeTpaPlugin plugin;

   public GuiManager(BladeTpaPlugin plugin) {
      this.plugin = plugin;
   }

   public void openConfirm(Player player, Player target, RequestType type) {
      String path = type == RequestType.TPA ? "confirm-tpa" : "confirm-tpahere";
      MenuHolder holder = new MenuHolder(path, type, target.getUniqueId());
      Inventory inv = Bukkit.createInventory(holder, this.validSize(this.plugin.getGui().getInt(path + ".size", 27)), this.rep(this.plugin.getGui().getString(path + ".title", "Confirm"), player, target, (TeleportRequest)null));
      holder.setInventory(inv);
      this.fill(inv, path, player, target, (TeleportRequest)null);
      if (this.plugin.getGui().isConfigurationSection(path + ".items")) {
         for(String key : this.plugin.getGui().getConfigurationSection(path + ".items").getKeys(false)) {
            String base = path + ".items." + key;
            int slot = this.plugin.getGui().getInt(base + ".slot", 0);
            if (this.validSlot(inv, slot)) {
               inv.setItem(slot, this.item(base, player, target, (TeleportRequest)null));
               holder.actions().put(slot, this.actions(base, key.equalsIgnoreCase("confirm") ? "CONFIRM" : "CLOSE"));
            }
         }
      }

      player.openInventory(inv);
   }

   public void openRequests(Player player, String menuPath) {
      List<TeleportRequest> list = this.plugin.requests().forTarget(player.getUniqueId());
      MenuHolder holder = new MenuHolder(menuPath, (RequestType)null, (UUID)null);
      Inventory inv = Bukkit.createInventory(holder, this.validSize(this.plugin.getGui().getInt(menuPath + ".size", 54)), this.rep(this.plugin.getGui().getString(menuPath + ".title", "Requests"), player, (Player)null, (TeleportRequest)null));
      holder.setInventory(inv);
      this.fill(inv, menuPath, player, (Player)null, (TeleportRequest)null);
      List<Integer> slots = this.plugin.getGui().getIntegerList(menuPath + ".request-item.slots");
      int fallbackSlot = this.plugin.getGui().getInt(menuPath + ".request-item.start-slot", 0);
      int index = 0;

      for(TeleportRequest request : list) {
         int slot = slots.isEmpty() ? fallbackSlot++ : (index < slots.size() ? (Integer)slots.get(index) : -1);
         ++index;
         if (this.validSlot(inv, slot)) {
            inv.setItem(slot, this.item(menuPath + ".request-item", player, Bukkit.getPlayer(request.getSender()), request));
            holder.requestSenders().put(slot, request.getSender());
            holder.actions().put(slot, this.actions(menuPath + ".request-item", "ACCEPT"));
         }
      }

      if (this.plugin.getGui().isConfigurationSection(menuPath + ".items")) {
         for(String key : this.plugin.getGui().getConfigurationSection(menuPath + ".items").getKeys(false)) {
            String base = menuPath + ".items." + key;
            int slot = this.plugin.getGui().getInt(base + ".slot", 0);
            if (this.validSlot(inv, slot)) {
               inv.setItem(slot, this.item(base, player, (Player)null, (TeleportRequest)null));
               holder.actions().put(slot, this.actions(base, "CLOSE"));
            }
         }
      }

      player.openInventory(inv);
   }

   public String actionForRequest(String menuPath, boolean leftClick, boolean rightClick, boolean shiftClick, boolean middleClick) {
      return this.actions(menuPath + ".request-item", "ACCEPT").pick(leftClick, rightClick, shiftClick, middleClick);
   }

   public ClickActions actions(String base, String fallback) {
      String legacy = this.plugin.getGui().getString(base + ".action", fallback);
      String left = this.plugin.getGui().getString(base + ".left-click-action", legacy);
      String right = this.plugin.getGui().getString(base + ".right-click-action", left);
      String shift = this.plugin.getGui().getString(base + ".shift-click-action", left);
      String middle = this.plugin.getGui().getString(base + ".middle-click-action", left);
      return new ClickActions(left, right, shift, middle);
   }

   private void fill(Inventory inv, String path, Player player, Player target, TeleportRequest request) {
      if (this.plugin.getGui().getBoolean(path + ".fill.enabled", true)) {
         ItemStack fill = this.simpleItem(this.plugin.getGui().getString(path + ".fill.material", "BLACK_STAINED_GLASS_PANE"), this.rep(this.plugin.getGui().getString(path + ".fill.name", " "), player, target, request), this.colorList(this.plugin.getGui().getStringList(path + ".fill.lore"), player, target, request));

         for(int i = 0; i < inv.getSize(); ++i) {
            inv.setItem(i, fill);
         }

      }
   }

   private ItemStack item(String base, Player player, Player target, TeleportRequest request) {
      Material material = Material.matchMaterial(this.plugin.getGui().getString(base + ".material", "STONE"));
      if (material == null) {
         material = Material.STONE;
      }

      int amount = Math.max(1, Math.min(64, this.plugin.getGui().getInt(base + ".amount", 1)));
      ItemStack item = new ItemStack(material, amount);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.setDisplayName(this.rep(this.plugin.getGui().getString(base + ".name", "&fItem"), player, target, request));
         meta.setLore(this.colorList(this.plugin.getGui().getStringList(base + ".lore"), player, target, request));
         if (this.plugin.getGui().contains(base + ".custom-model-data")) {
            try {
               meta.setCustomModelData(this.plugin.getGui().getInt(base + ".custom-model-data"));
            } catch (Throwable var10) {
            }
         }

         if (this.plugin.getGui().getBoolean(base + ".glow", false)) {
            Enchantment glowEnchant = Enchantment.getByKey(NamespacedKey.minecraft("unbreaking"));
            if (glowEnchant == null) {
               glowEnchant = Enchantment.getByName("DURABILITY");
            }

            if (glowEnchant != null) {
               meta.addEnchant(glowEnchant, 1, true);
               meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }
         }

         meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         item.setItemMeta(meta);
      }

      return item;
   }

   private ItemStack simpleItem(String materialName, String name, List<String> lore) {
      Material material = Material.matchMaterial(materialName);
      if (material == null) {
         material = Material.BLACK_STAINED_GLASS_PANE;
      }

      ItemStack item = new ItemStack(material);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.setDisplayName(name);
         meta.setLore(lore);
         item.setItemMeta(meta);
      }

      return item;
   }

   private List<String> colorList(List<String> lines, Player player, Player target, TeleportRequest request) {
      List<String> out = new ArrayList();

      for(String line : lines) {
         out.add(this.rep(line, player, target, request));
      }

      return out;
   }

   private String rep(String text, Player player, Player target, TeleportRequest request) {
      if (text == null) {
         text = "";
      }

      Player sender = request == null ? null : Bukkit.getPlayer(request.getSender());
      Player receiver = request == null ? null : Bukkit.getPlayer(request.getTarget());
      return ChatUtil.color(text.replace("%prefix%", this.plugin.getConfig().getString("prefix", "")).replace("%player%", player != null ? player.getName() : "").replace("%target%", target != null ? target.getName() : (receiver != null ? receiver.getName() : "")).replace("%sender%", sender != null ? sender.getName() : "Offline").replace("%receiver%", receiver != null ? receiver.getName() : (player != null ? player.getName() : "")).replace("%type%", request != null ? request.getType().name() : "").replace("%time_left%", request != null ? String.valueOf(request.secondsLeft()) : "").replace("%cooldown%", "").replace("%warmup%", String.valueOf(this.plugin.getConfig().getInt("warmup-seconds", 5))));
   }

   private int validSize(int size) {
      if (size < 9) {
         return 9;
      } else {
         return size > 54 ? 54 : size / 9 * 9;
      }
   }

   private boolean validSlot(Inventory inv, int slot) {
      return slot >= 0 && slot < inv.getSize();
   }

   public static final class MenuHolder implements InventoryHolder {
      private final String menuPath;
      private final RequestType requestType;
      private final UUID target;
      private final Map<Integer, ClickActions> actions = new HashMap();
      private final Map<Integer, UUID> requestSenders = new HashMap();
      private Inventory inventory;

      public MenuHolder(String menuPath, RequestType requestType, UUID target) {
         this.menuPath = menuPath;
         this.requestType = requestType;
         this.target = target;
      }

      public String menuPath() {
         return this.menuPath;
      }

      public RequestType requestType() {
         return this.requestType;
      }

      public UUID target() {
         return this.target;
      }

      public Map<Integer, ClickActions> actions() {
         return this.actions;
      }

      public Map<Integer, UUID> requestSenders() {
         return this.requestSenders;
      }

      public void setInventory(Inventory inventory) {
         this.inventory = inventory;
      }

      public Inventory getInventory() {
         return this.inventory;
      }
   }

   public static final class ClickActions {
      private final String left;
      private final String right;
      private final String shift;
      private final String middle;

      public ClickActions(String left, String right, String shift, String middle) {
         this.left = left;
         this.right = right;
         this.shift = shift;
         this.middle = middle;
      }

      public String pick(boolean leftClick, boolean rightClick, boolean shiftClick, boolean middleClick) {
         if (middleClick && this.middle != null && !this.middle.isEmpty()) {
            return this.middle;
         } else if (shiftClick && this.shift != null && !this.shift.isEmpty()) {
            return this.shift;
         } else if (rightClick && this.right != null && !this.right.isEmpty()) {
            return this.right;
         } else if (leftClick && this.left != null && !this.left.isEmpty()) {
            return this.left;
         } else {
            return this.left != null ? this.left : "";
         }
      }
   }
}
