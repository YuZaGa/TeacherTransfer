package com.teachertransfer.dto.geography;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockResponse {

    private Integer id;
    private Integer districtId;
    private String districtName;
    private String name;
    private String nameHindi;
    private String code;
    private Double lat;
    private Double lng;

    public BlockResponse() {}

    public BlockResponse(Integer id, Integer districtId, String name) {
        this.id = id;
        this.districtId = districtId;
        this.name = name;
    }

    public BlockResponse(Integer id, Integer districtId, String districtName, String name, String nameHindi, String code, Double lat, Double lng) {
        this.id = id;
        this.districtId = districtId;
        this.districtName = districtName;
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

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
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