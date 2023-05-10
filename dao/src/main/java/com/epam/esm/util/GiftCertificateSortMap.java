package com.epam.esm.util;

import com.epam.esm.exception.InvalidSortRequestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class GiftCertificateSortMap {
    private final Map<String, String> configMap;
    private final String sortPattern;

    public GiftCertificateSortMap(Map<String, String> configMap) {
        Map<String, String> tempMap = new HashMap<>();
        tempMap.putAll(configMap);
        this.configMap = tempMap;

        StringJoiner joiner = new StringJoiner("|", "^((", ").(asc|desc))?$");
        configMap.keySet().forEach(joiner::add);
        sortPattern = joiner.toString();
    }

    public String getSortPattern() {
        return sortPattern;
    }

    public Map<String, String> getConfigMap() {
        Map<String, String> tempMap = new HashMap<>();
        tempMap.putAll(this.configMap);
        return tempMap;
    }

    public Map<String, String> getSortMap(List<String> sortBy) {
        Map<String, String> sortMap = new HashMap<>();
        if (sortBy == null) {
            return sortMap;
        }
        for (String s : sortBy) {
            if ((s == null) || !Pattern.matches(sortPattern, s)) {
                throw new InvalidSortRequestException();
            }
            if (!s.isEmpty()) {
                String[] sortByPair = s.split("\\.");
                sortMap.put(sortByPair[0], sortByPair[1].toUpperCase());
            }
        }
        return sortMap;
    }
}
