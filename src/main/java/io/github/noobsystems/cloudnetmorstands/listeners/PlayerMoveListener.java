package io.github.noobsystems.cloudnetmorstands.listeners;

import io.github.noobsystems.cloudnetmorstands.CloudNetArmorStands;
import io.github.noobsystems.cloudnetmorstands.config.ConfigManager;
import io.github.noobsystems.cloudnetmorstands.data.CloudNetArmorStand;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class PlayerMoveListener {

    private final ConfigManager configManager;
    private final Map<Player, ArmorStand> playersLookingAtArmorStands = new HashMap<>();

    @Inject
    public PlayerMoveListener(JavaPlugin plugin, ConfigManager configManager) {
        this.configManager = configManager;

        int actionBarDistance = this.configManager.getActionBarDistance();

        if (actionBarDistance == -1) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                Location location = player.getEyeLocation();
                RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(location.add(location.getDirection()), location.getDirection(), actionBarDistance, entity -> entity instanceof ArmorStand && CloudNetArmorStands.ARMOR_STANDS.stream().anyMatch(cloudNetArmorStand -> cloudNetArmorStand.getArmorStand().equals(entity)));

                if (rayTraceResult == null) {
                    if (this.playersLookingAtArmorStands.containsKey(player)) {
                        this.playersLookingAtArmorStands.remove(player);
                        player.sendActionBar(Component.empty());
                    }
                } else {
                    ArmorStand armorStand = (ArmorStand) rayTraceResult.getHitEntity();

                    this.sendActionBar(player, armorStand);
                }
            });
        }, 20, 5);
    }

    private void sendActionBar(Player player, ArmorStand entity) {
        CloudNetArmorStand cloudNetArmorStand = CloudNetArmorStands.ARMOR_STANDS.stream()
                .filter(armorStand -> armorStand.getArmorStand().equals(entity))
                .findFirst().orElse(null);

        if (cloudNetArmorStand != null) {
            player.sendActionBar(this.configManager.getActionBarMessage(cloudNetArmorStand));
            this.playersLookingAtArmorStands.put(player, entity);
        }
    }

}
