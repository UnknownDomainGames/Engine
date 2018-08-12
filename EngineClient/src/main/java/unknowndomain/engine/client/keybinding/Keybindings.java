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
        manager.add(KeyBinding.create("player.move.sneak", KeyCode.KEY_LEFT_SHIFT, ActionMode.PRESS));
        manager.add(KeyBinding.create("player.mouse.left", KeyCode.MOUSE_BUTTON_LEFT, ActionMode.PRESS));
        manager.add(KeyBinding.create("player.mouse.right", KeyCode.MOUSE_BUTTON_RIGHT, ActionMode.PRESS));

        manager.add(KeyBinding.create("player.debug.addZ", KeyCode.KEY_I, ActionMode.PRESS));
        manager.add(KeyBinding.create("player.debug.subZ", KeyCode.KEY_O, ActionMode.PRESS));
        manager.add(KeyBinding.create("player.debug.addX", KeyCode.KEY_K, ActionMode.PRESS));
        manager.add(KeyBinding.create("player.debug.subX", KeyCode.KEY_L, ActionMode.PRESS));
    }
}
