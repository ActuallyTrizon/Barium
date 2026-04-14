package com.trizon.barium;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BariumMod implements ModInitializer {
    public static final String MOD_ID = "barium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Barium - Lightweight Performance Optimization Mod");
        LOGGER.info("Credit: Trizon");
        LOGGER.info("Target Versions: Fabric 1.19.1 and 1.21");
        LOGGER.info("Loaded optimization modules:");
        LOGGER.info("  - AI Brain Throttling");
        LOGGER.info("  - Fast Leaf Decay");
        LOGGER.info("  - RAM Optimization");
        LOGGER.info("  - Math Optimization");
    }
}