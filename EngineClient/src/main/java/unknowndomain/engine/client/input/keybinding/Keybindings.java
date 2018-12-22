package unknowndomain.engine.client.input.keybinding;


public enum Keybindings {
    INSTANCE;

    public void setup(KeyBindingManager manager) {
        manager.add(KeyBinding.create("unknowndomain.action.player.move.forward", Key.KEY_W, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.move.backward", Key.KEY_S, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.move.left", Key.KEY_A, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.move.right", Key.KEY_D, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.move.jump", Key.KEY_SPACE, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.move.sneak", Key.KEY_LEFT_SHIFT, ActionMode.PRESS, KeyModifier.SHIFT));
        manager.add(KeyBinding.create("unknowndomain.action.player.move.sneak", Key.KEY_LEFT_SHIFT, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.mouse.left", Key.MOUSE_BUTTON_LEFT, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.mouse.right", Key.MOUSE_BUTTON_RIGHT, ActionMode.PRESS));
    }
}
