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
    private String tagName;
    private String search;
    private List<SortBy> sortByList;
    private List<SortDir> sortDirList;
}
