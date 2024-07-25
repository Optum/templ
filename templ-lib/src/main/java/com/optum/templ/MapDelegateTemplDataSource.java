package com.optum.templ;

import java.util.Map;

public class MapDelegateTemplDataSource implements TemplDataSource {
    final private Map<String, String> backingMap;

    public MapDelegateTemplDataSource(Map<String, String> backingMap) {
        this.backingMap = backingMap;
    }

    @Override
    public String lookupValueForKey(String key) {
        return backingMap.get(key);
    }
}
