package com.iafenvoy.iceandfire.entity.util;

/**
 * Flight stamina shared by the flying mounts that can be limited by it (amphithere, hippogryph, dragon).
 * <p>
 * The stamina system is opt-in per mount type: {@link #isFlightStaminaEnabled()} returns the config flag of the
 * entity's own config section and is {@code false} by default. While it is disabled the mount must keep its original
 * flight behaviour, no stamina is drained and {@link com.iafenvoy.iceandfire.client.FlightStaminaOverlay} stays hidden.
 */
public interface IFlightStamina {
    float getFlightStamina();

    void setFlightStamina(float stamina);

    float getMaxFlightStamina();

    boolean isGliding();

    void setGliding(boolean gliding);

    boolean isFlightStaminaEnabled();
}
