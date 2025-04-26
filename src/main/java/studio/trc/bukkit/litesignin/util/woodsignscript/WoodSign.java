package studio.trc.bukkit.litesignin.util.woodsignscript;

import java.util.List;

import lombok.Getter;

import studio.trc.bukkit.litesignin.util.woodsignscript.WoodSignUtil.WoodSignLine;

@Getter
public class WoodSign
{
    private final String woodSignTitle;
    private final WoodSignLine woodSignText;
    private final List<String> woodSignCommand;
    private final String permission;
    
    public WoodSign(String woodSignTitle, WoodSignLine woodSignText, List<String> woodSignCommand, String permission) {
        this.woodSignCommand = woodSignCommand;
        this.woodSignText = woodSignText;
        this.woodSignTitle = woodSignTitle;
        this.permission = permission;
    }
}
