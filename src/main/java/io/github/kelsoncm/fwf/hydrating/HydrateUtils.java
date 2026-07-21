package io.github.kelsoncm.fwf.hydrating;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Utility class to hydrate and dehydrate objects for Java FWF.
 */
public final class HydrateUtils {

    private HydrateUtils() {
    }

    /**
     * Map legacy or cross-language class names to Java class names for compatibility.
     */
    private static final Map<String, String> CLASS_NAME_MAPPING = Map.ofEntries(
            Map.entry("pyfwf.columns.CharColumn", "io.github.kelsoncm.fwf.columns.CharColumn"),
            Map.entry("pyfwf.columns.RightCharColumn", "io.github.kelsoncm.fwf.columns.RightCharColumn"),
            Map.entry("pyfwf.columns.PositiveIntegerColumn", "io.github.kelsoncm.fwf.columns.PositiveIntegerColumn"),
            Map.entry("pyfwf.columns.PositiveDecimalColumn", "io.github.kelsoncm.fwf.columns.PositiveDecimalColumn"),
            Map.entry("pyfwf.columns.DateTimeColumn", "io.github.kelsoncm.fwf.columns.DateTimeColumn"),
            Map.entry("pyfwf.columns.DateColumn", "io.github.kelsoncm.fwf.columns.DateColumn"),
            Map.entry("pyfwf.columns.TimeColumn", "io.github.kelsoncm.fwf.columns.TimeColumn"),
            Map.entry("pyfwf.descriptors.HeaderRowDescriptor", "io.github.kelsoncm.fwf.descriptors.HeaderRowDescriptor"),
            Map.entry("pyfwf.descriptors.DetailRowDescriptor", "io.github.kelsoncm.fwf.descriptors.DetailRowDescriptor"),
            Map.entry("pyfwf.descriptors.FooterRowDescriptor", "io.github.kelsoncm.fwf.descriptors.FooterRowDescriptor"),
            Map.entry("pyfwf.descriptors.FileDescriptor", "io.github.kelsoncm.fwf.descriptors.FileDescriptor"),

            // Legacy com.kelsoncm.fwf mapping
            Map.entry("com.kelsoncm.fwf.columns.CharColumn", "io.github.kelsoncm.fwf.columns.CharColumn"),
            Map.entry("com.kelsoncm.fwf.columns.RightCharColumn", "io.github.kelsoncm.fwf.columns.RightCharColumn"),
            Map.entry("com.kelsoncm.fwf.columns.PositiveIntegerColumn", "io.github.kelsoncm.fwf.columns.PositiveIntegerColumn"),
            Map.entry("com.kelsoncm.fwf.columns.PositiveDecimalColumn", "io.github.kelsoncm.fwf.columns.PositiveDecimalColumn"),
            Map.entry("com.kelsoncm.fwf.columns.DateTimeColumn", "io.github.kelsoncm.fwf.columns.DateTimeColumn"),
            Map.entry("com.kelsoncm.fwf.columns.DateColumn", "io.github.kelsoncm.fwf.columns.DateColumn"),
            Map.entry("com.kelsoncm.fwf.columns.TimeColumn", "io.github.kelsoncm.fwf.columns.TimeColumn"),
            Map.entry("com.kelsoncm.fwf.descriptors.HeaderRowDescriptor", "io.github.kelsoncm.fwf.descriptors.HeaderRowDescriptor"),
            Map.entry("com.kelsoncm.fwf.descriptors.DetailRowDescriptor", "io.github.kelsoncm.fwf.descriptors.DetailRowDescriptor"),
            Map.entry("com.kelsoncm.fwf.descriptors.FooterRowDescriptor", "io.github.kelsoncm.fwf.descriptors.FooterRowDescriptor"),
            Map.entry("com.kelsoncm.fwf.descriptors.FileDescriptor", "io.github.kelsoncm.fwf.descriptors.FileDescriptor")
    );

    @SuppressWarnings("unchecked")
    public static Object hydrateObject(Map<String, Object> representation) {
        if (representation == null) {
            throw new IllegalArgumentException("representation is required");
        }
        if (!representation.containsKey("_hydrate_as")) {
            throw new IllegalArgumentException("_hydrate_as is required");
        }

        String className = (String) representation.get("_hydrate_as");
        if (className == null || className.isBlank()) {
            throw new IllegalArgumentException("_hydrate_as must be a non-blank string");
        }

        if (CLASS_NAME_MAPPING.containsKey(className)) {
            className = CLASS_NAME_MAPPING.get(className);
        }

        List<Object> rawArgs = (List<Object>) representation.getOrDefault("args", Collections.emptyList());
        List<Object> args = new ArrayList<>();
        for (Object arg : rawArgs) {
            if (arg instanceof Map) {
                args.add(hydrateObject((Map<String, Object>) arg));
            } else if (arg instanceof List) {
                List<Object> hydratedList = new ArrayList<>();
                for (Object item : (List<?>) arg) {
                    if (item instanceof Map) {
                        hydratedList.add(hydrateObject((Map<String, Object>) item));
                    } else {
                        hydratedList.add(item);
                    }
                }
                args.add(hydratedList);
            } else {
                args.add(arg);
            }
        }

        try {
            Class<?> clazz = Class.forName(className);
            Object instance = instantiate(clazz, args);

            Map<String, Object> attributes = (Map<String, Object>) representation.get("attributes");
            if (attributes != null) {
                for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                    setField(instance, entry.getKey(), entry.getValue());
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Error hydrating class: " + className, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> dehydrateObject(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("_hydrate_as", obj.getClass().getName());

        try {
            Field hydratingArgsField = getField(obj.getClass(), "HYDRATING_ARGS");
            if (hydratingArgsField != null) {
                String[] argNames = (String[]) hydratingArgsField.get(null);
                List<Object> args = new ArrayList<>();
                for (String name : argNames) {
                    Field field = getField(obj.getClass(), name);
                    if (field != null) {
                        field.setAccessible(true);
                        Object val = field.get(obj);
                        args.add(dehydrateValue(val));
                    }
                }
                result.put("args", args);
            }
        } catch (Exception ignored) {
            // If no args defined, omit
        }

        return result;
    }

    private static Object dehydrateValue(Object val) {
        if (val instanceof Hydrator hydrator) {
            return hydrator.dehydrate();
        } else if (val instanceof List<?> list) {
            List<Object> dehydratedList = new ArrayList<>();
            for (Object item : list) {
                dehydratedList.add(dehydrateValue(item));
            }
            return dehydratedList;
        }
        return val;
    }

    private static Object instantiate(Class<?> clazz, List<Object> args) throws Exception {
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> ctor : constructors) {
            if (ctor.getParameterCount() == args.size()) {
                try {
                    Object[] paramValues = new Object[args.size()];
                    Class<?>[] paramTypes = ctor.getParameterTypes();
                    for (int i = 0; i < args.size(); i++) {
                        paramValues[i] = castValue(args.get(i), paramTypes[i]);
                    }
                    return ctor.newInstance(paramValues);
                } catch (Exception ignored) {
                }
            }
        }
        throw new NoSuchMethodException("No suitable constructor found for " + clazz.getName());
    }

    private static Object castValue(Object val, Class<?> targetType) {
        if (val == null) return null;
        if (targetType.isAssignableFrom(val.getClass())) return val;
        if (targetType == int.class || targetType == Integer.class) {
            return ((Number) val).intValue();
        }
        if (targetType == double.class || targetType == Double.class) {
            return ((Number) val).doubleValue();
        }
        if (targetType == String.class) {
            return val.toString();
        }
        return val;
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                Field f = current.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private static void setField(Object obj, String fieldName, Object value) {
        Field f = getField(obj.getClass(), fieldName);
        if (f != null) {
            try {
                f.set(obj, castValue(value, f.getType()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
