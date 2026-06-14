package com.buuz135.industrialforegoingsouls.config;

import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

import java.util.ArrayList;
import java.util.List;

@ConfigFile(value = "industrialforegoing-souls-client")
public class IFSoulsClient {

    @ConfigVal.InRangeDouble(max = 1, min = 0)
    @ConfigVal(comment = "Particles spawned by pipes, where 0 is 0% and 1 is 100%")
    public static double SOUL_PIPES_PARTICLES = 0.1;


    @ConfigVal(comment = "Register a list of blocks that will not be affected by Soul Surge.")
    public static List<String> SOUL_SURGE_IGNORE_BLOCKS = List.of("minecraft:bedrock");
}
