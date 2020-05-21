package com.gnetop.ltgame.core.model.user;

public abstract class LTUser {

    public static final int GENDER_BOY  = 1;
    public static final int GENDER_GIRL = 2;
    public static final int GENDER_UNKONW = 0;



    public abstract String getUserId();

    public abstract String getUnionId();

    public abstract String getOpenId();

    public abstract String getUserNickName();

    public abstract int getUserGender();

    public abstract String getUserProvince();

    public abstract String getUserCity();

    public abstract String getUserHeadUrl();

    public abstract String getUserHeadUrlLarge();


    @Override
    public String toString() {
        return "BaseUser{" +
                "userId='" + getUserId() + '\'' +
                ", userNickName='" + getUserNickName() + '\'' +
                ", userGender=" + getUserGender() +
                ", userProvince='" + getUserProvince() + '\'' +
                ", userCity='" + getUserCity() + '\'' +
                ", userHeadUrl='" + getUserHeadUrl() + '\'' +
                ", userHeadUrlLarge='" + getUserHeadUrlLarge() + '\'' +
                '}';
    }
}
