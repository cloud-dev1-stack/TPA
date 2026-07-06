package net.clouddev1stack.tpa.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;

public final class ChatUtil {
   private static final Pattern AMP_HEX = Pattern.compile("&#([A-Fa-f0-9]{6})");
   private static final Pattern TAG_HEX = Pattern.compile("<#([A-Fa-f0-9]{6})>");

   private ChatUtil() {
   }

   public static String color(String input) {
      if (input != null && !input.isEmpty()) {
         String text = replaceHex(TAG_HEX.matcher(input));
         text = replaceHex(AMP_HEX.matcher(text));
         return ChatColor.translateAlternateColorCodes('&', text);
      } else {
         return "";
      }
   }

   private static String replaceHex(Matcher matcher) {
      StringBuffer buffer;
      String replacement;
      for(buffer = new StringBuffer(); matcher.find(); matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement))) {
         String hex = "#" + matcher.group(1);

         try {
            replacement = net.md_5.bungee.api.ChatColor.of(hex).toString();
         } catch (Throwable var5) {
            replacement = "";
         }
      }

      matcher.appendTail(buffer);
      return buffer.toString();
   }
}
