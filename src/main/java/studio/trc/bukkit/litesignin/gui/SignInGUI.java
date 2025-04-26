package studio.trc.bukkit.litesignin.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import studio.trc.bukkit.litesignin.api.Storage;
import studio.trc.bukkit.litesignin.config.ConfigurationUtil;
import studio.trc.bukkit.litesignin.config.ConfigurationType;
import studio.trc.bukkit.litesignin.util.*;
import studio.trc.bukkit.litesignin.queue.SignInQueue;
import studio.trc.bukkit.litesignin.gui.SignInGUIColumn.KeyType;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

@SuppressWarnings("deprecation")
public class SignInGUI
{
    public static SignInInventory getGUI(Player player) {
        ConfigurationSection section = ConfigurationUtil.getConfig(ConfigurationType.GUI_SETTINGS).getConfigurationSection(MessageUtil.getLanguage() + ".SignIn-GUI-Settings");
        String formattedDate = new SimpleDateFormat(section.getString("Date-Format", "yyyy-MM-dd")).format(new Date());
        String replacedText = replace(player, section.getString("GUI-Name"), "{date}", formattedDate);
        String title = MessageUtil.toColor(replacedText);
        Inventory gui = Bukkit.createInventory(null, 54, title);
        List<SignInGUIColumn> columns = new ArrayList<>();

        for (SignInGUIColumn items : getKey(player)) {
            gui.setItem(items.getKeyPostion(), items.getItemStack());
            columns.add(items);
        }
        for (SignInGUIColumn items : getOthers(player)) {
            gui.setItem(items.getKeyPostion(), items.getItemStack());
            columns.add(items);
        }
        return new SignInInventory(gui, columns);
    }
    
    public static SignInInventory getGUI(Player player, int month) {
        Inventory gui;
        ConfigurationSection section = ConfigurationUtil.getConfig(ConfigurationType.GUI_SETTINGS).getConfigurationSection(MessageUtil.getLanguage() + ".SignIn-GUI-Settings");

        Date now = new Date();
        if (month == SignInDate.getInstance(now).getMonth()) {
            String formattedDate = new SimpleDateFormat(section.getString("Date-Format", "yyyy-MM-dd")).format(now);
            String replacedText = replace(player, section.getString("GUI-Name"), "{date}", formattedDate);
            gui = Bukkit.createInventory(null, 54, MessageUtil.toColor(replacedText));
        } else {
            String replacedText = replace(player, section.getString("Specified-Month-GUI-Name"), "{month}", String.valueOf(month));
            gui = Bukkit.createInventory(null, 54, MessageUtil.toColor(replacedText));
        }

        List<SignInGUIColumn> columns = new ArrayList<>();
        for (SignInGUIColumn items : getKey(player, month)) {
            gui.setItem(items.getKeyPostion(), items.getItemStack());
            columns.add(items);
        }
        for (SignInGUIColumn items : getOthers(player, month)) {
            gui.setItem(items.getKeyPostion(), items.getItemStack());
            columns.add(items);
        }
        return new SignInInventory(gui, columns, month);
    }
    
    public static SignInInventory getGUI(Player player, int month, int year) {
        Inventory gui;
        SignInDate today = SignInDate.getInstance(new Date());
        ConfigurationSection section = ConfigurationUtil.getConfig(ConfigurationType.GUI_SETTINGS).getConfigurationSection(MessageUtil.getLanguage() + ".SignIn-GUI-Settings");

        if (year == today.getYear()) {
            if (month == today.getMonth()) {
                String formattedDate = new SimpleDateFormat(section.getString("Date-Format", "yyyy-MM-dd")).format(new Date());
                String replacedText = replace(player, section.getString("GUI-Name"), "{date}", formattedDate);
                gui = Bukkit.createInventory(null, 54, MessageUtil.toColor(replacedText));
            } else {
                String replacedText = replace(player, section.getString("Specified-Month-GUI-Name"), "{month}", String.valueOf(month));
                gui = Bukkit.createInventory(null, 54, MessageUtil.toColor(replacedText));
            }
        } else {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{month}", String.valueOf(month));
            placeholders.put("{year}", String.valueOf(year));
            String replaced = MessageUtil.replacePlaceholders(player, section.getString("Specified-Year-GUI-Name", ""), placeholders);
            gui = Bukkit.createInventory(null, 54, replaced);
        }

        List<SignInGUIColumn> columns = new ArrayList<>();
        for (SignInGUIColumn items : getKey(player, month, year)) {
            gui.setItem(items.getKeyPostion(), items.getItemStack());
            columns.add(items);
        }
        for (SignInGUIColumn items : getOthers(player, month, year)) {
            gui.setItem(items.getKeyPostion(), items.getItemStack());
            columns.add(items);
        }
        return new SignInInventory(gui, columns, month, year);
    }

    public static ItemStack getItem(ConfigurationSection section, String prefix, Player player, int i, Map<String, String> placeholders) {
        ItemStack key;
        try {
            if (section.get(prefix + ".Data") != null) {
                key = new ItemStack(Material.valueOf(section.getString(prefix + ".Item", "").toUpperCase()), i + 1, (short) section.getInt(prefix + ".Data"));
            } else {
                key = new ItemStack(Material.valueOf(section.getString(prefix + ".Item", "").toUpperCase()), i + 1);
            }
        } catch (IllegalArgumentException ex) {
            key = new ItemStack(Material.BARRIER, i + 1);
        }
        if (section.get(prefix + ".Head-Owner") != null) {
            PluginControl.setHead(key, replace(player, section.getString(prefix + ".Head-Owner"), "{player}", player.getName()));
        }
        if (section.get(prefix + ".Amount") != null) {
            key.setAmount(section.getInt(prefix + ".Amount"));
        }
        if (section.get(prefix + ".Head-Textures") != null) {
            setHeadTextures(player, MessageUtil.getLanguage() + ".SignIn-GUI-Settings.Key." + prefix + ".Head-Textures", key);
        }
        if (section.get(prefix + ".Custom-Model-Data") != null) {
            setCustomModelData(MessageUtil.getLanguage() + ".SignIn-GUI-Settings.Key." + prefix + ".Custom-Model-Data", key);
        }
        ItemMeta im = key.getItemMeta();
        if (im != null) {
            if (section.get(prefix + ".Lore") != null) {
                List<String> lore = new ArrayList<>();
                for (String loreLine : section.getStringList(prefix + ".Lore")) {
                    lore.add(MessageUtil.replacePlaceholders(player, loreLine, placeholders));
                }
                im.setLore(lore);
            }
            if (section.get(prefix + ".Enchantment") != null) {
                setEnchantments(MessageUtil.getLanguage() + ".SignIn-GUI-Settings.Key." + prefix + ".Enchantment", im);
            }
            if (section.get(prefix + ".Hide-Enchants") != null) PluginControl.hideEnchants(im);
            if (section.get(prefix + ".Display-Name") != null) im.setDisplayName(MessageUtil.toColor(replace(player, section.getString(prefix + ".Display-Name"), "{day}", String.valueOf(i + 1))));
        }
        key.setItemMeta(im);
        return key;
    }

    public static Set<SignInGUIColumn> getKey(Player player, int month, int year) {
        Set<SignInGUIColumn> set = new LinkedHashSet<>();
        SignInDate today = SignInDate.getInstance(new Date());
        if (today.getMonth() == month && today.getYear() == year) return getKey(player);
        Storage playerData = Storage.getPlayer(player);
        String queue = String.valueOf(SignInQueue.getInstance().getRank(playerData.getUserUUID()));
        String continuous = String.valueOf(playerData.getContinuousSignIn());
        String totalNumber = String.valueOf(playerData.getCumulativeNumber());
        String cards = String.valueOf(playerData.getRetroactiveCard());
        int nextPageMonth = getNextPageMonth(month);
        int nextPageYear = getNextPageYear(month, year);
        int previousPageMonth = getPreviousPageMonth(month);
        int previousPageYear = getPreviousPageYear(month, year);
        int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            days[1] = 29;
        }
        SignInDate specifiedDate = SignInDate.getInstance(year, month, 1);
        ConfigurationSection section = ConfigurationUtil.getConfig(ConfigurationType.GUI_SETTINGS).getConfigurationSection(MessageUtil.getLanguage() + ".SignIn-GUI-Settings.Key");
        if (specifiedDate.compareTo(today) == -1) {
            List<ItemStack> items = new LinkedList<>();
            List<KeyType> keys = new LinkedList<>();
            for (int i = 0; i < days[month-1]; i++) {
                SignInDate historicalDate = SignInDate.getInstance(year, month, i + 1);
                ItemStack key;
                KeyType keyType;
                Map<String, String> placeholder = getPlaceholdersOfItemLore(i, continuous, queue, totalNumber, cards, nextPageMonth, nextPageYear, previousPageMonth, previousPageYear, historicalDate);
                if (playerData.alreadySignIn(historicalDate)) {
                    key = getItem(section, "Already-SignIn", player, i, placeholder);
                    keyType = KeyType.ALREADY_SIGNIN;
                } else {
                    key = getItem(section, "Missed-SignIn", player, i, placeholder);
                    keyType = KeyType.MISSED_SIGNIN;
                }
                items.add(key);
                keys.add(keyType);
            }
            if (section.get("Slots") != null) {
                int i = 0;
                for (String slots : section.getStringList("Slots")) {
                    try {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month - 1, i + 1);
                        set.add(new SignInGUIColumn(items.get(i), Integer.parseInt(slots), SignInDate.getInstance(cal.getTime()), keys.get(i)));
                    } catch (IndexOutOfBoundsException ignored) {}
                    i++;
                }
            }
            return set;
        } else {
            List<ItemStack> items = new LinkedList<>();
            List<KeyType> keys = new LinkedList<>();
            for (int i = 0;i < days[month - 1];i++) {
                Map<String, String> placeholder = getPlaceholdersOfItemLore(i, continuous, queue, totalNumber, cards, nextPageMonth, nextPageYear, previousPageMonth, previousPageYear, null);
                ItemStack key = getItem(section, "Comming-Soon", player, i, placeholder);
                items.add(key);
                keys.add(KeyType.COMMING_SOON);
            }
            if (section.get("Slots") != null) {
                int i = 0;
                for (String slots : section.getStringList("Slots")) {
                    try {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month - 1, i + 1);
                        set.add(new SignInGUIColumn(items.get(i), Integer.parseInt(slots), SignInDate.getInstance(cal.getTime()), keys.get(i)));
                    } catch (IndexOutOfBoundsException ignored) {}
                    i++;
                }
            }
            return set;
        }
    }
    
    /**
     * Return key buttons.
     */
    public static Set<SignInGUIColumn> getKey(Player player, int month) {
        SignInDate today = SignInDate.getInstance(new Date());
        if (today.getMonth() == month) return getKey(player);
        else return getKey(player, month, today.getYear());
    }
    
    /**
     * Return key buttons.
     */
    public static Set<SignInGUIColumn> getKey(Player player) {
        Set<SignInGUIColumn> set = new LinkedHashSet<>();
        Storage playerData = Storage.getPlayer(player);
        String queue = String.valueOf(SignInQueue.getInstance().getRank(playerData.getUserUUID()));
        String continuous = String.valueOf(playerData.getContinuousSignIn());
        String totalNumber = String.valueOf(playerData.getCumulativeNumber());
        String cards = String.valueOf(playerData.getRetroactiveCard());
        String[] times = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).split("-");
        ConfigurationSection section = ConfigurationUtil.getConfig(ConfigurationType.GUI_SETTINGS).getConfigurationSection(MessageUtil.getLanguage() + ".SignIn-GUI-Settings.Key");
        int year = Integer.parseInt(times[0]);
        int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            days[1] = 29;
        }
        int month = Integer.parseInt(times[1]);
        int day = Integer.parseInt(times[2]);
        int nextPageMonth = getNextPageMonth(month);
        int nextPageYear = getNextPageYear(month, year);
        int previousPageMonth = getPreviousPageMonth(month);
        int previousPageYear = getPreviousPageYear(month, year);
        List<ItemStack> items = new LinkedList<>();
        List<KeyType> keys = new LinkedList<>();
        for (int i = 0;i < days[month - 1];i++) {
            SignInDate historicalDate = SignInDate.getInstance(year, month, i + 1);
            Map<String, String> placeholder = getPlaceholdersOfItemLore(i, continuous, queue, totalNumber, cards, nextPageMonth, nextPageYear, previousPageMonth, previousPageYear, historicalDate);
            if (i + 1 < day) {
                ItemStack key;
                KeyType keyType;
                if (playerData.alreadySignIn(historicalDate)) {
                    key = getItem(section, "Already-SignIn", player, i, placeholder);
                    keyType = KeyType.ALREADY_SIGNIN;
                } else {
                    key = getItem(section, "Missed-SignIn", player, i, placeholder);
                    keyType = KeyType.MISSED_SIGNIN;
                }
                items.add(key);
                keys.add(keyType);
            } else if (i + 1 == day) {
                ItemStack key;
                KeyType keyType;
                if (playerData.alreadySignIn(historicalDate)) {
                    key = getItem(section, "Already-SignIn", player, i, placeholder);
                    keyType = KeyType.ALREADY_SIGNIN;
                } else {
                    key = getItem(section, "Nothing-SignIn", player, i, placeholder);
                    keyType = KeyType.NOTHING_SIGNIN;
                }
                items.add(key);
                keys.add(keyType);
            } else if (i + 1 > day) {
                ItemStack key = getItem(section, "Comming-Soon", player, i, placeholder);
                items.add(key);
                keys.add(KeyType.COMMING_SOON);
            }
        }
        if (section.get("Slots") != null) {
            int i = 0;
            for (String slots : section.getStringList("Slots")) {
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month - 1, i + 1);
                    set.add(new SignInGUIColumn(items.get(i), Integer.parseInt(slots), SignInDate.getInstance(cal.getTime()), keys.get(i)));
                } catch (IndexOutOfBoundsException ignored) {}
                i++;
            }
        }
        return set;
    }

    public static Set<SignInGUIColumn> getOthers(Player player) {
        SignInDate today = SignInDate.getInstance(new Date());
        return getOthers(player, today.getMonth(), today.getYear());
    }
    
    public static Set<SignInGUIColumn> getOthers(Player player, int month) {
        SignInDate today = SignInDate.getInstance(new Date());
        return getOthers(player, month, today.getYear());
    }
    
    public static Set<SignInGUIColumn> getOthers(Player player, int month, int year) {
        Set<SignInGUIColumn> set = new HashSet<>();
        Storage playerData = Storage.getPlayer(player);
        String queue = String.valueOf(SignInQueue.getInstance().getRank(playerData.getUserUUID()));
        String continuous = String.valueOf(playerData.getContinuousSignIn());
        String totalNumber = String.valueOf(playerData.getCumulativeNumber());
        String cards = String.valueOf(playerData.getRetroactiveCard());
        ConfigurationSection section = ConfigurationUtil.getConfig(ConfigurationType.GUI_SETTINGS).getConfigurationSection(MessageUtil.getLanguage() + ".SignIn-GUI-Settings.Others");
        int nextPageMonth = getNextPageMonth(month);
        int nextPageYear = getNextPageYear(month, year);
        int previousPageMonth = getPreviousPageMonth(month);
        int previousPageYear = getPreviousPageYear(month, year);
        if (section != null) for (String items : section.getKeys(false)) {
            ItemStack other;
            try {
                if (section.get(items + ".Data") != null) {
                    other = new ItemStack(Material.valueOf(section.getString(items + ".Item", "").toUpperCase()), 1, (short) section.getInt(items + ".Data"));
                } else {
                    other = new ItemStack(Material.valueOf(section.getString(items + ".Item", "").toUpperCase()), 1);
                }
            } catch (IllegalArgumentException ex) {
                other = new ItemStack(Material.BARRIER);
            }
            if (section.get(items + ".Head-Owner") != null) {
                PluginControl.setHead(other, replace(player, section.getString(items + ".Head-Owner"), "{player}", player.getName()));
            }
            if (section.get(items + ".Head-Textures") != null) {
                setHeadTextures(player, MessageUtil.getLanguage() + ".SignIn-GUI-Settings.Others." + items + ".Head-Textures", other);
            }
            if (section.get(items + ".Custom-Model-Data") != null) {
                setCustomModelData(MessageUtil.getLanguage() + ".SignIn-GUI-Settings.Others." + items + ".Custom-Model-Data", other);
            }
            ItemMeta im = other.getItemMeta();
            if (im != null) {
                if (section.get(items + ".Lore") != null) {
                    List<String> lore = new ArrayList<>();
                    Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                    placeholders.put("{continuous}", continuous);
                    placeholders.put("{queue}", queue);
                    placeholders.put("{total-number}", totalNumber);
                    placeholders.put("{cards}", cards);
                    placeholders.put("{month}", String.valueOf(month));
                    placeholders.put("{year}", String.valueOf(year));
                    placeholders.put("{nextPageMonth}", String.valueOf(nextPageMonth));
                    placeholders.put("{nextPageYear}", String.valueOf(nextPageYear));
                    placeholders.put("{previousPageMonth}", String.valueOf(previousPageMonth));
                    placeholders.put("{previousPageYear}", String.valueOf(previousPageYear));
                    for (String loreLine : section.getStringList(items + ".Lore")) {
                        lore.add(MessageUtil.replacePlaceholders(player, loreLine, placeholders));
                    }
                    im.setLore(lore);
                }
                if (section.get(items + ".Enchantment") != null) {
                    setEnchantments(MessageUtil.getLanguage() + ".SignIn-GUI-Settings.Others." + items + ".Enchantment", im);
                }
                if (section.get(items + ".Hide-Enchants") != null) PluginControl.hideEnchants(im);
                if (section.get(items + ".Display-Name") != null)
                    im.setDisplayName(MessageUtil.toColor(MessageUtil.toPlaceholderAPIResult(section.getString(items + ".Display-Name"), player)));
            }
            other.setItemMeta(im);
            other.setAmount(section.get(items + ".Amount") != null ? section.getInt(items + ".Amount") : 1);
            if (section.get(items + ".Slots") != null) {
                for (int slot : section.getIntegerList(items + ".Slots")) {
                    set.add(new SignInGUIColumn(other, slot, items));
                }
            }
            if (section.get(items + ".Slot") != null) {
                set.add(new SignInGUIColumn(other, section.getInt(items + ".Slot"), items));
            }
        }
        return set;
    }
    
    public static int getNextPageMonth(int month) {
        if (month == 12) {
            return 1;
        } else {
            return month + 1;
        }
    }
    
    public static int getNextPageYear(int month, int year) {
        if (month != 12) {
            return year;
        }
        return year + 1;
    }
    
    public static int getPreviousPageMonth(int month) {
        if (month == 1) {
            return 12;
        } else {
            return month - 1;
        }
    }
    
    public static int getPreviousPageYear(int month, int year) {
        if (month != 1) {
            return year;
        }
        return year - 1;
    }
    
    private static Map<String, String> getPlaceholdersOfItemLore(
            int day,
            String continuous,
            String queue,
            String totalNumber,
            String cards,
            int nextPageMonth,
            int nextPageYear,
            int previousPageMonth,
            int previousPageYear,
            SignInDate historicalDate) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        if (historicalDate != null) placeholders.put("{date}", historicalDate.getName(ConfigurationUtil.getConfig(ConfigurationType.GUI_SETTINGS).getString(MessageUtil.getLanguage() + ".SignIn-GUI-Settings.Date-Format")));
        placeholders.put("{day}", String.valueOf(day + 1));
        placeholders.put("{continuous}", continuous);
        placeholders.put("{queue}", queue);
        placeholders.put("{total-number}", totalNumber);
        placeholders.put("{cards}", cards);
        placeholders.put("{nextPageMonth}", String.valueOf(nextPageMonth));
        placeholders.put("{nextPageYear}", String.valueOf(nextPageYear));
        placeholders.put("{previousPageMonth}", String.valueOf(previousPageMonth));
        placeholders.put("{previousPageYear}", String.valueOf(previousPageYear));
        return placeholders;
    }
    
    private static String replace(Player player, String text, String target, String replacement) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put(target, replacement);
        return MessageUtil.replacePlaceholders(player, text, placeholders);
    }
    
    private static void setEnchantments(String configPath, ItemMeta im) {
        for (String name : ConfigurationUtil.getConfig(ConfigurationType.GUI_SETTINGS).getStringList(configPath)) {
            try {
                String[] data = name.split(":");
                boolean invalid = true;
                for (Enchantment enchant : Enchantment.values()) {
                    if (enchant.getName().equalsIgnoreCase(data[0])) {
                        try {
                            im.addEnchant(enchant, Integer.parseInt(data[1]), true);
                            invalid = false;
                            break;
                        } catch (Exception ex) {
                            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                            placeholders.put("{path}", configPath + "." + name);
                            SignInPluginProperties.sendOperationMessage("InvalidEnchantmentSetting", placeholders);
                        }
                    }
                }
                if (invalid) {
                    Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                    placeholders.put("{enchantment}", data[0]);
                    placeholders.put("{path}", configPath + "." + name);
                    SignInPluginProperties.sendOperationMessage("InvalidEnchantment", placeholders);
                }
            } catch (Exception ex) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{path}", configPath + "." + name);
                SignInPluginProperties.sendOperationMessage("InvalidEnchantmentSetting", placeholders);
            }
        }
    }
    
    private static void setCustomModelData(String configPath, ItemStack is) {
        String version = Bukkit.getBukkitVersion();
        if (version.startsWith("1.7") ||
            version.startsWith("1.8") ||
            version.startsWith("1.9") ||
            version.startsWith("1.10") ||
            version.startsWith("1.11") ||
            version.startsWith("1.12") || 
            version.startsWith("1.13")) return;
        ItemMeta im = is.getItemMeta();
        if (im == null) return;
        String name = ConfigurationUtil.getConfig(ConfigurationType.GUI_SETTINGS).getString(configPath);
        if (name == null) return;
        try {
            im.setCustomModelData(Integer.valueOf(name));
        } catch (Exception ex) {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{data}", name);
            placeholders.put("{path}", configPath + "." + name);
            SignInPluginProperties.sendOperationMessage("InvalidCustomModelData", placeholders);
        }
        is.setItemMeta(im);
    }
    
    private static void setHeadTextures(Player player, String configPath, ItemStack is) {
        String version = Bukkit.getBukkitVersion();
        if (version.startsWith("1.7")) return;
        ItemMeta im = is.getItemMeta();
        String textures = MessageUtil.toPlaceholderAPIResult(ConfigurationUtil.getConfig(ConfigurationType.GUI_SETTINGS).getString(configPath), player);
        if (im == null || textures == null) return;
        if (im instanceof SkullMeta) {
            is.setItemMeta(SkullsUtil.setSkullBase64(im, textures));
        }
    }
}
