package nullengine.enginemod;

import nullengine.Platform;
import nullengine.block.AirBlock;
import nullengine.block.Block;
import nullengine.block.component.ActivateBehavior;
import nullengine.block.component.ClickBehavior;
import nullengine.client.block.AirBlockRenderer;
import nullengine.client.event.rendering.EntityRendererRegistrationEvent;
import nullengine.client.game.GameClient;
import nullengine.client.gui.Scene;
import nullengine.client.input.controller.MotionType;
import nullengine.client.input.keybinding.ActionMode;
import nullengine.client.input.keybinding.Key;
import nullengine.client.input.keybinding.KeyBinding;
import nullengine.client.rendering.block.BlockRenderer;
import nullengine.client.rendering.camera.Camera;
import nullengine.client.rendering.entity.EntityItemRenderer;
import nullengine.enginemod.client.gui.game.GUIGameCreation;
import nullengine.enginemod.client.gui.game.GuiChat;
import nullengine.enginemod.client.gui.game.GuiIngameMenu;
import nullengine.enginemod.client.gui.game.GuiItemList;
import nullengine.enginemod.client.gui.hud.HUDGameDebug;
import nullengine.entity.Entity;
import nullengine.entity.EntityProvider;
import nullengine.entity.component.TwoHands;
import nullengine.entity.item.ItemEntity;
import nullengine.event.Listener;
import nullengine.event.engine.EngineEvent;
import nullengine.event.mod.ModLifecycleEvent;
import nullengine.event.mod.ModRegistrationEvent;
import nullengine.event.world.block.BlockActivateEvent;
import nullengine.event.world.block.BlockClickEvent;
import nullengine.item.BlockItem;
import nullengine.item.Item;
import nullengine.item.ItemStack;
import nullengine.item.component.HitBlockBehavior;
import nullengine.item.component.UseBlockBehavior;
import nullengine.player.Player;
import nullengine.registry.game.BlockRegistry;
import nullengine.registry.impl.IdAutoIncreaseRegistry;
import nullengine.registry.impl.SimpleBlockRegistry;
import nullengine.registry.impl.SimpleEntityRegistry;
import nullengine.registry.impl.SimpleItemRegistry;
import nullengine.world.WorldProvider;
import nullengine.world.collision.RayTraceBlockHit;

public final class EngineModListeners {

    @Listener
    public static void onPreInit(ModLifecycleEvent.PreInitialization event) {
        Platform.getEngine().getEventBus().register(EngineModListeners.class);
    }

    @Listener
    public static void constructRegistry(ModRegistrationEvent.Construction e) {
        // TODO: move to common.
        e.addRegistry(WorldProvider.class, () -> new IdAutoIncreaseRegistry<>(WorldProvider.class));
        e.addRegistry(Block.class, SimpleBlockRegistry::new);
        e.addRegistry(Item.class, SimpleItemRegistry::new);
        e.addRegistry(EntityProvider.class, SimpleEntityRegistry::new);

        e.addRegistry(KeyBinding.class, () -> new IdAutoIncreaseRegistry<>(KeyBinding.class));
    }

    @Listener
    public static void registerBlocks(ModRegistrationEvent.Register<Block> event) {
        event.register(AirBlock.AIR);
        AirBlock.AIR.addComponent(BlockRenderer.class, new AirBlockRenderer());
        ((BlockRegistry) event.getRegistry()).setAirBlock(AirBlock.AIR);
    }

    @Listener
    public static void registerKeyBindings(ModRegistrationEvent.Register<KeyBinding> envet) {

        // TODO: When separating common and client, only register on client side
        // TODO: almost everything is hardcoded... Fix when GameContext and
        envet.register(
                KeyBinding.create("player.move.forward", Key.KEY_W, (c) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.FORWARD, true), ActionMode.PRESS)
                        .endAction((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.FORWARD, false)));
        envet.register(
                KeyBinding.create("player.move.backward", Key.KEY_S, (c) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.BACKWARD, true), ActionMode.PRESS)
                        .endAction((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.BACKWARD, false)));
        envet.register(KeyBinding.create("player.move.left", Key.KEY_A, (c) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.LEFT, true), ActionMode.PRESS)
                .endAction((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.LEFT, false)));
        envet.register(KeyBinding.create("player.move.right", Key.KEY_D, (c) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.RIGHT, true), ActionMode.PRESS)
                .endAction((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.RIGHT, false)));
        envet.register(KeyBinding.create("player.move.jump", Key.KEY_SPACE, (c) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.UP, true), ActionMode.PRESS)
                .endAction((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.UP, false)));
        envet.register(
                KeyBinding.create("player.move.sneak", Key.KEY_LEFT_SHIFT, (c) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.DOWN, true), ActionMode.PRESS)
                        .endAction((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.DOWN, false)));
        envet.register(KeyBinding.create("player.mouse.left", Key.MOUSE_BUTTON_LEFT, (c) -> {
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
        envet.register(KeyBinding.create("player.mouse.right", Key.MOUSE_BUTTON_RIGHT, (c) -> {
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
        envet.register(KeyBinding.create("player.mouse.middle", Key.MOUSE_BUTTON_3, (c) -> {
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
        envet.register(KeyBinding.create("game.chat", Key.KEY_T, (c) -> {
            Scene scene = new Scene(new GuiChat(c.getCurrentGame()));
            c.getRenderContext().getGuiManager().showScreen(scene);
        }, ActionMode.PRESS));
        envet.register(KeyBinding.create("game.inventory", Key.KEY_E, (c) -> {
            Scene scene = new Scene(new GuiItemList(c.getRenderContext()));
            c.getRenderContext().getGuiManager().showScreen(scene);
        }, ActionMode.PRESS));
        envet.register(KeyBinding.create("game.menu", Key.KEY_ESCAPE, (c) -> {
            if (!c.getRenderContext().getGuiManager().isDisplayingScreen()) {
                c.getRenderContext().getGuiManager().showScreen(new Scene(new GuiIngameMenu()));
            }
        }, ActionMode.PRESS));
        envet.register(KeyBinding.create("engine.fullscreen", true, Key.KEY_F11, (c) -> {
            c.getRenderContext().getWindow().toggleFullscreen();
        }, ActionMode.PRESS));

        var renderContext = Platform.getEngineClient().getRenderContext();
        var guiManager = renderContext.getGuiManager();
        var hudGameDebug = new HUDGameDebug();
//        renderContext.getScheduler().runTaskEveryFrame(() -> hudGameDebug.update(renderContext));
        envet.register(KeyBinding.create("debug.switch", Key.KEY_F3, gameClient -> guiManager.showHud("debugGame", new Scene(hudGameDebug))
                , ActionMode.SWITCH).endAction((gameClient, integer) -> guiManager.hideHud("debugGame")));
    }

    @Listener
    public static void registerEntityRenderer(EntityRendererRegistrationEvent event) {
        event.register(ItemEntity.class, new EntityItemRenderer());
    }

    @Listener
    public static void onEngineReady(EngineEvent.Ready event) {
        var renderContext = Platform.getEngineClient().getRenderContext();
        var guiManager = renderContext.getGuiManager();

        var scene = new Scene(new GUIGameCreation());
        guiManager.showScreen(scene);
    }
}
