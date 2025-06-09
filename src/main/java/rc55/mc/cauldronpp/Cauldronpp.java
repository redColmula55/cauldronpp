package rc55.mc.cauldronpp;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rc55.mc.cauldronpp.block.CauldronppBlocks;
import rc55.mc.cauldronpp.blockEntity.CauldronppBlockEntityTypes;
import rc55.mc.cauldronpp.item.CauldronppItems;

public class Cauldronpp implements ModInitializer {
	//mod id
	public static final String MODID = "cauldronpp";
	//日志
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		CauldronppItems.regItem();
		CauldronppBlocks.regBlock();
		CauldronppBlockEntityTypes.regBlockEntity();
		LOGGER.info("Cauldron++ loaded.");
	}
}