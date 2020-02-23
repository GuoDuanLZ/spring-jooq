package org.jooq.example.spring.dto;

import org.jooq.example.db.generated.tables.pojos.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class OrderUser {

    private Integer   orderId;
    private Integer   uid;
    private Long      amout;
    private Byte      status;
    private Timestamp orderTime;
    private List<User> user;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Long getAmout() {
        return amout;
    }

    public void setAmout(Long amout) {
        this.amout = amout;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Timestamp getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderUser orderUser = (OrderUser) o;
        return Objects.equals(orderId, orderUser.orderId) &&
                Objects.equals(uid, orderUser.uid) &&
                Objects.equals(amout, orderUser.amout) &&
                Objects.equals(status, orderUser.status) &&
                Objects.equals(orderTime, orderUser.orderTime) &&
                Objects.equals(user, orderUser.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, uid, amout, status, orderTime, user);
    }

    @Override
    public String toString() {
        return "OrderUser{" +
                "orderId=" + orderId +
                ", uid=" + uid +
                ", amout=" + amout +
                ", status=" + status +
                ", orderTime=" + orderTime +
                ", user=" + user +
                '}';
    }
}
