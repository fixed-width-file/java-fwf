package io.github.kelsoncm.fwf.hydrating;

import io.github.kelsoncm.fwf.columns.CharColumn;
import io.github.kelsoncm.fwf.columns.PositiveIntegerColumn;
import io.github.kelsoncm.fwf.descriptors.DetailRowDescriptor;
import io.github.kelsoncm.fwf.descriptors.FileDescriptor;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestHydrating {

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<HydrateUtils> ctor = HydrateUtils.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertNotNull(ctor.newInstance());
    }

    @Test
    void testHydrateDehydrateCharColumn() {
        CharColumn col = new CharColumn("name", 10, "User Name");
        Map<String, Object> map = col.dehydrate();

        assertEquals("io.github.kelsoncm.fwf.columns.CharColumn", map.get("_hydrate_as"));

        Object hydrated = Hydrator.hydrate(map);
        assertInstanceOf(CharColumn.class, hydrated);

        CharColumn hCol = (CharColumn) hydrated;
        assertEquals("name", hCol.getName());
        assertEquals(10, hCol.getSize());
        assertEquals("User Name", hCol.getDescription());
    }

    @Test
    void testHydratePythonClassNames() {
        Map<String, Object> map = Map.of(
                "_hydrate_as", "pyfwf.columns.CharColumn",
                "args", List.of("name", 10, "User Name")
        );

        Object hydrated = HydrateUtils.hydrateObject(map);
        assertInstanceOf(CharColumn.class, hydrated);
        CharColumn hCol = (CharColumn) hydrated;
        assertEquals("name", hCol.getName());
        assertEquals(10, hCol.getSize());
    }

    @Test
    void testHydrateFileDescriptor() {
        CharColumn col1 = new CharColumn("name", 10);
        PositiveIntegerColumn col2 = new PositiveIntegerColumn("age", 3);
        DetailRowDescriptor detail = new DetailRowDescriptor(List.of(col1, col2));
        FileDescriptor fd = new FileDescriptor(List.of(detail));

        Map<String, Object> map = fd.dehydrate();
        Object hydrated = HydrateUtils.hydrateObject(map);

        assertInstanceOf(FileDescriptor.class, hydrated);
        FileDescriptor hFd = (FileDescriptor) hydrated;
        assertEquals(13, hFd.getLineSize());
    }

    @Test
    void testHydrateDehydrateEdgeCases() {
        assertNull(HydrateUtils.dehydrateObject(null));

        Map<String, Object> customMap = new HashMap<>();
        customMap.put("_hydrate_as", "io.github.kelsoncm.fwf.columns.CharColumn");
        customMap.put("args", List.of("name", 10));
        customMap.put("attributes", Map.of("start", 1));

        Object hydrated = HydrateUtils.hydrateObject(customMap);
        assertInstanceOf(CharColumn.class, hydrated);
        assertEquals(1, ((CharColumn) hydrated).getStart());

        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("_hydrate_as", "io.github.kelsoncm.fwf.descriptors.FileDescriptor");
        nestedMap.put("args", List.of(List.of(Map.of(
                "_hydrate_as", "io.github.kelsoncm.fwf.descriptors.DetailRowDescriptor",
                "args", List.of(List.of(Map.of(
                        "_hydrate_as", "io.github.kelsoncm.fwf.columns.PositiveDecimalColumn",
                        "args", List.of("price", 10, 2)
                )))
        ))));

        Object hydratedNested = HydrateUtils.hydrateObject(nestedMap);
        assertInstanceOf(FileDescriptor.class, hydratedNested);

        Map<String, Object> simpleArgMap = Map.of(
                "_hydrate_as", "io.github.kelsoncm.fwf.columns.PositiveIntegerColumn",
                "args", List.of("count", 5)
        );
        Object hydratedSimple = HydrateUtils.hydrateObject(simpleArgMap);
        assertInstanceOf(PositiveIntegerColumn.class, hydratedSimple);

        Map<String, Object> badAttrMap = new HashMap<>();
        badAttrMap.put("_hydrate_as", "io.github.kelsoncm.fwf.columns.CharColumn");
        badAttrMap.put("args", List.of("name", 10));
        badAttrMap.put("attributes", Map.of("nonExistentField", 123));
        assertNotNull(HydrateUtils.hydrateObject(badAttrMap));

        Map<String, Object> badCtorMap = Map.of(
                "_hydrate_as", "io.github.kelsoncm.fwf.columns.CharColumn",
                "args", List.of(1, 2, 3, 4, 5)
        );
        assertThrows(RuntimeException.class, () -> HydrateUtils.hydrateObject(badCtorMap));
    }

    @Test
    void testHydrateInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> HydrateUtils.hydrateObject(null));
        assertThrows(IllegalArgumentException.class, () -> HydrateUtils.hydrateObject(Collections.emptyMap()));
        assertThrows(IllegalArgumentException.class, () -> HydrateUtils.hydrateObject(Map.of("_hydrate_as", "")));
        assertThrows(RuntimeException.class, () -> HydrateUtils.hydrateObject(Map.of("_hydrate_as", "invalid.Class")));
    }
}
