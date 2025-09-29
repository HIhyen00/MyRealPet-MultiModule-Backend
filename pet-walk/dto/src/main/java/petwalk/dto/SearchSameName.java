package petwalk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchSameName {
    private String[] region;

    private String keyword;

    @JsonProperty("selectedRegion")
    private String selectedRegion;
}