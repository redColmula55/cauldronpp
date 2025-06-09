package rc55.mc.cauldronpp;

import net.fabricmc.api.ClientModInitializer;
import rc55.mc.cauldronpp.client.render.CauldronppRenderer;

public class CauldronppClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CauldronppRenderer.regRenderer();
        Cauldronpp.LOGGER.info("Cauldron++ loaded client side.");
    }
}
