package nullengine.client.rendering3d.queue;

public interface StandardRenderTypes {

    RenderType OPAQUE = RenderType.builder().name("Opaque").build();

    RenderType TRANSPARENT = RenderType.builder().name("Transparent").after("Opaque").build();

    RenderType TRANSLUCENT = RenderType.builder().name("Translucent").after("Transparent").build();

    RenderType LIQUID_OPAQUE = RenderType.builder().name("LiquidOpaque").before("Transparent", "Translucent").build();

    RenderType LIQUID_TRANSLUCENT = RenderType.builder().name("LiquidTranslucent").after("Opaque", "Transparent").build();
}
