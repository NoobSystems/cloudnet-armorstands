package io.github.noobsystems.cloudnetmorstands.data;

import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.modules.bridge.BridgeDocProperties;
import eu.cloudnetservice.modules.bridge.BridgeServiceHelper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class CloudNetUtils {

    private final CloudServiceProvider cloudServiceProvider;

    @Inject
    public CloudNetUtils(CloudServiceProvider cloudServiceProvider) {
        this.cloudServiceProvider = cloudServiceProvider;
    }

    public int getOnlinePlayersFromTask(String taskName) {
        return this.cloudServiceProvider.servicesByTask(taskName).stream()
                .map(service -> service.propertyHolder().readProperty(BridgeDocProperties.ONLINE_COUNT))
                .reduce(0, Integer::sum);
    }

    public int getNonIngamePlayersFromTask(String taskName) {
        return this.cloudServiceProvider.servicesByTask(taskName).stream()
                .filter(service -> !BridgeServiceHelper.inGameService(service))
                .map(service -> service.propertyHolder().readProperty(BridgeDocProperties.ONLINE_COUNT))
                .reduce(0, Integer::sum);
    }

}
