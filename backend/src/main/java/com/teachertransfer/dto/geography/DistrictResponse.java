package com.teachertransfer.dto.geography;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistrictResponse {

    private Integer id;
    private String name;
    private String nameHindi;
    private String code;
    private Double lat;
    private Double lng;

    public DistrictResponse() {}

    public DistrictResponse(Integer id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public DistrictResponse(Integer id, String name, String nameHindi, String code, Double lat, Double lng) {
        this.id = id;
        this.name = name;
        this.nameHindi = nameHindi;
        this.code = code;
        this.lat = lat;
        this.lng = lng;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameHindi() {
        return nameHindi;
    }

    public void setNameHindi(String nameHindi) {
        this.nameHindi = nameHindi;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}