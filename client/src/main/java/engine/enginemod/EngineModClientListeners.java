package engine.enginemod;

import engine.Platform;
import engine.block.AirBlock;
import engine.block.Block;
import engine.client.EngineClient;
import engine.client.event.graphics.RegisterEntityRendererEvent;
import engine.client.hud.HUDControl;
import engine.client.hud.HUDManager;
import engine.client.input.controller.MotionType;
import engine.client.input.keybinding.Key;
import engine.client.input.keybinding.KeyBinding;
import engine.enginemod.client.gui.game.GUIItemList;
import engine.enginemod.client.gui.game.GUIMainMenu;
import engine.enginemod.client.gui.game.GuiChat;
import engine.enginemod.client.gui.hud.HUDDebug;
import engine.enginemod.client.gui.hud.HUDHandingItem;
import engine.enginemod.client.network.ClientPacketEventsHandler;
import engine.entity.item.ItemEntity;
import engine.event.Listener;
import engine.event.engine.EngineEvent;
import engine.event.game.GameStartEvent;
import engine.event.mod.ModLifecycleEvent;
import engine.event.mod.ModRegistrationEvent;
import engine.graphics.block.BlockDisplay;
import engine.graphics.entity.EntityItemRenderer;
import engine.graphics.util.GraphicsUtils;
import engine.gui.Scene;
import engine.registry.impl.BaseRegistry;

public final class EngineModClientListeners {

    public static final HUDDebug HUD_DEBUG = new HUDDebug();
    public static final HUDHandingItem HUD_HANDING_ITEM = new HUDHandingItem();

    @Listener
    public static void onPreInit(ModLifecycleEvent.PreInitialization event) {
        Platform.getEngine().getEventBus().register(EngineModClientListeners.class);
        Platform.getEngine().getEventBus().register(ClientPacketEventsHandler.class);
    }

    @Listener
    public static void constructRegistry(ModRegistrationEvent.Construction e) {
        e.addRegistry(KeyBinding.class, () -> new BaseRegistry<>(KeyBinding.class));
        e.addRegistry(HUDControl.class, () -> new BaseRegistry<>(HUDControl.class));
    }

    @Listener
    public static void registerBlocks(ModRegistrationEvent.Register<Block> event) {
        AirBlock.AIR.setComponent(BlockDisplay.class, new BlockDisplay().visible(false));
    }

    @Listener
    public static void registerKeyBindings(ModRegistrationEvent.Register<KeyBinding> event) {

        // TODO: When separating common and client, only register on client side
        // TODO: almost everything is hardcoded... Fix when GameContext and
        event.register(
                KeyBinding.builder()
                        .name("player.move.forward")
                        .key(Key.KEY_W)
                        .startHandler(c -> c.getCurrentClientGame().getClientPlayer().getEntityController().onInputMove(MotionType.FORWARD, true))
                        .endHandler((c, i) -> c.getCurrentClientGame().getClientPlayer().getEntityController().onInputMove(MotionType.FORWARD, false))
                        .build());
        event.register(
                KeyBinding.builder()
                        .name("player.move.backward")
                        .key(Key.KEY_S)
                        .startHandler(c -> c.getCurrentClientGame().getClientPlayer().getEntityController().onInputMove(MotionType.BACKWARD, true))
                        .endHandler((c, i) -> c.getCurrentClientGame().getClientPlayer().getEntityController().onInputMove(MotionType.BACKWARD, false))
                        .build());
        event.register(KeyBinding.builder()
                .name("player.move.left")
                .key(Key.KEY_A)
                .startHandler(c -> c.getCurrentClientGame().getClientPlayer().getEntityController().onInputMove(MotionType.LEFT, true))
                .endHandler((c, i) -> c.getCurrentClientGame().getClientPlayer().getEntityController().onInputMove(MotionType.LEFT, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.move.right")
                .key(Key.KEY_D)
                .startHandler(c -> c.getCurrentClientGame().getClientPlayer().getEntityController().onInputMove(MotionType.RIGHT, true))
                .endHandler((c, i) -> c.getCurrentClientGame().getClientPlayer().getEntityController().onInputMove(MotionType.RIGHT, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.move.jump")
                .key(Key.KEY_SPACE)
                .startHandler(c -> c.getCurrentClientGame().getClientPlayer().getEntityController().onInputMove(MotionType.UP, true))
                .endHandler((c, i) -> c.getCurrentClientGame().getClientPlayer().getEntityController().onInputMove(MotionType.UP, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.move.sneak")
                .key(Key.KEY_LEFT_SHIFT)
                .startHandler(c -> c.getCurrentClientGame().getClientPlayer().getEntityController().onInputMove(MotionType.DOWN, true))
                .endHandler((c, i) -> c.getCurrentClientGame().getClientPlayer().getEntityController().onInputMove(MotionType.DOWN, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.mouse.left")
                .key(Key.MOUSE_BUTTON_LEFT)
                .startHandler(c -> {
                    c.getCurrentClientGame().getClientPlayer().getEntityController().onAttack();
                })
                .build());
        event.register(KeyBinding.builder()
                .name("player.mouse.right")
                .key(Key.MOUSE_BUTTON_RIGHT)
                .startHandler(c -> {
                    c.getCurrentClientGame().getClientPlayer().getEntityController().onInteract();
                })
                .build());
        event.register(KeyBinding.builder()
                .name("player.mouse.middle")
                .key(Key.MOUSE_BUTTON_3)
                .startHandler(c -> {
                    c.getCurrentClientGame().getClientPlayer().getEntityController().onPickBlock();
                })
                .build());
        event.register(KeyBinding.builder()
                .name("game.chat")
                .key(Key.KEY_ENTER)
                .startHandler(c -> {
                    Scene scene = new Scene(new GuiChat(c.getCurrentClientGame()));
                    c.getGraphicsManager().getGUIManager().show(scene);
                })
                .build());
        event.register(KeyBinding.builder()
                .name("game.inventory")
                .key(Key.KEY_E)
                .startHandler(c -> c.getGraphicsManager().getGUIManager().show(GUIItemList.create()))
                .build());
        event.register(KeyBinding.builder()
                .name("game.menu")
                .key(Key.KEY_ESCAPE)
                .startHandler(c -> c.setGamePauseState(true))
                .build());
        event.register(KeyBinding.builder()
                .name("game.screenshot")
                .key(Key.KEY_F2)
                .startHandler(engineClient -> GraphicsUtils.takeScreenshot(engineClient.getRunPath().resolve("screenshot")))
                .build());
        event.register(KeyBinding.builder()
                .name("game.debug_display_switch")
                .key(Key.KEY_F3)
                .startHandler(gameClient -> HUD_DEBUG.setVisible(!HUD_DEBUG.isVisible()))
                .build());
        HUDManager hudManager = Platform.getEngineClient().getGraphicsManager().getHUDManager();
        event.register(KeyBinding.builder()
                .name("game.hud_display_switch")
                .key(Key.KEY_F1)
                .startHandler(gameClient -> hudManager.toggleVisible())
                .build());
    }

    @Listener
    public static void registerHUDControls(ModRegistrationEvent.Register<HUDControl> event) {
        event.registerAll(HUD_DEBUG, HUD_HANDING_ITEM);
    }

    @Listener
    public static void registerEntityRenderer(RegisterEntityRendererEvent event) {
        event.register(ItemEntity.class, new EntityItemRenderer());
    }

    @Listener
    public static void onEngineReady(EngineEvent.Ready event) {
        if (event.getEngine() instanceof EngineClient) { //TODO: remove this when integrated server no longer use "Engine" type
            var renderContext = Platform.getEngineClient().getGraphicsManager();
            var guiManager = renderContext.getGUIManager();

            var scene = new Scene(new GUIMainMenu());
            guiManager.show(scene);
        }
    }

    @Listener
    public static void onGameReady(GameStartEvent.Post event) {
        HUD_HANDING_ITEM.setVisible(true);
    }
}
