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
import nullengine.client.rendering.util.GLHelper;
import nullengine.enginemod.client.gui.game.GUIGameCreation;
import nullengine.enginemod.client.gui.game.GuiChat;
import nullengine.enginemod.client.gui.game.GuiIngameMenu;
import nullengine.enginemod.client.gui.game.GuiItemList;
import nullengine.enginemod.client.gui.hud.HUDGameDebug;
import nullengine.entity.Entity;
import nullengine.entity.component.TwoHands;
import nullengine.entity.item.ItemEntity;
import nullengine.event.Listener;
import nullengine.event.block.BlockActivateEvent;
import nullengine.event.block.BlockClickEvent;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.event.engine.EngineEvent;
import nullengine.event.mod.ModLifecycleEvent;
import nullengine.event.mod.ModRegistrationEvent;
import nullengine.item.BlockItem;
import nullengine.item.ItemStack;
import nullengine.item.component.HitBlockBehavior;
import nullengine.item.component.UseBlockBehavior;
import nullengine.player.Player;
import nullengine.registry.impl.IdAutoIncreaseRegistry;
import nullengine.world.WorldProvider;
import nullengine.world.collision.RayTraceBlockHit;
import nullengine.world.provider.FlatWorldProvider;

public final class EngineModClientListeners {

    @Listener
    public static void onPreInit(ModLifecycleEvent.PreInitialization event) {
        Platform.getEngine().getEventBus().register(EngineModClientListeners.class);
    }

    @Listener
    public static void constructRegistry(ModRegistrationEvent.Construction e) {
        e.addRegistry(KeyBinding.class, () -> new IdAutoIncreaseRegistry<>(KeyBinding.class));
    }

    @Listener
    public static void registerWorldProvider(ModRegistrationEvent.Register<WorldProvider> event) {
        event.register(new FlatWorldProvider().name("flat"));
    }

    @Listener
    public static void registerBlocks(ModRegistrationEvent.Register<Block> event) {
        AirBlock.AIR.addComponent(BlockRenderer.class, new AirBlockRenderer());
    }

    @Listener
    public static void registerKeyBindings(ModRegistrationEvent.Register<KeyBinding> event) {

        // TODO: When separating common and client, only register on client side
        // TODO: almost everything is hardcoded... Fix when GameContext and
        event.register(
                KeyBinding.builder()
                        .name("player.move.forward")
                        .key(Key.KEY_W)
                        .startHandler(c -> c.getCurrentGame().getEntityController().handleMotion(MotionType.FORWARD, true))
                        .endHandler((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.FORWARD, false))
                        .build());
        event.register(
                KeyBinding.builder()
                        .name("player.move.backward")
                        .key(Key.KEY_S)
                        .startHandler(c -> c.getCurrentGame().getEntityController().handleMotion(MotionType.BACKWARD, true))
                        .endHandler((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.BACKWARD, false))
                        .build());
        event.register(KeyBinding.builder()
                .name("player.move.left")
                .key(Key.KEY_A)
                .startHandler(c -> c.getCurrentGame().getEntityController().handleMotion(MotionType.LEFT, true))
                .endHandler((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.LEFT, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.move.right")
                .key(Key.KEY_D)
                .startHandler(c -> c.getCurrentGame().getEntityController().handleMotion(MotionType.RIGHT, true))
                .endHandler((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.RIGHT, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.move.jump")
                .key(Key.KEY_SPACE)
                .startHandler(c -> c.getCurrentGame().getEntityController().handleMotion(MotionType.UP, true))
                .endHandler((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.UP, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.move.sneak")
                .key(Key.KEY_LEFT_SHIFT)
                .startHandler(c -> c.getCurrentGame().getEntityController().handleMotion(MotionType.DOWN, true))
                .endHandler((c, i) -> c.getCurrentGame().getEntityController().handleMotion(MotionType.DOWN, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.mouse.left")
                .key(Key.MOUSE_BUTTON_LEFT)
                .startHandler(c -> {
                    GameClient game = c.getCurrentGame();
                    Player player = game.getPlayer();
                    Camera camera = c.getRenderManager().getCamera();
                    Entity controllingEntity = player.getControlledEntity();
                    RayTraceBlockHit blockHit = player.getWorld().getCollisionManager().raycastBlock(camera.getPosition(), camera.getFrontVector(), 10);
                    if (blockHit.isSuccess()) {
                        blockHit.getBlock().getComponent(ClickBehavior.class).ifPresent(clickBehavior -> clickBehavior.onClicked(player.getWorld(), blockHit.getPos(), blockHit.getBlock()));
                        game.getEngine().getEventBus().post(new BlockClickEvent(player.getWorld(), controllingEntity, blockHit.getPos(), blockHit.getBlock()));
                        controllingEntity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                twoHands.getMainHand().ifNonEmpty(itemStack ->
                                        itemStack.getItem().getComponent(HitBlockBehavior.class).ifPresent(hitBlockBehavior ->
                                                hitBlockBehavior.onHit(player, itemStack, blockHit))));
                        // TODO: Remove it
                        player.getWorld().destoryBlock(blockHit.getPos(), new BlockChangeCause.PlayerEntityCause(player));
                    }
                })
                .build());
        event.register(KeyBinding.builder()
                .name("player.mouse.right")
                .key(Key.MOUSE_BUTTON_RIGHT)
                .startHandler(c -> {
                    GameClient game = c.getCurrentGame();
                    Player player = game.getPlayer();
                    Camera camera = c.getRenderManager().getCamera();
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
                })
                .build());
        event.register(KeyBinding.builder()
                .name("player.mouse.middle")
                .key(Key.MOUSE_BUTTON_3)
                .startHandler(c -> {
                    GameClient game = c.getCurrentGame();
                    Player player = game.getPlayer();
                    Camera camera = c.getRenderManager().getCamera();
                    Entity entity = player.getControlledEntity();
                    player.getWorld().getCollisionManager().raycastBlock(camera.getPosition(), camera.getFrontVector(), 10).ifSuccess(hit ->
                            // TODO: Dont create BlockItem
                            entity.getComponent(TwoHands.class)
                                    .ifPresent(twoHands -> twoHands.setMainHand(new ItemStack(new BlockItem(hit.getBlock()))))
                    );
                })
                .build());
        event.register(KeyBinding.builder()
                .name("game.chat")
                .key(Key.KEY_ENTER)
                .startHandler(c -> {
                    Scene scene = new Scene(new GuiChat(c.getCurrentGame()));
                    c.getRenderManager().getGuiManager().showScreen(scene);
                })
                .build());
        event.register(KeyBinding.builder()
                .name("game.inventory")
                .key(Key.KEY_E)
                .startHandler(c -> {
                    Scene scene = new Scene(new GuiItemList(c.getRenderManager()));
                    c.getRenderManager().getGuiManager().showScreen(scene);
                })
                .build());
        event.register(KeyBinding.builder()
                .name("game.menu")
                .key(Key.KEY_ESCAPE)
                .startHandler(c -> {
                    if (!c.getRenderManager().getGuiManager().isDisplayingScreen()) {
                        c.getRenderManager().getGuiManager().showScreen(new Scene(new GuiIngameMenu()));
                    }
                })
                .build());
        event.register(KeyBinding.builder()
                .name("game.screenshot")
                .key(Key.KEY_F2)
                .startHandler(engineClient -> GLHelper.takeScreenshot(engineClient.getRunPath().resolve("screenshot")))
                .build());

        var renderContext = Platform.getEngineClient().getRenderManager();
        var guiManager = renderContext.getGuiManager();
        var hudGameDebug = new HUDGameDebug();
//        renderContext.getScheduler().runTaskEveryFrame(() -> hudGameDebug.update(renderContext));
        event.register(KeyBinding.builder()
                .name("game.debug_display_switch")
                .key(Key.KEY_F3)
                .actionMode(ActionMode.SWITCH)
                .startHandler(gameClient -> guiManager.showHud("debugGame", new Scene(hudGameDebug)))
                .endHandler((gameClient, integer) -> guiManager.removeHud("debugGame"))
                .build());
        event.register(KeyBinding.builder()
                .name("game.hud_display_switch")
                .key(Key.KEY_F1)
                .startHandler(gameClient -> guiManager.toggleHudVisible())
                .build());
    }

    @Listener
    public static void registerEntityRenderer(EntityRendererRegistrationEvent event) {
        event.register(ItemEntity.class, new EntityItemRenderer());
    }

    @Listener
    public static void onEngineReady(EngineEvent.Ready event) {
        var renderContext = Platform.getEngineClient().getRenderManager();
        var guiManager = renderContext.getGuiManager();

        var scene = new Scene(new GUIGameCreation());
        guiManager.showScreen(scene);
    }
}
