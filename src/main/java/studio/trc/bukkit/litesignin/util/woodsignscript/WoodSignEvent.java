package studio.trc.bukkit.litesignin.util.woodsignscript;

import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import studio.trc.bukkit.litesignin.config.ConfigurationType;
import studio.trc.bukkit.litesignin.config.ConfigurationUtil;
import studio.trc.bukkit.litesignin.util.MessageUtil;

import studio.trc.bukkit.litesignin.util.PluginControl;

public class WoodSignEvent
    implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void click(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && block != null) {
            if (!PluginControl.enableSignScript() || !(block.getState() instanceof Sign) || event.useInteractedBlock().equals(Event.Result.DENY)) {
                return;
            }
            Player player = event.getPlayer();
            if (WoodSignUtil.getAllScriptedSign().get(block.getLocation()) != null) {
                WoodSignUtil.clickScript(player, WoodSignUtil.getAllScriptedSign().get(block.getLocation()));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void destroy(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!PluginControl.enableSignScript() || !(block.getState() instanceof Sign) || event.isCancelled()) {
            return;
        }
        if (WoodSignUtil.getAllScriptedSign().get(block.getLocation()) != null) {
            String name = WoodSignUtil.getAllScriptedSign().get(block.getLocation()).getWoodSignTitle();
            WoodSignUtil.removeWoodSignScript(block.getLocation());
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{sign}", name);
            MessageUtil.sendMessage(event.getPlayer(), ConfigurationUtil.getConfig(ConfigurationType.MESSAGES), "Wood-Sign.Successfully-Remove", placeholders);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void check(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (!PluginControl.enableSignScript() || !(block.getState() instanceof Sign) || event.isCancelled()) {
            return;
        }
        WoodSignUtil.removeWoodSignScript(block.getLocation());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void place(SignChangeEvent event) {
        if (!PluginControl.enableSignScript() || !(event.getBlock().getState() instanceof Sign)) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getBlock();
        WoodSignUtil.removeWoodSignScript(block.getLocation());
        for (WoodSign signs : WoodSignUtil.getWoodSignScripts()) {
            if (signs.getWoodSignTitle().equalsIgnoreCase(event.getLine(0))) {
                if (signs.getPermission() != null) {
                    if (player.hasPermission(signs.getPermission())) {
                        success(block, signs, event.getPlayer());
                    } else {
                        fail(signs, event.getPlayer());
                    }
                } else {
                    success(block, signs, event.getPlayer());
                }
                break;
            }
        }
    }

    private void success(Block block, WoodSign signs, Player player) {
        WoodSignUtil.createWoodSignScript(block, signs, true);
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{sign}", signs.getWoodSignTitle());
        MessageUtil.sendMessage(player, ConfigurationUtil.getConfig(ConfigurationType.MESSAGES), "Wood-Sign.Successfully-Create", placeholders);
    }

    private void fail(WoodSign signs, Player player) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{sign}", signs.getWoodSignTitle());
        placeholders.put("{permission}", signs.getPermission());
        MessageUtil.sendMessage(player, ConfigurationUtil.getConfig(ConfigurationType.MESSAGES), "Wood-Sign.Unable-To-Create", placeholders);
    }
}
