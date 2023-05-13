package com.epam.esm.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestParametersHolder {
    private List<String> tags;
    private String search;
    private List<String> sortList;
    private Integer page;
    private Integer size;

    public RequestParametersHolder(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public int getOffset() {
        return page * size;
    }
}
