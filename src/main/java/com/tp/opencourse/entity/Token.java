package com.tp.opencourse.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token implements Serializable {

    private String uuid;

    private String userKey;

    private Integer timeToLive;
}