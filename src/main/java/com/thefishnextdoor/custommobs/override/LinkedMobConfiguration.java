package com.thefishnextdoor.custommobs.override;

import com.thefishnextdoor.custommobs.configuration.MobConfiguration;

public class LinkedMobConfiguration {

    private final MobConfiguration entityConfiguration;

    private final int weight;

    public LinkedMobConfiguration(MobConfiguration entityConfiguration, int weight) {
        this.entityConfiguration = entityConfiguration;
        this.weight = weight;
    }

    public MobConfiguration unWrap() {
        return entityConfiguration;
    }

    public int getWeight() {
        return weight;
    }
}
