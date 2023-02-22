/*
 *
 *     Copyright (C) 2019  Salkin (mc.salkin@gmail.com)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.settla.spigot.universe.event;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import de.settla.spigot.universe.WorldElement;

public class RegionEventHandler<T extends WorldElement> {

    private final Map<Class<? extends Event>, EnumMap<EventPriority, Set<EventMethod>>> methods = new HashMap<>();
    private final Class<T> clazz;

    public RegionEventHandler(Class<T> clazz) {
        checkNotNull(clazz);
        this.clazz = clazz;
        findMethods();
    }

    public Class<T> getRegionClass() {
        return clazz;
    }

    private void findMethods() {
        for (Method method : getRegionClass().getMethods()) {
            if (Modifier.isStatic(method.getModifiers()))
                continue;
            if (!method.isAnnotationPresent(EventHandler.class))
                continue;
            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1)
                continue;
            Class<?> event = params[0];
            if (!Event.class.isAssignableFrom(event))
                continue;
            EventHandler eventHandler = method.getDeclaredAnnotation(EventHandler.class);
            EnumMap<EventPriority, Set<EventMethod>> e = methods.get(event);
            if (e == null) {
                e = new EnumMap<>(EventPriority.class);
                methods.put((Class<Event>) event, e);
            }
            Set<EventMethod> m = e.computeIfAbsent(eventHandler.priority(), k -> new HashSet<>());
            m.add(new EventMethod(eventHandler, method));
        }
    }

    void fire(Event event, WorldElement region) {
        checkNotNull(region);
        checkNotNull(event);
        EnumMap<EventPriority, Set<EventMethod>> e = methods.get(event.getClass());
        if (e != null) {
            e.forEach((p, set) -> set.forEach(m -> m.fire(event, region)));
        }
    }

    private class EventMethod {

        private final EventHandler eventHandler;
        private final Method method;

        private EventMethod(EventHandler eventHandler, Method method) {
            this.eventHandler = eventHandler;
            this.method = method;
        }

        private void fire(Event event, WorldElement region) {
            try {
                if (eventHandler.ignoreCancelled() || !(event instanceof Cancellable) || !((Cancellable) event).isCancelled()) {
                    method.invoke(region, event);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}