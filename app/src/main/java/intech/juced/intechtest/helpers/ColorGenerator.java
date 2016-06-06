package intech.juced.intechtest.helpers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by juced on 04.06.2016.
 */
public class ColorGenerator {

    public static ColorGenerator DEFAULT;

    public static ColorGenerator MATERIAL;

    static {
        DEFAULT = create(Arrays.asList(
                0xfff16364,
                0xfff58559,
                0xfff9a43e,
                0xffe4c62e,
                0xff67bf74,
                0xff59a2be,
                0xff2093cd,
                0xffad62a7,
                0xff805781
        ));

        MATERIAL = create(Arrays.asList(
                /*0xffe57373,
                0xfff06292,
                0xffba68c8,
                0xff9575cd,
                0xff7986cb,
                0xff64b5f6,
                0xff4fc3f7,
                0xff4dd0e1,
                0xff4db6ac,
                0xff81c784,
                0xffaed581,
                0xffff8a65,
                0xffd4e157,
                0xffffd54f,
                0xffffb74d,
                0xffa1887f,
                0xff90a4ae*/

                0xFFF44336,   // Red 500
                0xFFE91E63,   // Pink 500
                0xFF9C27B0,   // Purple 500
                0xFF673AB7,   // Deep Purple 500
                0xFF3F51B5,   // Indigo 500
                0xFF2196F3,   // Blue 500
                0xFF03A9F4,   // Light Blue 500
                0xFF00BCD4,   // Cyan 500
                0xFF009688,   // Teal 500
                0xFF4CAF50,   // Green 500
                0xFF8BC34A,   // Light Green 500
                0xFFCDDC39,   // Lime 500
                0xFFFFEB3B,   // Yellow 500
                0xFFFFC107,   // Amber 500
                0xFFFF9800,   // Orange 500
                0xFFFF5722,   // Deep Orange 500
                0xFF795548,   // Brown 500
                0xFF9E9E9E,   // Grey 500
                0xFF607D8B,   // Blue Grey 500
                0xFF000000    // Black
        ));
    }

    private final List<Integer> mColors;
    private final Random mRandom;

    public static ColorGenerator create(List<Integer> colorList) {
        return new ColorGenerator(colorList);
    }

    private ColorGenerator(List<Integer> colorList) {
        mColors = colorList;
        mRandom = new Random(System.currentTimeMillis());
    }

    public int getRandomColor() {
        return mColors.get(mRandom.nextInt(mColors.size()));
    }

    public int getColor(Object key) {
        return mColors.get(Math.abs(key.hashCode()) % mColors.size());
    }

}
