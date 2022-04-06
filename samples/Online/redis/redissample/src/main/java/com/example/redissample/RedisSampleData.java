package com.example.redissample;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
class RedisSampleData {

    private String string;

    private List<String> list;

    private Map<String, String> map;

}