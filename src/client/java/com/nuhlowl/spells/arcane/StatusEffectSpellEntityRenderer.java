package com.nuhlowl.spells.arcane;

import com.nuhlowl.spells.status.StatusEffectSpellEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;

public class StatusEffectSpellEntityRenderer extends ShotSpellEntityRenderer<StatusEffectSpellEntity> {
    public StatusEffectSpellEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }
}
