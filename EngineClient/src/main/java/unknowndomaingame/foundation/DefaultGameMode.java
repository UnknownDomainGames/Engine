package unknowndomaingame.foundation;

import nullengine.Platform;
import nullengine.block.Block;
import nullengine.block.component.ActivateBehavior;
import nullengine.block.component.ClickBehavior;
import nullengine.client.asset.AssetPath;
import nullengine.client.block.AirBlockRenderer;
import nullengine.client.event.rendering.EntityRendererRegistrationEvent;
import nullengine.client.game.GameClient;
import nullengine.client.gui.Scene;
import nullengine.client.input.controller.MotionType;
import nullengine.client.input.keybinding.ActionMode;
import nullengine.client.input.keybinding.Key;
import nullengine.client.input.keybinding.KeyBinding;
import nullengine.client.rendering.block.BlockRenderer;
import nullengine.client.rendering.block.DefaultBlockRenderer;
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
import unknowndomaingame.foundation.client.gui.game.GuiItemList;
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
    }

    @Listener
    public static void registerStage(RegistrationEvent.Start e) {
        RegistryManager registryManager = e.getRegistryManager();
        registerBlocks((BlockRegistry) registryManager.getRegistry(Block.class));
        registerItems(registryManager.getRegistry(Item.class));
        registerKeyBindings(registryManager.getRegistry(KeyBinding.class));
    }

    private static void registerBlocks(BlockRegistry registry) {
        registry.register(Blocks.AIR);
        Blocks.AIR.addComponent(BlockRenderer.class, new AirBlockRenderer());
        registry.setAirBlock(Blocks.AIR);

        AssetPath blockModelPath = AssetPath.of("unknowndomain", "models", "block");
        Blocks.GRASS.addComponent(BlockRenderer.class, new DefaultBlockRenderer().setModelPath(blockModelPath.resolve("grass.json")));
        Blocks.DIRT.addComponent(BlockRenderer.class, new DefaultBlockRenderer().setModelPath(blockModelPath.resolve("dirt.json")));
        registry.register(Blocks.GRASS);
        registry.register(Blocks.DIRT);
    }

    private static void registerItems(Registry<Item> registry) {
        registry.register(new BlockItem(Blocks.AIR));
        registry.register(Items.GRASS);
        registry.register(Items.DIRT);

        Items.GRASS.setComponent(ItemRenderer.class, new ItemBlockRenderer());
        Items.DIRT.setComponent(ItemRenderer.class, new ItemBlockRenderer());
    }

    private static void registerKeyBindings(Registry<KeyBinding> registry) {

        // TODO: When separating common and client, only register on client side
        // TODO: almost everything is hardcoded... Fix when GameContext and
        registry.register(
                KeyBinding.create("player.move.forward", Key.KEY_W, (c) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.FORWARD, true), ActionMode.PRESS)
                        .endAction((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.FORWARD, false)));
        registry.register(
                KeyBinding.create("player.move.backward", Key.KEY_S, (c) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.BACKWARD, true), ActionMode.PRESS)
                        .endAction((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.BACKWARD, false)));
        registry.register(KeyBinding.create("player.move.left", Key.KEY_A, (c) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.LEFT, true), ActionMode.PRESS)
                .endAction((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.LEFT, false)));
        registry.register(KeyBinding.create("player.move.right", Key.KEY_D, (c) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.RIGHT, true), ActionMode.PRESS)
                .endAction((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.RIGHT, false)));
        registry.register(KeyBinding.create("player.move.jump", Key.KEY_SPACE, (c) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.UP, true), ActionMode.PRESS)
                .endAction((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.UP, false)));
        registry.register(
                KeyBinding.create("player.move.sneak", Key.KEY_LEFT_SHIFT, (c) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.DOWN, true), ActionMode.PRESS)
                        .endAction((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.DOWN, false)));
        registry.register(KeyBinding.create("player.mouse.left", Key.MOUSE_BUTTON_LEFT, (c) -> {
            GameClient game = c.getCurrentGame();
            Player player = game.getPlayer();
            Camera camera = c.getRenderContext().getCamera();
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
        registry.register(KeyBinding.create("player.mouse.right", Key.MOUSE_BUTTON_RIGHT, (c) -> {
            GameClient game = c.getCurrentGame();
            Player player = game.getPlayer();
            Camera camera = c.getRenderContext().getCamera();
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
        registry.register(KeyBinding.create("player.mouse.middle", Key.MOUSE_BUTTON_3, (c) -> {
            GameClient game = c.getCurrentGame();
            Player player = game.getPlayer();
            Camera camera = c.getRenderContext().getCamera();
            Entity entity = player.getControlledEntity();
            player.getWorld().getCollisionManager().raycastBlock(camera.getPosition(), camera.getFrontVector(), 10).ifSuccess(hit ->
                    // TODO: Dont create BlockItem
                    entity.getComponent(TwoHands.class)
                            .ifPresent(twoHands -> twoHands.setMainHand(new ItemStack(new BlockItem(hit.getBlock()))))
            );
        }, ActionMode.PRESS));
        registry.register(KeyBinding.create("game.chat", Key.KEY_T, (c) -> {
            Scene scene = new Scene(new GuiChat(c.getCurrentGame()));
            c.getRenderContext().getGuiManager().showScreen(scene);
        }, ActionMode.PRESS));
        registry.register(KeyBinding.create("game.inventory", Key.KEY_E, (c) -> {
            Scene scene = new Scene(new GuiItemList(c.getRenderContext()));
            c.getRenderContext().getGuiManager().showScreen(scene);
        }, ActionMode.PRESS));

        var renderContext = Platform.getEngineClient().getRenderContext();
        var guiManager = renderContext.getGuiManager();
        var hudGameDebug = new HUDGameDebug();
//        renderContext.getScheduler().runTaskEveryFrame(() -> hudGameDebug.update(renderContext));
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
