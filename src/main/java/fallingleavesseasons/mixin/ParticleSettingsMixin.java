package fallingleavesseasons.mixin;

import fallingleavesseasons.SeasonState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Falling Leaves Plus erlaubt pro Blattart einen eigenen Wert (z. B. Kirschblueten), der die globale
 * Einstellung ueberschreibt. Dieser Wert wird hier mit dem Jahreszeiten-Faktor multipliziert.
 * Blattarten ohne eigenen Wert nutzen die globale Einstellung, die der Controller direkt setzt.
 */
@Pseudo
@Mixin(targets = "fuzs.fallingleavesplus.client.particle.settings.ParticleSettings", remap = false)
public abstract class ParticleSettingsMixin {

    @Shadow(remap = false)
    public abstract Optional<Float> leafParticleChance();

    @Inject(method = "getLeafParticleChance", at = @At("RETURN"), cancellable = true, remap = false)
    private void fallingleavesseasons$applySeasonFactor(CallbackInfoReturnable<Float> cir) {
        if (this.leafParticleChance().isPresent()) {
            Float chance = cir.getReturnValue();
            if (chance != null) {
                cir.setReturnValue((float) (chance * SeasonState.blockFactor));
            }
        }
    }
}
