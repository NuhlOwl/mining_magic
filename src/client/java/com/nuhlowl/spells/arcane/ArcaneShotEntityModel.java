package com.nuhlowl.spells.arcane;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

public class ArcaneShotEntityModel extends SinglePartEntityModel<ArcaneShotEntity> {
    private final ModelPart modelData;

    public ArcaneShotEntityModel() {
        super(RenderLayer::getEntityTranslucent);
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData bone = root.addChild(EntityModelPartNames.BONE,
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F)
        );
        bone.addChild(
                "inner",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-1.0F, -1.0F, -1.0F,
                                2.0F, 2.0F, 2.0F,
                                new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F)
        );
        bone.addChild(
                "middle", ModelPartBuilder.create()
                        .uv(0, 4)
                        .cuboid(-2.0F, -2.0F, -2.0F,
                                4.0F, 4.0F, 4.0F,
                                new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F)
        );
        bone.addChild(
                "outer", ModelPartBuilder.create()
                        .uv(0, 12)
                        .cuboid(-4.0F, -4.0F, -4.0F,
                                8.0F, 8.0F, 8.0F,
                                new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F)
        );

        this.modelData = TexturedModelData.of(modelData, 32, 32).createModel();
    }

    @Override
    public ModelPart getPart() {
        return modelData.getChild(EntityModelPartNames.BONE);
    }

    @Override
    public void setAngles(ArcaneShotEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        ModelPart bone = this.modelData.getChild(EntityModelPartNames.BONE);
        bone.getChild("inner").yaw = -animationProgress * 16.0F * (float) (Math.PI / 180.0);
        bone.getChild("middle").yaw = animationProgress * 16.0F * (float) (Math.PI / 180.0);
        bone.getChild("outer").yaw = -animationProgress * 16.0F * (float) (Math.PI / 180.0);
    }
}
