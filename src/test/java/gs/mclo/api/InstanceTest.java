package gs.mclo.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InstanceTest {
    @Test
    public void defaults() {
        Instance instance = new Instance();
        assertEquals("https://api.mclo.gs/", instance.getApiBaseUrl());
        assertEquals("https://api.mclo.gs/1/log", instance.getLogUploadUrl());
        assertEquals("https://api.mclo.gs/1/raw/HpAwPry", instance.getRawLogUrl("HpAwPry"));
    }

    @Test
    public void constructor() {
        Instance instance = new Instance("https://custom.api/");
        assertEquals("https://custom.api/", instance.getApiBaseUrl());
        assertEquals("https://custom.api/1/log", instance.getLogUploadUrl());
        assertEquals("https://custom.api/1/raw/HpAwPry", instance.getRawLogUrl("HpAwPry"));
    }

    @Test
    public void setter() {
        Instance instance = new Instance();
        assertSame(instance, instance.setApiBaseUrl("https://another.api"));
        assertEquals("https://another.api/", instance.getApiBaseUrl());
        assertEquals("https://another.api/1/log", instance.getLogUploadUrl());
        assertEquals("https://another.api/1/raw/HpAwPry", instance.getRawLogUrl("HpAwPry"));
    }

    @Test
    public void setterBlank() {
        Instance instance = new Instance();
        assertThrows(IllegalArgumentException.class, () -> instance.setApiBaseUrl("   "));
    }

    @Test
    public void setterNull() {
        Instance instance = new Instance();
        //noinspection DataFlowIssue
        assertThrows(NullPointerException.class, () -> instance.setApiBaseUrl(null));
    }
}
