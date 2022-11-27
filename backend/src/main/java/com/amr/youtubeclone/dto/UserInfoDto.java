package com.amr.youtubeclone.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private String id;

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String family_name;

    @JsonProperty("name")
    private String name;

    @JsonProperty("picture")
    private String picture;

    private String email;
}
