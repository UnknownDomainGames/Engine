package unknowndomain.engine.client.keybinding;


public enum Keybindings {
    INSTANCE;

    public void setup(KeyBindingManager manager) {
        manager.add(KeyBinding.create("player.move.forward", KeyCode.KEY_W, ActionMode.PRESS));
        manager.add(KeyBinding.create("player.move.backward", KeyCode.KEY_S, ActionMode.PRESS));
        manager.add(KeyBinding.create("player.move.left", KeyCode.KEY_A, ActionMode.PRESS));
        manager.add(KeyBinding.create("player.move.right", KeyCode.KEY_D, ActionMode.PRESS));
        manager.add(KeyBinding.create("player.move.jump", KeyCode.KEY_SPACE, ActionMode.PRESS));
        manager.add(KeyBinding.create("player.move.sneak", KeyCode.KEY_LEFT_SHIFT, ActionMode.PRESS, KeyModifier.SHIFT));
    }
}
