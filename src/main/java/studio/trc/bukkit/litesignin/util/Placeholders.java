package studio.trc.bukkit.litesignin.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.jetbrains.annotations.NotNull;
import studio.trc.bukkit.litesignin.Main;
import studio.trc.bukkit.litesignin.api.Storage;
import studio.trc.bukkit.litesignin.config.ConfigurationType;
import studio.trc.bukkit.litesignin.config.ConfigurationUtil;
import studio.trc.bukkit.litesignin.queue.SignInQueue;

import org.bukkit.entity.Player;

public class Placeholders
    extends PlaceholderExpansion
{
    private final Random random = new Random();
    private final Main plugin;
    
    private static Placeholders instance;
    private static final Map<UUID, Map<String, String>> cacheOfPlayers = new HashMap<>();
    
    private static long cacheOfUpdateTime = System.currentTimeMillis();

    public Placeholders(Main plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static void init(Main plugin) {
        if (instance == null) {
            instance = new Placeholders(plugin);
        }
        if (!instance.isRegistered()) {
            instance.register();
        }
    }

    public static void checkUpdate() {
        if (cacheOfUpdateTime < System.currentTimeMillis()) {
            cacheOfPlayers.clear();
            cacheOfUpdateTime = System.currentTimeMillis() + (long) (ConfigurationUtil.getConfig(ConfigurationType.CONFIG).getDouble("PlaceholderAPI.Cache-Update-Delay") * 1000);
        }
    }
  
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        checkUpdate();
        String lowerIdentifier = identifier.toLowerCase();
        boolean cache = ConfigurationUtil.getConfig(ConfigurationType.CONFIG).getStringList("PlaceholderAPI.Exceptions").stream().noneMatch(placeholder -> placeholder.equals(lowerIdentifier));
        if (identifier.toLowerCase().startsWith("random")) {
            String[] randomValue = identifier.split("_");
            if (randomValue.length < 3) {
                return "0";
            }
            try {
                int number1 = Integer.parseInt(randomValue[1]);
                int number2 = Integer.parseInt(randomValue[2]);
                if (number1 == number2) {
                    return String.valueOf(number1);
                } else if (number1 > number2) {
                    return String.valueOf(this.random.nextInt(number1 - number2 + 1) + number2);
                } else /*if (number2 > number1)*/ {
                    return String.valueOf(this.random.nextInt(number2 - number1 + 1) + number1);
                }
            } catch (NumberFormatException ex) {
                return "0";
            }
        }
        if (player != null) {
            UUID uuid = player.getUniqueId();
            Map<String, String> map = cacheOfPlayers.computeIfAbsent(uuid, k -> new HashMap<>());
            String value = map.get(identifier);
            if (value != null) return value;

            String result = "";
            Storage data = Storage.getPlayer(player);
            Map<String, String> cacheMap = cacheOfPlayers.get(uuid);
            if (identifier.equalsIgnoreCase("signed-in")) {
                result = String.valueOf(data.alreadySignIn(SignInDate.getInstance(new Date())));
            } else if (identifier.equalsIgnoreCase("queue")) {
                result = String.valueOf(SignInQueue.getInstance().getRank(player.getUniqueId()));
            } else if (identifier.equalsIgnoreCase("cards_amount")) {
                result = String.valueOf(data.getRetroactiveCard());
            } else if (identifier.equalsIgnoreCase("group")) {
                result = data.getGroup().getGroupName();
            } else if (identifier.equalsIgnoreCase("statistics")) {
                result = String.valueOf(data.getCumulativeNumber());
            } else if (identifier.startsWith("statistics_of_month")) {
                if (identifier.equalsIgnoreCase("statistics_of_month")) {
                    SignInDate date = SignInDate.getInstance(new Date());
                    result = String.valueOf(data.getCumulativeNumberOfMonth(date.getYear(), date.getMonth()));
                } else {
                    String[] time = identifier.split("_");
                    try {
                        int year = Integer.parseInt(time[3]);
                        int month = Integer.parseInt(time[4]);
                        result = String.valueOf(data.getCumulativeNumberOfMonth(year, month));
                    } catch (Throwable ignored) {}
                }
            } else if (identifier.equalsIgnoreCase("continuous")) {
                result = String.valueOf(data.getContinuousSignIn());
            } else if (identifier.equalsIgnoreCase("continuous_of_month")) {
                result = String.valueOf(data.getContinuousSignInOfMonth());
            } else if (identifier.equalsIgnoreCase("last_year")) {
                result = String.valueOf(data.getYear());
            } else if (identifier.equalsIgnoreCase("last_month")) {
                result = String.valueOf(data.getMonth());
            } else if (identifier.equalsIgnoreCase("last_day")) {
                result = String.valueOf(data.getDay());
            } else if (identifier.equalsIgnoreCase("last_hour")) {
                result = String.valueOf(data.getHour());
            } else if (identifier.equalsIgnoreCase("last_minute")) {
                result = String.valueOf(data.getMinute());
            } else if (identifier.equalsIgnoreCase("last_second")) {
                result = String.valueOf(data.getSecond());
            }
            if (cache) {
                cacheMap.put(identifier, result);
            }
            return result;
        }
        return null;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return plugin.getDescription().getName().toLowerCase();
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
}
