package unknowndomaingame.foundation;

import nullengine.Platform;
import nullengine.block.Block;
import nullengine.block.component.ActivateBehavior;
import nullengine.block.component.ClickBehavior;
import nullengine.client.asset.AssetPath;
import nullengine.client.block.AirClientBlock;
import nullengine.client.block.ClientBlock;
import nullengine.client.block.DefaultClientBlock;
import nullengine.client.event.rendering.EntityRendererRegistrationEvent;
import nullengine.client.gui.Scene;
import nullengine.client.input.controller.MotionType;
import nullengine.client.input.keybinding.ActionMode;
import nullengine.client.input.keybinding.Key;
import nullengine.client.input.keybinding.KeyBinding;
import nullengine.client.rendering.camera.Camera;
import nullengine.client.rendering.entity.EntityItemRenderer;
import nullengine.client.rendering.item.ItemBlockRenderer;
import nullengine.client.rendering.item.ItemRenderer;
import nullengine.entity.Entity;
import nullengine.entity.component.TwoHands;
import nullengine.entity.item.ItemEntity;
import nullengine.event.Listener;
import nullengine.event.engine.EngineEvent;
import nullengine.event.registry.RegistrationEvent;
import nullengine.event.registry.RegistryConstructionEvent;
import nullengine.event.world.block.BlockActivateEvent;
import nullengine.event.world.block.BlockClickEvent;
import nullengine.item.BlockItem;
import nullengine.item.Item;
import nullengine.item.ItemStack;
import nullengine.item.component.HitBlockBehavior;
import nullengine.item.component.UseBlockBehavior;
import nullengine.player.Player;
import nullengine.registry.Registry;
import nullengine.registry.RegistryManager;
import nullengine.registry.game.BlockRegistry;
import nullengine.registry.impl.IdAutoIncreaseRegistry;
import nullengine.world.collision.RayTraceBlockHit;
import unknowndomaingame.foundation.client.gui.game.GUIGameCreation;
import unknowndomaingame.foundation.client.gui.game.GuiChat;
import unknowndomaingame.foundation.client.gui.hud.HUDGameDebug;
import unknowndomaingame.foundation.init.Blocks;
import unknowndomaingame.foundation.init.Items;
import unknowndomaingame.foundation.registry.SimpleBlockRegistry;
import unknowndomaingame.foundation.registry.SimpleItemRegistry;

public final class DefaultGameMode {

    @Listener
    public static void constructionRegistry(RegistryConstructionEvent e) {
        // TODO: move to common.
        e.register(new SimpleBlockRegistry());
        e.register(new SimpleItemRegistry());

        e.register(new IdAutoIncreaseRegistry<>(KeyBinding.class));
        e.register(new IdAutoIncreaseRegistry<>(ClientBlock.class));
//        e.registerPostTask(Block.class, Item.class, (block, registry)->registry.register(new BlockItem(block)));
    }

    @Listener
    public static void registerStage(RegistrationEvent.Start e) {
        RegistryManager registryManager = e.getRegistryManager();
        registerBlocks((BlockRegistry) registryManager.getRegistry(Block.class));
        registerItems(registryManager.getRegistry(Item.class));
        registerKeyBindings(registryManager.getRegistry(KeyBinding.class));
        registerClientBlock(registryManager.getRegistry(ClientBlock.class));
    }

    private static void registerBlocks(BlockRegistry registry) {
        registry.register(Blocks.AIR);
        registry.register(Blocks.GRASS);
        registry.register(Blocks.DIRT);
        registry.setAirBlock(Blocks.AIR);
    }

    private static void registerItems(Registry<Item> registry) {
        registry.register(new BlockItem(Blocks.AIR));
        registry.register(Items.GRASS);
        registry.register(Items.DIRT);

        Items.GRASS.setComponent(ItemRenderer.class, new ItemBlockRenderer());
        Items.DIRT.setComponent(ItemRenderer.class, new ItemBlockRenderer());
    }

    private static void registerClientBlock(Registry<ClientBlock> registry) {
        AssetPath blockModelPath = AssetPath.of("unknowndomain", "models", "block");
        registry.register(new AirClientBlock(Blocks.AIR));
        registry.register(new DefaultClientBlock(Blocks.GRASS).setRenderer(blockModelPath.resolve("grass.json")));
        registry.register(new DefaultClientBlock(Blocks.DIRT).setRenderer(blockModelPath.resolve("dirt.json")));
    }

    private static void registerKeyBindings(Registry<KeyBinding> registry) {

        // TODO: When separating common and client, only register on client side
        // TODO: almost everything is hardcoded... Fix when GameContext and
        registry.register(
                KeyBinding.create("player.move.forward", Key.KEY_W, (c) -> c.getEntityController().handleMotion(MotionType.FORWARD, true), ActionMode.PRESS)
                        .endAction((c, i) -> c.getEntityController().handleMotion(MotionType.FORWARD, false)));
        registry.register(
                KeyBinding.create("player.move.backward", Key.KEY_S, (c) -> c.getEntityController().handleMotion(MotionType.BACKWARD, true), ActionMode.PRESS)
                        .endAction((c, i) -> c.getEntityController().handleMotion(MotionType.BACKWARD, false)));
        registry.register(KeyBinding.create("player.move.left", Key.KEY_A, (c) -> c.getEntityController().handleMotion(MotionType.LEFT, true), ActionMode.PRESS)
                .endAction((c, i) -> c.getEntityController().handleMotion(MotionType.LEFT, false)));
        registry.register(KeyBinding.create("player.move.right", Key.KEY_D, (c) -> c.getEntityController().handleMotion(MotionType.RIGHT, true), ActionMode.PRESS)
                .endAction((c, i) -> c.getEntityController().handleMotion(MotionType.RIGHT, false)));
        registry.register(KeyBinding.create("player.move.jump", Key.KEY_SPACE, (c) -> c.getEntityController().handleMotion(MotionType.UP, true), ActionMode.PRESS)
                .endAction((c, i) -> c.getEntityController().handleMotion(MotionType.UP, false)));
        registry.register(
                KeyBinding.create("player.move.sneak", Key.KEY_LEFT_SHIFT, (c) -> c.getEntityController().handleMotion(MotionType.DOWN, true), ActionMode.PRESS)
                        .endAction((c, i) -> c.getEntityController().handleMotion(MotionType.DOWN, false)));
        registry.register(KeyBinding.create("player.mouse.left", Key.MOUSE_BUTTON_LEFT, (game) -> {
            Player player = game.getPlayer();
            Camera camera = game.getEngine().getRenderContext().getCamera();
            Entity controllingEntity = player.getControlledEntity();
            RayTraceBlockHit blockHit = player.getWorld().getCollisionManager().raycastBlock(camera.getPosition(), camera.getFrontVector(), 10);
            if (blockHit.isSuccess()) {
                controllingEntity.getComponent(TwoHands.class)
                        .ifPresent(twoHands -> twoHands.getMainHand()
                                .ifNonEmpty(itemStack -> {
                                    blockHit.getBlock().getComponent(ClickBehavior.class).ifPresent(clickBehavior -> clickBehavior.onClicked(player.getWorld(), blockHit.getPos(), blockHit.getBlock()));
                                    game.getEngine().getEventBus().post(new BlockClickEvent(player.getWorld(), controllingEntity, blockHit.getPos(), blockHit.getBlock()));
                                    itemStack.getItem()
                                            .getComponent(HitBlockBehavior.class)
                                            .ifPresent(hitBlockBehavior -> hitBlockBehavior.onHit(player, itemStack, blockHit));
                                }));
            }

        }, ActionMode.PRESS));
        registry.register(KeyBinding.create("player.mouse.right", Key.MOUSE_BUTTON_RIGHT, (game) -> {
            Player player = game.getPlayer();
            Camera camera = game.getEngine().getRenderContext().getCamera();
            Entity entity = player.getControlledEntity();
            RayTraceBlockHit hit = player.getWorld().getCollisionManager().raycastBlock(camera.getPosition(), camera.getFrontVector(), 10);
            if (hit.isSuccess()) {
                entity.getComponent(TwoHands.class)
                        .ifPresent(twoHands -> twoHands.getMainHand()
                                .ifNonEmpty(itemStack -> {
                                    hit.getBlock().getComponent(ActivateBehavior.class).ifPresent(activateBehavior -> activateBehavior.onActivated(player.getWorld(), entity, hit.getPos(), hit.getBlock()));
                                    game.getEngine().getEventBus().post(new BlockActivateEvent(player.getWorld(), entity, hit.getPos(), hit.getBlock()));
                                    itemStack.getItem()
                                            .getComponent(UseBlockBehavior.class)
                                            .ifPresent(useBlockBehavior -> useBlockBehavior.onUseBlockStart(player, itemStack, hit));
                                }));
            }
        }, ActionMode.PRESS));
        registry.register(KeyBinding.create("player.mouse.middle", Key.MOUSE_BUTTON_3, (game) -> {
            Player player = game.getPlayer();
            Camera camera = game.getEngine().getRenderContext().getCamera();
            Entity entity = player.getControlledEntity();
            player.getWorld().getCollisionManager().raycastBlock(camera.getPosition(), camera.getFrontVector(), 10).ifSuccess(hit ->
                    // TODO: Dont create BlockItem
                    entity.getComponent(TwoHands.class)
                            .ifPresent(twoHands -> twoHands.setMainHand(new ItemStack(new BlockItem(hit.getBlock()))))
            );
        }, ActionMode.PRESS));
        registry.register(KeyBinding.create("game.chat", Key.KEY_T, (game) -> {
            Scene scene = new Scene(new GuiChat(game));
            game.getEngine().getRenderContext().getGuiManager().showScreen(scene);
        }, ActionMode.PRESS));

        var renderContext = Platform.getEngineClient().getRenderContext();
        var guiManager = renderContext.getGuiManager();
        var hudGameDebug = new HUDGameDebug();
        renderContext.getScheduler().runTaskEveryFrame(() -> hudGameDebug.update(renderContext));
        registry.register(KeyBinding.create("debug.switch", Key.KEY_F3, gameClient -> guiManager.showHud("debugGame", new Scene(hudGameDebug))
                , ActionMode.SWITCH).endAction((gameClient, integer) -> guiManager.hideHud("debugGame")));
    }

    @Listener
    public static void engineInit(EngineEvent.Ready event) {
        var renderContext = Platform.getEngineClient().getRenderContext();
        var guiManager = renderContext.getGuiManager();

        var scene = new Scene(new GUIGameCreation());
        guiManager.showScreen(scene);
    }

    @Listener
    public static void registerEntityRenderer(EntityRendererRegistrationEvent event) {
        event.register(ItemEntity.class, new EntityItemRenderer());
    }
}
