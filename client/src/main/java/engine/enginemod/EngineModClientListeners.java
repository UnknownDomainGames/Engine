package engine.enginemod;

import engine.Platform;
import engine.block.AirBlock;
import engine.block.Block;
import engine.block.component.ActivateBehavior;
import engine.block.component.ClickBehavior;
import engine.client.event.rendering.RegisterEntityRendererEvent;
import engine.client.game.GameClient;
import engine.client.input.controller.MotionType;
import engine.client.input.keybinding.Key;
import engine.client.input.keybinding.KeyBinding;
import engine.enginemod.client.gui.game.GUIGameCreation;
import engine.enginemod.client.gui.game.GuiChat;
import engine.enginemod.client.gui.game.GuiIngameMenu;
import engine.enginemod.client.gui.game.GuiItemList;
import engine.enginemod.client.gui.hud.HUDDebug;
import engine.enginemod.client.gui.hud.HUDHandingItem;
import engine.entity.Entity;
import engine.entity.component.TwoHands;
import engine.entity.item.ItemEntity;
import engine.event.Listener;
import engine.event.block.BlockInteractEvent;
import engine.event.block.cause.BlockChangeCause;
import engine.event.block.cause.BlockInteractCause;
import engine.event.engine.EngineEvent;
import engine.event.entity.EntityInteractEvent;
import engine.event.entity.cause.EntityInteractCause;
import engine.event.game.GameStartEvent;
import engine.event.item.ItemInteractEvent;
import engine.event.item.cause.ItemInteractCause;
import engine.event.mod.ModLifecycleEvent;
import engine.event.mod.ModRegistrationEvent;
import engine.graphics.RenderManager;
import engine.graphics.block.BlockDisplay;
import engine.graphics.camera.Camera;
import engine.graphics.entity.EntityItemRenderer;
import engine.graphics.util.GLHelper;
import engine.gui.Scene;
import engine.item.ItemStack;
import engine.item.component.ActivateBlockBehavior;
import engine.item.component.ActivateEntityBehavior;
import engine.item.component.ClickBlockBehavior;
import engine.item.component.ClickEntityBehavior;
import engine.player.Player;
import engine.registry.Registries;
import engine.registry.impl.IdAutoIncreaseRegistry;
import engine.world.WorldProvider;
import engine.world.hit.BlockHitResult;
import engine.world.hit.EntityHitResult;
import engine.world.hit.HitResult;
import engine.world.provider.FlatWorldProvider;

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
                        .startHandler(c -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.FORWARD, true))
                        .endHandler((c, i) -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.FORWARD, false))
                        .build());
        event.register(
                KeyBinding.builder()
                        .name("player.move.backward")
                        .key(Key.KEY_S)
                        .startHandler(c -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.BACKWARD, true))
                        .endHandler((c, i) -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.BACKWARD, false))
                        .build());
        event.register(KeyBinding.builder()
                .name("player.move.left")
                .key(Key.KEY_A)
                .startHandler(c -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.LEFT, true))
                .endHandler((c, i) -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.LEFT, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.move.right")
                .key(Key.KEY_D)
                .startHandler(c -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.RIGHT, true))
                .endHandler((c, i) -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.RIGHT, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.move.jump")
                .key(Key.KEY_SPACE)
                .startHandler(c -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.UP, true))
                .endHandler((c, i) -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.UP, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.move.sneak")
                .key(Key.KEY_LEFT_SHIFT)
                .startHandler(c -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.DOWN, true))
                .endHandler((c, i) -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.DOWN, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.mouse.left")
                .key(Key.MOUSE_BUTTON_LEFT)
                .startHandler(c -> {
                    GameClient game = c.getCurrentGame();
                    Player player = game.getClientPlayer();
                    Camera camera = c.getRenderManager().getViewport().getCamera();
                    Entity entity = player.getControlledEntity();
                    HitResult hitResult = player.getWorld().raycast(camera.getPosition(), camera.getFront(), 10);
                    if (hitResult.isFailure()) {
                        var cause = new ItemInteractCause.PlayerCause(player);
                        entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                twoHands.getMainHand().ifNonEmpty(itemStack -> {
                                    game.getEngine().getEventBus().post(new ItemInteractEvent.Click(itemStack, cause));
                                    itemStack.getItem().getComponent(engine.item.component.ClickBehavior.class).ifPresent(clickBehavior ->
                                            clickBehavior.onClicked(itemStack, cause));
                                }));
                        return;
                    }
                    if (hitResult instanceof BlockHitResult) {
                        var blockHitResult = (BlockHitResult) hitResult;
                        var cause = new BlockInteractCause.PlayerCause(player);
                        game.getEngine().getEventBus().post(new BlockInteractEvent.Click(blockHitResult, cause));
                        blockHitResult.getBlock().getComponent(ClickBehavior.class).ifPresent(clickBehavior ->
                                clickBehavior.onClicked(blockHitResult, cause));
                        entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                twoHands.getMainHand().ifNonEmpty(itemStack ->
                                        itemStack.getItem().getComponent(ClickBlockBehavior.class).ifPresent(clickBlockBehavior ->
                                                clickBlockBehavior.onClicked(itemStack, blockHitResult, cause))));
                        // TODO: Remove it
                        player.getWorld().destroyBlock(blockHitResult.getPos(), new BlockChangeCause.PlayerCause(player));
                    } else if (hitResult instanceof EntityHitResult) {
                        var entityHitResult = (EntityHitResult) hitResult;
                        var cause = new EntityInteractCause.PlayerCause(player);
                        game.getEngine().getEventBus().post(new EntityInteractEvent.Click(entityHitResult, cause));
                        entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                twoHands.getMainHand().ifNonEmpty(itemStack ->
                                        itemStack.getItem().getComponent(ClickEntityBehavior.class).ifPresent(clickBlockBehavior ->
                                                clickBlockBehavior.onClicked(itemStack, entityHitResult, cause))));
                    }
                })
                .build());
        event.register(KeyBinding.builder()
                .name("player.mouse.right")
                .key(Key.MOUSE_BUTTON_RIGHT)
                .startHandler(c -> {
                    GameClient game = c.getCurrentGame();
                    Player player = game.getClientPlayer();
                    Camera camera = c.getRenderManager().getViewport().getCamera();
                    Entity entity = player.getControlledEntity();
                    HitResult hitResult = player.getWorld().raycast(camera.getPosition(), camera.getFront(), 10);
                    if (hitResult.isFailure()) {
                        var cause = new ItemInteractCause.PlayerCause(player);
                        entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                twoHands.getMainHand().ifNonEmpty(itemStack -> {
                                    game.getEngine().getEventBus().post(new ItemInteractEvent.Activate(itemStack, cause));
                                    itemStack.getItem().getComponent(engine.item.component.ActivateBehavior.class).ifPresent(activateBehavior ->
                                            activateBehavior.onActivate(itemStack, cause));
                                }));
                        return;
                    }
                    if (hitResult instanceof BlockHitResult) {
                        var blockHitResult = (BlockHitResult) hitResult;
                        var cause = new BlockInteractCause.PlayerCause(player);
                        game.getEngine().getEventBus().post(new BlockInteractEvent.Activate(blockHitResult, cause));
                        blockHitResult.getBlock().getComponent(ActivateBehavior.class).ifPresent(activateBehavior ->
                                activateBehavior.onActivated(blockHitResult, cause));
                        entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                twoHands.getMainHand().ifNonEmpty(itemStack ->
                                        itemStack.getItem().getComponent(ActivateBlockBehavior.class).ifPresent(activateBlockBehavior ->
                                                activateBlockBehavior.onActivate(itemStack, blockHitResult, cause))));
                    } else if (hitResult instanceof EntityHitResult) {
                        var entityHitResult = (EntityHitResult) hitResult;
                        var cause = new EntityInteractCause.PlayerCause(player);
                        game.getEngine().getEventBus().post(new EntityInteractEvent.Activate(entityHitResult, cause));
                        entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                twoHands.getMainHand().ifNonEmpty(itemStack ->
                                        itemStack.getItem().getComponent(ActivateEntityBehavior.class).ifPresent(activateEntityBehavior ->
                                                activateEntityBehavior.onActivate(itemStack, entityHitResult, cause))));
                    }
                })
                .build());
        event.register(KeyBinding.builder()
                .name("player.mouse.middle")
                .key(Key.MOUSE_BUTTON_3)
                .startHandler(c -> {
                    GameClient game = c.getCurrentGame();
                    Player player = game.getClientPlayer();
                    Camera camera = c.getRenderManager().getViewport().getCamera();
                    Entity entity = player.getControlledEntity();
                    player.getWorld().raycastBlock(camera.getPosition(), camera.getFront(), 10).ifSuccess(hit ->
                            entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                    Registries.getItemRegistry().getBlockItem(hit.getBlock()).ifPresent(item ->
                                            twoHands.setMainHand(new ItemStack(item)))));
                })
                .build());
        event.register(KeyBinding.builder()
                .name("game.chat")
                .key(Key.KEY_ENTER)
                .startHandler(c -> {
                    Scene scene = new Scene(new GuiChat(c.getCurrentGame()));
                    c.getRenderManager().getGUIManager().show(scene);
                })
                .build());
        event.register(KeyBinding.builder()
                .name("game.inventory")
                .key(Key.KEY_E)
                .startHandler(c -> {
                    Scene scene = new Scene(new GuiItemList(c.getRenderManager()));
                    c.getRenderManager().getGUIManager().show(scene);
                })
                .build());
        event.register(KeyBinding.builder()
                .name("game.menu")
                .key(Key.KEY_ESCAPE)
                .startHandler(c -> c.getRenderManager().getGUIManager().show(new Scene(new GuiIngameMenu())))
                .build());
        event.register(KeyBinding.builder()
                .name("game.screenshot")
                .key(Key.KEY_F2)
                .startHandler(engineClient -> GLHelper.takeScreenshot(engineClient.getRunPath().resolve("screenshot")))
                .build());
        var hudManager = Platform.getEngineClient().getRenderManager().getHUDManager();
        var hudGameDebug = new HUDDebug();
        hudManager.add(hudGameDebug);
        event.register(KeyBinding.builder()
                .name("game.debug_display_switch")
                .key(Key.KEY_F3)
                .startHandler(gameClient -> hudGameDebug.visible().set(!hudGameDebug.visible().get()))
                .build());
        event.register(KeyBinding.builder()
                .name("game.hud_display_switch")
                .key(Key.KEY_F1)
                .startHandler(gameClient -> hudManager.toggleVisible())
                .build());
    }

    @Listener
    public static void registerEntityRenderer(RegisterEntityRendererEvent event) {
        event.register(ItemEntity.class, new EntityItemRenderer());
    }

    @Listener
    public static void onEngineReady(EngineEvent.Ready event) {
        var renderContext = Platform.getEngineClient().getRenderManager();
        var guiManager = renderContext.getGUIManager();

        var scene = new Scene(new GUIGameCreation());
        guiManager.show(scene);
    }

    @Listener
    public static void onGameReady(GameStartEvent.Post event) {
        HUDHandingItem hudHandingItem = new HUDHandingItem();
        RenderManager.instance().getHUDManager().add(hudHandingItem);
        hudHandingItem.visible().set(true);
    }
}
