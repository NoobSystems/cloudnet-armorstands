package io.github.noobsystems.cloudnetmorstands.listeners;

import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.modules.bridge.BridgeDocProperties;
import eu.cloudnetservice.modules.bridge.BridgeServiceHelper;
import io.github.noobsystems.cloudnetmorstands.CloudNetArmorStands;
import io.github.noobsystems.cloudnetmorstands.data.CloudNetArmorStand;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class InteractAtEntityListener implements Listener {

    private final CloudServiceProvider cloudServiceProvider;

    @Inject
    public InteractAtEntityListener(CloudServiceProvider cloudServiceProvider) {
        this.cloudServiceProvider = cloudServiceProvider;
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();

        if (!(event.getRightClicked() instanceof ArmorStand)) {
            return;
        }

        CloudNetArmorStand cloudNetArmorStand = CloudNetArmorStands.ARMOR_STANDS.stream()
                .filter(armorStand -> armorStand.getArmorStand().equals(event.getRightClicked()))
                .findFirst().orElse(null);

        if (cloudNetArmorStand == null) {
            return;
        }

        event.setCancelled(true);

        Inventory inventory = Bukkit.createInventory(null, 9 * 3, cloudNetArmorStand.getInventoryName());
        AtomicInteger slot = new AtomicInteger(0);

        this.cloudServiceProvider.servicesByTask(cloudNetArmorStand.getTask()).stream()
                .filter(service -> !BridgeServiceHelper.inGameService(service))
                .filter(service -> !service.propertyHolder().empty())
                .forEach(service -> {
                    ItemStack itemStack = new ItemStack(cloudNetArmorStand.getMaterial());
                    itemStack.editMeta(itemMeta -> {
                        itemMeta.displayName(LegacyComponentSerializer.legacySection().deserialize(cloudNetArmorStand.getColor() + service.name()).decoration(TextDecoration.ITALIC, false));

                        itemMeta.lore(List.of(
                                Component.empty(),
                                Component.text("● ").color(NamedTextColor.DARK_GRAY)
                                        .append(Component.text(service.propertyHolder().readProperty(BridgeDocProperties.STATE)).color(NamedTextColor.GRAY))
                                        .append(Component.text(" ●").color(NamedTextColor.DARK_GRAY))
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("● ").color(NamedTextColor.DARK_GRAY)
                                        .append(Component.text(service.propertyHolder().readProperty(BridgeDocProperties.ONLINE_COUNT)).color(NamedTextColor.GRAY))
                                        .append(Component.text("/").color(NamedTextColor.DARK_GRAY))
                                        .append(Component.text(service.propertyHolder().getInt("Max-Players")).color(NamedTextColor.GRAY))
                                        .append(Component.text(" ●").color(NamedTextColor.DARK_GRAY))
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("● ").color(NamedTextColor.DARK_GRAY)
                                        .append(Component.text(service.propertyHolder().readProperty(BridgeDocProperties.MOTD)).color(NamedTextColor.GRAY))
                                        .append(Component.text(" ●").color(NamedTextColor.DARK_GRAY))
                                        .decoration(TextDecoration.ITALIC, false)
                        ));
                    });

                    inventory.setItem(slot.getAndIncrement(), itemStack);
                });

        player.openInventory(inventory);
    }

}
