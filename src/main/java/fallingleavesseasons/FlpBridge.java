package fallingleavesseasons;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Zugriff auf {@code FallingLeavesPlus.CONFIG.get(ClientConfig.class).leafParticleChance} per Reflection.
 */
final class FlpBridge {
    private final Object holder;
    private final Method getConfig;
    private final Class<?> clientConfigClass;
    private final Field leafParticleChance;

    private FlpBridge(Object holder, Method getConfig, Class<?> clientConfigClass, Field leafParticleChance) {
        this.holder = holder;
        this.getConfig = getConfig;
        this.clientConfigClass = clientConfigClass;
        this.leafParticleChance = leafParticleChance;
    }

    static FlpBridge create() throws ReflectiveOperationException {
        ClassLoader loader = FlpBridge.class.getClassLoader();
        Class<?> mainClass = Class.forName("fuzs.fallingleavesplus.FallingLeavesPlus", true, loader);
        Field holderField = mainClass.getField("CONFIG");
        Object holder = holderField.get(null);

        // Die Methode wird ueber den oeffentlichen Typ des Feldes gesucht (ConfigHolder), nicht ueber die
        // Implementierungsklasse, die moeglicherweise nicht oeffentlich ist.
        Method getConfig = null;
        for (Method method : holderField.getType().getMethods()) {
            if (method.getName().equals("get") && method.getParameterCount() == 1
                    && method.getParameterTypes()[0] == Class.class) {
                getConfig = method;
                break;
            }
        }
        if (getConfig == null) {
            throw new NoSuchMethodException("ConfigHolder.get(Class) nicht gefunden");
        }

        Class<?> clientConfigClass = Class.forName("fuzs.fallingleavesplus.config.ClientConfig", true, loader);
        Field chance = clientConfigClass.getField("leafParticleChance");
        if (chance.getType() != double.class) {
            throw new NoSuchFieldException("leafParticleChance ist kein double");
        }
        return new FlpBridge(holder, getConfig, clientConfigClass, chance);
    }

    double read() throws ReflectiveOperationException {
        return this.leafParticleChance.getDouble(this.configInstance());
    }

    void write(double value) throws ReflectiveOperationException {
        this.leafParticleChance.setDouble(this.configInstance(), value);
    }

    private Object configInstance() throws ReflectiveOperationException {
        return this.getConfig.invoke(this.holder, this.clientConfigClass);
    }
}
