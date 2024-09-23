package com.nuhlowl.spells.arcane;

import com.nuhlowl.spells.ShotSpellEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public abstract class ShotSpellEntityRenderer<T extends ShotSpellEntity> extends EntityRenderer<T> {
    private static final float SOME_CONSTANT = MathHelper.square(3.5F);
    private final ArcaneShotEntityModel model;

    public ShotSpellEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new ArcaneShotEntityModel();
    }

    public void render(
            T arcaneShotEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
    ) {
        if (arcaneShotEntity.age >= 2 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(arcaneShotEntity) < (double) SOME_CONSTANT)) {
            float h = (float) arcaneShotEntity.age + g;
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(arcaneShotEntity)));
            this.model.setAngles(arcaneShotEntity, 0.0F, 0.0F, h, 0.0F, 0.0F);
            this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, arcaneShotEntity.getColor());
            super.render(arcaneShotEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

    protected float getXOffset(float tickDelta) {
        return tickDelta * 0.03F;
    }

    @Override
    public Identifier getTexture(ShotSpellEntity entity) {
        return entity.getTexture();
    }
}
