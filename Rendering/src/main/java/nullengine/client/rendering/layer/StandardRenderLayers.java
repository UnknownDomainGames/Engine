package nullengine.client.rendering.layer;

public interface StandardRenderLayers {

    RenderLayer OPAQUE = RenderLayer.builder().name("Opaque").build();

    RenderLayer TRANSPARENT = RenderLayer.builder().name("Transparent").after("Opaque").build();

    RenderLayer TRANSLUCENT = RenderLayer.builder().name("Translucent").after("Transparent").build();

    RenderLayer LIQUID_OPAQUE = RenderLayer.builder().name("LiquidOpaque").before("Transparent", "Translucent").build();

    RenderLayer LIQUID_TRANSLUCENT = RenderLayer.builder().name("LiquidTranslucent").after("Opaque", "Transparent").build();
}
