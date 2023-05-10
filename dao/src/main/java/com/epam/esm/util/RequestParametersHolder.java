package com.epam.esm.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestParametersHolder {
    private List<String> tags;
    private String search;
    private Map<String, String> sortMap;// = new LinkedHashMap<>();
    private Integer page;
    private Integer size;

    public RequestParametersHolder(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }
}
