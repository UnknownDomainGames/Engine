package engine.graphics.lwjgl.font;

import com.google.common.collect.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class UnicodeBlockWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnicodeBlockWrapper.class);

    private static final Map<Character.UnicodeBlock, Range<Integer>> map = new HashMap<>();

    public static Range<Integer> getRange(Character.UnicodeBlock block) {
        return map.get(block);
    }

    public static int getBlockSize(Character.UnicodeBlock block) {
        var range = getRange(block);
        return range.upperEndpoint() - range.lowerEndpoint() + 1;
    }

    static {
        try {
            var blocksField = Character.UnicodeBlock.class.getDeclaredField("blocks");
            blocksField.setAccessible(true);
            var blocks = (Character.UnicodeBlock[]) blocksField.get(null);
            var blksField = Character.UnicodeBlock.class.getDeclaredField("blockStarts");
            blksField.setAccessible(true);
            var blockStarts = (int[]) blksField.get(null);
            // blocks and blockStarts has the same length, and the last block recorded ends with code point 0x10FFFF
            for (int i = 0; i < blocks.length; i++) {
                if (blocks[i] == null) continue; //unassigned
                int rangeStart = blockStarts[i];
                int rangeEnd;
                if (i == blocks.length - 1) { //last block recorded
                    rangeEnd = 0x10FFFF;
                } else {
                    rangeEnd = blockStarts[i + 1] - 1;
                }
                var range = Range.closed(rangeStart, rangeEnd);
                map.put(blocks[i], range);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Cannot initialize unicode block.", e);
        }
    }
}
