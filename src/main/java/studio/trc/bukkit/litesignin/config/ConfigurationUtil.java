package studio.trc.bukkit.litesignin.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litesignin.Main;
import studio.trc.bukkit.litesignin.util.SignInPluginProperties;
import studio.trc.bukkit.litesignin.util.MessageUtil;

public class ConfigurationUtil
{
    private final static Map<ConfigurationType, PreparedConfiguration> cache = new HashMap<>();
    
    public static PreparedConfiguration getConfig(ConfigurationType type) {
        if (cache.get(type) == null) {
            cache.put(type, new PreparedConfiguration(new YamlConfiguration(), type));
        }
        return cache.get(type);
    }
    
    public static FileConfiguration getFileConfiguration(ConfigurationType fileType) {
        return getConfig(fileType).getRawConfig();
    }
    
    public static void reloadConfig(ConfigurationType fileType) {
        saveResource(fileType);
        try (InputStream fis = Files.newInputStream(new File(Main.getInstance().getDataFolder(), fileType.getFileName()).toPath());
            InputStreamReader Config = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            getFileConfiguration(fileType).load(Config);
        } catch (IOException | InvalidConfigurationException ex) {
            File dataFolder = Main.getInstance().getDataFolder();
            File oldFile = new File(dataFolder, fileType.getFileName() + ".old");
            File file = new File(dataFolder, fileType.getFileName());
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{file}", fileType.getFileName());
            SignInPluginProperties.sendOperationMessage("ConfigurationLoadingError", placeholders);
            if (oldFile.exists()) {
                oldFile.delete();
            }
            file.renameTo(oldFile);
            saveResource(fileType);
            try (InputStream fis = Files.newInputStream(file.toPath());
                 InputStreamReader newConfig = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                getFileConfiguration(fileType).load(newConfig);
                SignInPluginProperties.sendOperationMessage("ConfigurationRepair", new HashMap<>());
            } catch (IOException | InvalidConfigurationException ex1) {
                Main.getInstance().getLogger().log(Level.WARNING, "保存默认配置 " + fileType.getFileName() + " 时出现异常", ex);
            }
        }
    }
    
    public static void reloadConfig() {
        for (ConfigurationType type : ConfigurationType.values()) {
            if (type.equals(ConfigurationType.WOOD_SIGN_SETTINGS)) {
                continue; //Because has a dedicated loading function
            }
            reloadConfig(type);
        }
    }
    
    public static void saveResource(ConfigurationType fileType) {
        File dataFolder = Main.getInstance().getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        try {
            File configFile = new File(dataFolder, fileType.getFileName());
            if (!configFile.exists()) {
                configFile.createNewFile();
                InputStream is;
                if (fileType.equals(ConfigurationType.GUI_SETTINGS) || fileType.equals(ConfigurationType.REWARD_SETTINGS)) {
                    String version = Bukkit.getBukkitVersion();
                    if (version.startsWith("1.7") || version.startsWith("1.8") || version.startsWith("1.9") || version.startsWith("1.10") || version.startsWith("1.11") || version.startsWith("1.12")) {
                        is = Main.getInstance().getResource("Languages/" + MessageUtil.Language.getLocaleLanguage().getFolderName() + "/" + fileType.getFileName().replace(".yml", "") + "-OLDVERSION.yml");
                    } else {
                        is = Main.getInstance().getResource("Languages/" + MessageUtil.Language.getLocaleLanguage().getFolderName() + "/" + fileType.getFileName().replace(".yml", "") + "-NEWVERSION.yml");
                    }
                } else {
                    is = Main.getInstance().getResource("Languages/" + MessageUtil.Language.getLocaleLanguage().getFolderName() + "/" + fileType.getFileName());
                }
                if (is != null) {
                    try (InputStream input = is;
                         OutputStream output = Files.newOutputStream(configFile.toPath())) {
                        int length;
                        byte[] buffer = new byte[16 * 1024];
                        while ((length = input.read(buffer)) != -1) {
                            output.write(buffer, 0, length);
                        }
                    }
                }
            }
        } catch (IOException ignored) {}
    }

    public static void saveConfig() {
        for (ConfigurationType type : ConfigurationType.values()) {
            if (type.equals(ConfigurationType.WOOD_SIGN_SETTINGS)) {
                continue; //Because has a dedicated saving function
            }
            saveConfig(type);
        }
    }
    
    public static void saveConfig(ConfigurationType fileType) {
        try {
            File file = new File(Main.getInstance().getDataFolder(), fileType.getFileName());
            cache.get(fileType).getRawConfig().save(file);
        } catch (IOException ex) {
            Main.getInstance().getLogger().log(Level.WARNING, "保存 " + fileType.getFileName() + " 配置时出现异常", ex);
        }
    }
}