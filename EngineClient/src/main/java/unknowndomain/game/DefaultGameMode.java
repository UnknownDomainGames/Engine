package unknowndomain.game;

import unknowndomain.engine.Platform;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.RayTraceBlockHit;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.block.ClientBlockAir;
import unknowndomain.engine.client.block.ClientBlockDefault;
import unknowndomain.engine.client.event.asset.AssetReloadEvent;
import unknowndomain.engine.client.event.rendering.EntityRendererRegistrationEvent;
import unknowndomain.engine.client.event.rendering.ItemRendererRegistrationEvent;
import unknowndomain.engine.client.gui.Scene;
import unknowndomain.engine.client.input.controller.MotionType;
import unknowndomain.engine.client.input.keybinding.ActionMode;
import unknowndomain.engine.client.input.keybinding.Key;
import unknowndomain.engine.client.input.keybinding.KeyBinding;
import unknowndomain.engine.client.rendering.block.model.BlockModel;
import unknowndomain.engine.client.rendering.camera.Camera;
import unknowndomain.engine.client.rendering.entity.EntityItemRenderer;
import unknowndomain.engine.client.rendering.item.ItemBlockRenderer;
import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.rendering.texture.TextureUV;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.entity.component.TwoHands;
import unknowndomain.engine.entity.item.EntityItem;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.event.engine.EngineEvent;
import unknowndomain.engine.event.game.GameEvent;
import unknowndomain.engine.event.registry.RegistrationStartEvent;
import unknowndomain.engine.event.registry.RegistryConstructionEvent;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemBlock;
import unknowndomain.engine.item.ItemPrototype;
import unknowndomain.engine.item.ItemStack;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.registry.impl.IdAutoIncreaseRegistry;
import unknowndomain.game.client.gui.game.GUIGameCreation;
import unknowndomain.game.client.gui.game.GuiChat;
import unknowndomain.game.client.gui.hud.HUDGameDebug;
import unknowndomain.game.init.Blocks;
import unknowndomain.game.init.Items;

import static unknowndomain.engine.client.rendering.texture.TextureTypes.BLOCK;

public final class DefaultGameMode {

    @Listener
    public void constructionRegistry(RegistryConstructionEvent e) {
        // TODO: move to common.
        e.register(new IdAutoIncreaseRegistry<>(Block.class));
        e.register(new IdAutoIncreaseRegistry<>(Item.class));

        e.register(new IdAutoIncreaseRegistry<>(KeyBinding.class));
        e.register(new IdAutoIncreaseRegistry<>(ClientBlock.class));
//        e.registerPostTask(Block.class, Item.class, (block, registry)->registry.register(new ItemBlock(block)));
    }

    @Listener
    public void registerStage(RegistrationStartEvent e) {
        RegistryManager registryManager = e.getRegistryManager();
        registerBlocks(registryManager.getRegistry(Block.class));
        registerItems(registryManager.getRegistry(Item.class));
        registerKeyBindings(registryManager.getRegistry(KeyBinding.class));
        registerClientBlock(registryManager.getRegistry(ClientBlock.class));
    }

    private void registerBlocks(Registry<Block> registry) {
        registry.register(Blocks.AIR);
        registry.register(Blocks.GRASS);
        registry.register(Blocks.DIRT);
    }

    private void registerItems(Registry<Item> registry) {
        registry.register(new ItemBlock(Blocks.AIR));
        registry.register(Items.GRASS);
        registry.register(Items.DIRT);
    }

    private void registerClientBlock(Registry<ClientBlock> registry) {
        registry.register(new ClientBlockAir(Blocks.AIR));
        registry.register(new ClientBlockDefault(Blocks.GRASS));
        registry.register(new ClientBlockDefault(Blocks.DIRT));
    }

    private void registerKeyBindings(Registry<KeyBinding> registry) {

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
            Entity controlingEntity = player.getControlledEntity();
            RayTraceBlockHit blockHit = player.getWorld().raycast(camera.getPosition(), camera.getFrontVector(), 10);
            controlingEntity.getComponent(TwoHands.class)
                    .ifPresent(twoHands -> twoHands.getMainHand()
                            .ifNonEmpty(itemStack -> itemStack.getItem()
                                    .getComponent(ItemPrototype.HitBlockBehavior.class)
                                    .ifPresent(hitBlockBehavior -> hitBlockBehavior.onHit(player, itemStack, blockHit))));

        }, ActionMode.PRESS));
        registry.register(KeyBinding.create("player.mouse.right", Key.MOUSE_BUTTON_RIGHT, (game) -> {
            Player player = game.getPlayer();
            Camera camera = game.getEngine().getRenderContext().getCamera();
            Entity entity = player.getControlledEntity();
            RayTraceBlockHit hit = player.getWorld().raycast(camera.getPosition(), camera.getFrontVector(), 10);
            entity.getComponent(TwoHands.class)
                    .ifPresent(twoHands -> twoHands.getMainHand()
                            .ifNonEmpty(itemStack -> itemStack.getItem()
                                    .getComponent(ItemPrototype.UseBlockBehavior.class)
                                    .ifPresent(useBlockBehavior -> useBlockBehavior.onUseBlockStart(player, itemStack, hit))));
        }, ActionMode.PRESS));
        registry.register(KeyBinding.create("player.mouse.middle", Key.MOUSE_BUTTON_3, (game) -> {
            Player player = game.getPlayer();
            Camera camera = game.getEngine().getRenderContext().getCamera();
            Entity entity = player.getControlledEntity();
            player.getWorld().raycast(camera.getPosition(), camera.getFrontVector(), 10).ifSuccess(hit ->
                    // TODO: Dont create ItemBlock
                    entity.getComponent(TwoHands.class)
                            .ifPresent(twoHands -> twoHands.setMainHand(new ItemStack(new ItemBlock(hit.getBlock()))))
            );
        }, ActionMode.PRESS));
        registry.register(KeyBinding.create("game.chat", Key.KEY_T, (game)->{
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
    @Deprecated
    public void assetLoad(AssetReloadEvent event) {
        AssetPath enginePath = AssetPath.of("engine");
        AssetPath blockTexturePath = AssetPath.of(enginePath, "texture", "block");
        TextureManager textureManager = Platform.getEngineClient().getRenderContext().getTextureManager();
        TextureUV side = textureManager.addTextureToAtlas(AssetPath.of(blockTexturePath, "grass_side.png"), BLOCK);
        TextureUV top = textureManager.addTextureToAtlas(AssetPath.of(blockTexturePath, "grass_top.png"), BLOCK);
        TextureUV bottom = textureManager.addTextureToAtlas(AssetPath.of(blockTexturePath, "dirt.png"), BLOCK);

        BlockModel blockModel = new BlockModel();
        blockModel.addCube(0, 0, 0, 1, 1, 1, new TextureUV[]{side, side, side, side, top, bottom});
        ClientBlockDefault.blockRendererMap.put(Blocks.GRASS, blockModel);

        blockModel = new BlockModel();
        blockModel.addCube(0, 0, 0, 1, 1, 1, bottom);
        ClientBlockDefault.blockRendererMap.put(Blocks.DIRT, blockModel);
    }

    @Listener
    public void engineInit(EngineEvent.InitializationComplete event) {
        var renderContext = Platform.getEngineClient().getRenderContext();
        var guiManager = renderContext.getGuiManager();

        var scene = new Scene(new GUIGameCreation());
        guiManager.showScreen(scene);
    }

    @Listener
    public void gameReady(GameEvent.Ready event) {

    }

    @Listener
    public void registerItemRenderer(ItemRendererRegistrationEvent event) {
        event.register(Items.GRASS, new ItemBlockRenderer());
        event.register(Items.DIRT, new ItemBlockRenderer());
    }

    @Listener
    public void registerEntityRenderer(EntityRendererRegistrationEvent event) {
        event.register(EntityItem.class, new EntityItemRenderer());
    }
}
