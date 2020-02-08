package engine.input;

import java.util.HashMap;
import java.util.Map;

public enum KeyCode {

    NONE(0, "None"),

    /**
     * Printable keys.
     */
    SPACE(32, " ", "Space"),
    APOSTROPHE(39, "'", "Apostrophe"),
    COMMA(44, ",", "Comma"),
    MINUS(45, "-", "Minus"),
    PERIOD(46, ".", "Period"),
    SLASH(47, "/", "Slash"),
    NUM_0(48, "0", "0"),
    NUM_1(49, "1", "1"),
    NUM_2(50, "2", "2"),
    NUM_3(51, "3", "3"),
    NUM_4(52, "4", "4"),
    NUM_5(53, "5", "5"),
    NUM_6(54, "6", "6"),
    NUM_7(55, "7", "7"),
    NUM_8(56, "8", "8"),
    NUM_9(57, "9", "9"),
    SEMICOLON(59, ";", "Semicolon"),
    EQUAL(61, "=", "Equal"),
    A(65, "A", "A"),
    B(66, "B", "B"),
    C(67, "C", "C"),
    D(68, "D", "D"),
    E(69, "E", "E"),
    F(70, "F", "F"),
    G(71, "G", "G"),
    H(72, "H", "H"),
    I(73, "I", "I"),
    J(74, "J", "J"),
    K(75, "K", "K"),
    L(76, "L", "L"),
    M(77, "M", "M"),
    N(78, "N", "N"),
    O(79, "O", "O"),
    P(80, "P", "P"),
    Q(81, "Q", "Q"),
    R(82, "R", "R"),
    S(83, "S", "S"),
    T(84, "T", "T"),
    U(85, "U", "U"),
    V(86, "V", "V"),
    W(87, "W", "W"),
    X(88, "X", "X"),
    Y(89, "Y", "Y"),
    Z(90, "Z", "Z"),
    LEFT_BRACKET(91, "[", "Left Bracket"),
    BACKSLASH(92, "\\", "Back Slash"),
    RIGHT_BRACKET(93, "]", "Right Bracket"),
    GRAVE_ACCENT(96, "`", "Grave Accent"),
    WORLD_1(161, "World 1"),
    WORLD_2(162, "World 2"),

    /**
     * Function keys.
     */
    ESCAPE(256, "Esc"),
    ENTER(257, "\n", "Enter"),
    TAB(258, "\t", "Tab"),
    BACKSPACE(259, "\b", "Backspace"),
    INSERT(260, "Insert"),
    DELETE(261, "Delete"),
    RIGHT(262, "Right"),
    LEFT(263, "Left"),
    DOWN(264, "Down"),
    UP(265, "Up"),
    PAGE_UP(266, "Page Up"),
    PAGE_DOWN(267, "Page Down"),
    HOME(268, "Home"),
    END(269, "End"),
    CAPS_LOCK(280, "Caps Lock"),
    SCROLL_LOCK(281, "Scroll Lock"),
    NUM_LOCK(282, "Num Lock"),
    PRINT_SCREEN(283, "Print Screen"),
    PAUSE(284, "Pause"),
    F1(290, "F1"),
    F2(291, "F2"),
    F3(292, "F3"),
    F4(293, "F4"),
    F5(294, "F5"),
    F6(295, "F6"),
    F7(296, "F7"),
    F8(297, "F8"),
    F9(298, "F9"),
    F10(299, "F10"),
    F11(300, "F11"),
    F12(301, "F12"),
    F13(302, "F13"),
    F14(303, "F14"),
    F15(304, "F15"),
    F16(305, "F16"),
    F17(306, "F17"),
    F18(307, "F18"),
    F19(308, "F19"),
    F20(309, "F20"),
    F21(310, "F21"),
    F22(311, "F22"),
    F23(312, "F23"),
    F24(313, "F24"),
    F25(314, "F25"),
    NUMPAD_0(320, "0", "Numpad 0"),
    NUMPAD_1(321, "1", "Numpad 1"),
    NUMPAD_2(322, "2", "Numpad 2"),
    NUMPAD_3(323, "3", "Numpad 3"),
    NUMPAD_4(324, "4", "Numpad 4"),
    NUMPAD_5(325, "5", "Numpad 5"),
    NUMPAD_6(326, "6", "Numpad 6"),
    NUMPAD_7(327, "7", "Numpad 7"),
    NUMPAD_8(328, "8", "Numpad 8"),
    NUMPAD_9(329, "9", "Numpad 9"),
    NUMPAD_DECIMAL(330, ".", "Numpad ."),
    NUMPAD_DIVIDE(331, "/", "Numpad /"),
    NUMPAD_MULTIPLY(332, "*", "Numpad *"),
    NUMPAD_SUBTRACT(333, "-", "Numpad -"),
    NUMPAD_ADD(334, "+", "Numpad +"),
    NUMPAD_ENTER(335, "\n", "Numpad Enter"),
    NUMPAD_EQUAL(336, "=", "Numpad ="),
    LEFT_SHIFT(340, "Left Shift"),
    LEFT_CONTROL(341, "Left Ctrl"),
    LEFT_ALT(342, "Left Alt"),
    LEFT_SUPER(343, "Left Super"),
    RIGHT_SHIFT(344, "Right Shift"),
    RIGHT_CONTROL(345, "Right Ctrl"),
    RIGHT_ALT(346, "Right Alt"),
    RIGHT_SUPER(347, "Right Super"),
    MENU(348, "Menu");

    private static Map<Integer, KeyCode> code2Key = new HashMap<>();

    private final int code;
    private final String character;
    private final String name;

    static {
        for (KeyCode key : values()) {
            code2Key.put(key.code, key);
        }
    }

    public static KeyCode valueOf(int code) {
        return code2Key.getOrDefault(code, NONE);
    }

    KeyCode(int code, String name) {
        this(code, "", name);
    }

    KeyCode(int code, String character, String name) {
        this.code = code;
        this.character = character;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getCharacter() {
        return character;
    }

    public String getName() {
        return name;
    }
}
