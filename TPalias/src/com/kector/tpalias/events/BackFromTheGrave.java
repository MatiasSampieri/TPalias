package com.kector.tpalias.events;

import com.kector.tpalias.otr.SavesManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.IOException;

public class BackFromTheGrave implements Listener {

    private SavesManager saveman;

    public BackFromTheGrave(SavesManager saveman) {
        this.saveman = saveman;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Entity entity = event.getEntity();
        Player player = event.getEntity().getPlayer();

        assert player != null;
        saveman.saveQuick(player);
    }
}
