package io.github.noobsystems.cloudnetmorstands.listeners;

import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.registry.injection.Service;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import io.github.noobsystems.cloudnetmorstands.CloudNetArmorStands;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

@Singleton
public class InventoryClickListener implements Listener {

    private final CloudServiceProvider cloudServiceProvider;
    private final PlayerManager playerManager;
    private final JavaPlugin plugin;

    @Inject
    public InventoryClickListener(JavaPlugin plugin, CloudServiceProvider cloudServiceProvider, @Service PlayerManager playerManager) {
        this.cloudServiceProvider = cloudServiceProvider;
        this.playerManager = playerManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        if (event.getCurrentItem() == null) {
            return;
        }
        if (!event.getCurrentItem().hasItemMeta()) {
            return;
        }
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) {
            return;
        }

        if (CloudNetArmorStands.ARMOR_STANDS.stream()
                .map(cloudNetArmorStand -> PlainTextComponentSerializer.plainText().serialize(cloudNetArmorStand.getInventoryName()))
                .anyMatch(title::contains)
        ) {
            event.setCancelled(true);
            String clickedTitle = PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().getItemMeta().displayName());

            this.cloudServiceProvider.services().stream()
                    .map(ServiceInfoSnapshot::name)
                    .filter(clickedTitle::contains)
                    .findFirst()
                    .ifPresent(serviceName -> this.playerManager.playerExecutor(event.getWhoClicked().getUniqueId()).connect(serviceName));
        }
    }

}
