package io.github.noobsystems.cloudnetmorstands.listeners;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.events.service.CloudServiceUpdateEvent;
import io.github.noobsystems.cloudnetmorstands.CloudNetArmorStands;
import io.github.noobsystems.cloudnetmorstands.data.CloudNetUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;

@Singleton
public class CloudServiceListener {

    private final JavaPlugin plugin;
    private final CloudNetUtils cloudNetUtils;

    @Inject
    public CloudServiceListener(JavaPlugin plugin, CloudNetUtils cloudNetUtils) {
        this.plugin = plugin;
        this.cloudNetUtils = cloudNetUtils;
    }

    @EventListener
    public void onCloudServiceUpdate(CloudServiceUpdateEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            CloudNetArmorStands.ARMOR_STANDS.stream()
                    .filter(cloudNetArmorStand -> cloudNetArmorStand.getTask().equals(event.serviceInfo().serviceId().taskName()))
                    .forEach(cloudNetArmorStand ->  {
                        if (cloudNetArmorStand.getArmorStand().getPassengers().isEmpty()) {
                            return;
                        }

                        TextDisplay textDisplay = (TextDisplay) cloudNetArmorStand.getArmorStand().getPassengers().get(0);
                        textDisplay.text(cloudNetArmorStand.getComponentTitle(cloudNetUtils));
                    });
        }, 0);
    }

}
