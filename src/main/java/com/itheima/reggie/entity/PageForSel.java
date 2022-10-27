package com.itheima.reggie.entity;

import lombok.Data;

import java.util.List;

@Data
public class PageForSel<T> {
    private List<T> records;
    private long count;
}
