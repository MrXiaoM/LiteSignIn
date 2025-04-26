package studio.trc.bukkit.litesignin.event.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SignInGUICloseEvent
    extends Event
{
    public static HandlerList handlers = new HandlerList();
    
    private final Player player;
    
    public SignInGUICloseEvent(Player player) {
        this.player = player;
    }
    
    public Player getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
