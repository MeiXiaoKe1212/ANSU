package com.ansu.api.enums;

/**
 * 运输状态枚举
 */
public enum TransportStatusEnum {

    CREATED("CREATED", "已创建"),
    DEPARTED("DEPARTED", "已出发"),
    TRANSPORTING("TRANSPORTING", "运输中"),
    EXCEPTION("EXCEPTION", "异常"),
    DELIVERED("DELIVERED", "已送达");

    private final String code;
    private final String desc;

    TransportStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TransportStatusEnum getByCode(String code) {
        for (TransportStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}