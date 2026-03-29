package net.joseplay.allianceutils.api.internalListener;

import net.joseplay.allianceutils.api.extensions.AlliancePlugin;
import net.joseplay.allianceutils.api.internalListener.annotations.AuEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventManager {

    private static final boolean ALLOW_PRIVATE_HANDLERS = false;

    private static final Map<AuEventpriority, Map<JavaPlugin, Map<AuListener, List<Method>>>> listenerMethods = new ConcurrentHashMap<>();
    private static final Map<AuEventpriority, Map<AlliancePlugin, Map<AuListener, List<Method>>>> extensionListenerMethods = new ConcurrentHashMap<>();

    private static final String PREFIX = "[Extensions] [Event] ";

    /**
     * Registers an event listener for a Bukkit plugin.
     *
     * <p>Scans the listener for methods annotated with {@link AuEventHandler}
     * and groups them by priority.</p>
     *
     * <b>Use when:</b>
     * - Registering core plugin listeners
     *
     * <b>Avoid when:</b>
     * - Listener has invalid method signatures (ignored silently)
     *
     * <b>Side effects:</b>
     * - Uses reflection (performance cost on registration)
     *
     * @param listener listener instance
     * @param plugin owning plugin
     */
    public static void registerListener(AuListener listener, JavaPlugin plugin) {
        Map<AuEventpriority, List<Method>> handlerMethods = extractHandlerMethods(listener);

        Bukkit.getLogger().info(PREFIX + "Registering listener: " +
                listener.getClass().getSimpleName() +
                " (Plugin: " + plugin.getName() + ")");

        for (Map.Entry<AuEventpriority, List<Method>> entry : handlerMethods.entrySet()) {
            listenerMethods
                    .computeIfAbsent(entry.getKey(), k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(plugin, k -> new ConcurrentHashMap<>())
                    .put(listener, entry.getValue());
        }
    }

    /**
     * Registers an event listener for an Alliance extension.
     *
     * <b>Use when:</b>
     * - Registering extension-based listeners
     *
     * <b>Side effects:</b>
     * - Same as registerListener(JavaPlugin)
     *
     * @param listener listener instance
     * @param plugin owning extension
     */
    public static void registerListener(AuListener listener, AlliancePlugin plugin) {
        Map<AuEventpriority, List<Method>> handlerMethods = extractHandlerMethods(listener);

        Bukkit.getLogger().info(PREFIX + "Registering extension listener: " +
                listener.getClass().getSimpleName() +
                " (Extension: " + plugin.getExtensionName() + ")");

        for (Map.Entry<AuEventpriority, List<Method>> entry : handlerMethods.entrySet()) {
            extensionListenerMethods
                    .computeIfAbsent(entry.getKey(), k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(plugin, k -> new ConcurrentHashMap<>())
                    .put(listener, entry.getValue());
        }
    }

    /**
     * Extracts all valid event handler methods from a listener.
     *
     * <b>Rules:</b>
     * - Must have @AuEventHandler
     * - Must have exactly 1 parameter
     * - Must be public unless ALLOW_PRIVATE_HANDLERS = true
     *
     * @param listener listener instance
     * @return map grouped by priority
     */
    private static Map<AuEventpriority, List<Method>> extractHandlerMethods(AuListener listener) {
        Map<AuEventpriority, List<Method>> handlerMethods = new HashMap<>();

        for (Method method : listener.getClass().getMethods()) {

            if (!method.isAnnotationPresent(AuEventHandler.class)) continue;
            if (method.getParameterCount() != 1) continue;

            if (!Modifier.isPublic(method.getModifiers()) && !ALLOW_PRIVATE_HANDLERS) {
                continue;
            }

            if (!method.canAccess(listener)) {
                method.setAccessible(true);
            }

            AuEventpriority priority = method.getAnnotation(AuEventHandler.class).priority();
            handlerMethods.computeIfAbsent(priority, k -> new ArrayList<>()).add(method);
        }

        return handlerMethods;
    }

    /**
     * Dispatches an event to all registered listeners.
     *
     * <p>Respects priority order and cancellation rules.</p>
     *
     * <b>Use when:</b>
     * - Triggering custom events
     *
     * <b>Side effects:</b>
     * - Executes arbitrary listener code
     * - Exceptions are logged but do not stop execution
     *
     * @param event event instance
     */
    public static void callEvent(Object event) {
        dispatch(listenerMethods, event);
        dispatch(extensionListenerMethods, event);
    }

    /**
     * Internal dispatch logic.
     *
     * <b>Important behavior:</b>
     * - Stops execution if event is cancelled (except MONITOR)
     *
     * @param pluginsMap registry
     * @param event event instance
     */
    private static <P> void dispatch(
            Map<AuEventpriority, Map<P, Map<AuListener, List<Method>>>> pluginsMap,
            Object event
    ) {
        boolean cancelled = false;

        for (AuEventpriority priority : AuEventpriority.values()) {

            Map<P, Map<AuListener, List<Method>>> pluginMap = pluginsMap.get(priority);
            if (pluginMap == null) continue;

            if (cancelled && priority != AuEventpriority.MONITOR) continue;

            for (Map<AuListener, List<Method>> listenerMap : pluginMap.values()) {
                for (Map.Entry<AuListener, List<Method>> entry : listenerMap.entrySet()) {

                    for (Method method : entry.getValue()) {

                        if (!method.getParameterTypes()[0].isAssignableFrom(event.getClass()))
                            continue;

                        try {
                            method.invoke(entry.getKey(), event);
                        } catch (Exception e) {
                            Bukkit.getLogger().severe(
                                    PREFIX + "Listener error in: " +
                                            entry.getKey().getClass().getName()
                            );
                            e.printStackTrace();
                        }

                        if (event instanceof CancellableEvent ce && ce.isCancelled()) {
                            cancelled = true;
                        }
                    }
                }
            }
        }
    }

    /**
     * Unregisters all listeners from a Bukkit plugin.
     *
     * @param plugin plugin instance
     */
    public static void unregisterAll(JavaPlugin plugin) {
        Bukkit.getLogger().warning(PREFIX + "Unregistering all listeners (Plugin: " + plugin.getName() + ")");

        for (var entry : listenerMethods.entrySet()) {
            entry.getValue().remove(plugin);
        }
    }

    /**
     * Unregisters all listeners from an extension.
     *
     * @param plugin extension instance
     */
    public static void unregisterAll(AlliancePlugin plugin) {
        Bukkit.getLogger().warning(PREFIX + "Unregistering all listeners (Extension: " + plugin.getExtensionName() + ")");

        for (var entry : extensionListenerMethods.entrySet()) {
            entry.getValue().remove(plugin);
        }
    }

    /**
     * Unregisters a specific listener instance.
     *
     * @param listener target listener
     */
    public static void unregisterAll(AuListener listener) {
        unregister(listenerMethods, listener);
        unregister(extensionListenerMethods, listener);
    }

    /**
     * Internal removal logic for listeners.
     */
    private static <P> void unregister(
            Map<AuEventpriority, Map<P, Map<AuListener, List<Method>>>> pluginsMap,
            AuListener target
    ) {
        for (var entry : pluginsMap.entrySet()) {
            for (var listenerMap : entry.getValue().entrySet()) {
                listenerMap.getValue().keySet().removeIf(target::equals);
            }
        }
    }

    /**
     * Clears all registered listeners globally.
     *
     * <b>⚠ Dangerous:</b>
     * - Removes ALL listeners (plugins + extensions)
     */
    public static void unregisterAll() {
        Bukkit.getLogger().severe(PREFIX + "Clearing ALL listeners globally.");

        listenerMethods.clear();
        extensionListenerMethods.clear();
    }
}