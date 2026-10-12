package fallingleavesseasons;

import net.minecraft.world.level.Level;

import java.lang.reflect.Method;

/**
 * Fragt die aktuelle Jahreszeit ueber {@code SeasonHelper.getSeasonState(level).getSeason()} ab.
 */
final class SereneSeasonsBridge {
    private final Method getSeasonState;
    private final Method getSeason;

    private SereneSeasonsBridge(Method getSeasonState, Method getSeason) {
        this.getSeasonState = getSeasonState;
        this.getSeason = getSeason;
    }

    static SereneSeasonsBridge create() throws ReflectiveOperationException {
        ClassLoader loader = SereneSeasonsBridge.class.getClassLoader();
        Class<?> helper = Class.forName("sereneseasons.api.season.SeasonHelper", true, loader);
        Class<?> state = Class.forName("sereneseasons.api.season.ISeasonState", true, loader);
        return new SereneSeasonsBridge(helper.getMethod("getSeasonState", Level.class), state.getMethod("getSeason"));
    }

    /**
     * @return SPRING, SUMMER, AUTUMN oder WINTER
     * @throws java.lang.reflect.InvocationTargetException wenn Serene Seasons noch nicht bereit ist
     */
    String currentSeason(Level level) throws ReflectiveOperationException {
        Object state = this.getSeasonState.invoke(null, level);
        Object season = this.getSeason.invoke(state);
        return ((Enum<?>) season).name();
    }
}
