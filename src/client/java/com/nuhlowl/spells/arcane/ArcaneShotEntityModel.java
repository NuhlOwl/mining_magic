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
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BONE, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        modelPartData2.addChild(
                "wind",
                ModelPartBuilder.create()
                        .uv(15, 20)
                        .cuboid(-4.0F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F, new Dilation(0.0F))
                        .uv(0, 9)
                        .cuboid(-3.0F, -2.0F, -3.0F, 6.0F, 4.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F)
        );
        modelPartData2.addChild(
                "wind_charge", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F)
        );

        this.modelData = TexturedModelData.of(modelData, 64, 32).createModel();
    }

    @Override
    public ModelPart getPart() {
        return modelData.getChild(EntityModelPartNames.BONE);
    }

    @Override
    public void setAngles(ArcaneShotEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        ModelPart bone = this.modelData.getChild(EntityModelPartNames.BONE);
        bone.getChild("windCharge").yaw = -animationProgress * 16.0F * (float) (Math.PI / 180.0);
        bone.getChild("wind").yaw = animationProgress * 16.0F * (float) (Math.PI / 180.0);
    }
}
