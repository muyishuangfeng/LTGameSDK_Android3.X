package com.gnetop.ltgame.core.model;

import java.io.Serializable;

public class ResultModel implements Serializable {
    //用户id
    private int user_id;
    //是否绑定第三方账号 1->是 0->否
    private int is_bind_third;
    //是否绑定游客信息 1->是 0->否
    private int is_bind_visitor;
    //是否绑定email 1->是 0->否
    private int is_bind_email;
    //用户的key，登录后使用
    private String ukey;
    // 是否注册 1->是 0->否
    private int is_register;
    //乐推订单ID
    private String lt_order_id;
    //类型
    private String lt_type;
    //单位
    private String goods_price_type;
    //价格
    private double goods_price;
    //订单号
    private String order_number ;
    //id
    private String id;
    //昵称
    private String nickName;
    //邮箱
    private String emali;
    //token
    private String accessToken;
    //时间
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmali() {
        return emali;
    }

    public void setEmali(String emali) {
        this.emali = emali;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getLt_order_id() {
        return lt_order_id;
    }

    public void setLt_order_id(String lt_order_id) {
        this.lt_order_id = lt_order_id;
    }


    public String getLt_type() {
        return lt_type;
    }

    public void setLt_type(String lt_type) {
        this.lt_type = lt_type;
    }



    public String getGoods_price_type() {
        return goods_price_type;
    }

    public void setGoods_price_type(String goods_price_type) {
        this.goods_price_type = goods_price_type;
    }

    public double getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(double goods_price) {
        this.goods_price = goods_price;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getIs_bind_third() {
        return is_bind_third;
    }

    public void setIs_bind_third(int is_bind_third) {
        this.is_bind_third = is_bind_third;
    }

    public int getIs_bind_visitor() {
        return is_bind_visitor;
    }

    public void setIs_bind_visitor(int is_bind_visitor) {
        this.is_bind_visitor = is_bind_visitor;
    }

    public int getIs_bind_email() {
        return is_bind_email;
    }

    public void setIs_bind_email(int is_bind_email) {
        this.is_bind_email = is_bind_email;
    }

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
    }

    public int getIs_register() {
        return is_register;
    }

    public void setIs_register(int is_register) {
        this.is_register = is_register;
    }


    @Override
    public String toString() {
        return "ResultModel{" +
                "user_id=" + user_id +
                ", is_bind_third=" + is_bind_third +
                ", is_bind_visitor=" + is_bind_visitor +
                ", is_bind_email=" + is_bind_email +
                ", ukey='" + ukey + '\'' +
                ", is_register=" + is_register +
                ", lt_order_id='" + lt_order_id + '\'' +
                ", lt_type='" + lt_type + '\'' +
                ", goods_price_type='" + goods_price_type + '\'' +
                ", goods_price=" + goods_price +
                ", order_number='" + order_number + '\'' +
                ", id='" + id + '\'' +
                ", nickName='" + nickName + '\'' +
                ", emali='" + emali + '\'' +
                '}';
    }




}
