package io.github.kelsoncm.fwf.hydrating;

import java.util.Map;

/**
 * Abstract base class for hydratable/dehydratable objects in Java FWF.
 */
public abstract class Hydrator {

    /**
     * Dehydrates the current object into a map representation.
     *
     * @return Map containing object state.
     */
    public Map<String, Object> dehydrate() {
        return HydrateUtils.dehydrateObject(this);
    }

    /**
     * Hydrates a map representation into an object.
     *
     * @param representation Map representation
     * @return Hydrated object
     */
    public static Object hydrate(Map<String, Object> representation) {
        return HydrateUtils.hydrateObject(representation);
    }
}
