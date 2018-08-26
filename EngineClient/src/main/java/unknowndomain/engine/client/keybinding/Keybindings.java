package unknowndomain.engine.client.keybinding;


public enum Keybindings {
    INSTANCE;

    public void setup(KeyBindingManager manager) {
        manager.add(KeyBinding.create("unknowndomain.action.player.move.forward", KeyCode.KEY_W, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.move.backward", KeyCode.KEY_S, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.move.left", KeyCode.KEY_A, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.move.right", KeyCode.KEY_D, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.move.jump", KeyCode.KEY_SPACE, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.move.sneak", KeyCode.KEY_LEFT_SHIFT, ActionMode.PRESS, KeyModifier.SHIFT));
        manager.add(KeyBinding.create("unknowndomain.action.player.move.sneak", KeyCode.KEY_LEFT_SHIFT, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.mouse.left", KeyCode.MOUSE_BUTTON_LEFT, ActionMode.PRESS));
        manager.add(KeyBinding.create("unknowndomain.action.player.mouse.right", KeyCode.MOUSE_BUTTON_RIGHT, ActionMode.PRESS));
    }
}
