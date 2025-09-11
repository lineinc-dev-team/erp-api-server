package com.lineinc.erp.api.server.domain.site.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SiteType {
    RETAINING_WALL("흙막이 공사"),
    RETAINING_AND_AUXILIARY("흙막이 및 부대토목 공사"),
    AUXILIARY_CIVIL("부대토목 공사"),
    ROAD_CONSTRUCTION("도로 공사"),
    LAND_DEVELOPMENT("택지 공사"),
    ETC("기타");

    private final String label;

}
