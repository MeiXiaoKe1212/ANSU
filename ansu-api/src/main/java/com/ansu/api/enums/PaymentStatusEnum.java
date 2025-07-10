package com.ansu.api.enums;

/**
 * 款项状态枚举
 */
public enum PaymentStatusEnum {

    UNPAID("UNPAID", "未付款"),
    PREPAID("PREPAID", "已预付费"),
    PAID("PAID", "已结款");

    private final String code;
    private final String desc;

    PaymentStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PaymentStatusEnum getByCode(String code) {
        for (PaymentStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}