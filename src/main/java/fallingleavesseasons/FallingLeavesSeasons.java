package fallingleavesseasons;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FallingLeavesSeasons implements ClientModInitializer {
    public static final String MOD_ID = "fallingleavesseasons";
    static final Logger LOGGER = LoggerFactory.getLogger("Falling Leaves Seasons");

    @Override
    public void onInitializeClient() {
        SeasonLeafController controller = new SeasonLeafController();
        ClientTickEvents.END_CLIENT_TICK.register(client -> controller.onTick(client.level));
    }
}
