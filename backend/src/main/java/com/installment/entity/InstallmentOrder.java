package com.installment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InstallmentOrder {
    private String orderId;
    private String userId;
    private BigDecimal totalAmount;
    private Integer installments;
    private BigDecimal feeRate;
    private BigDecimal totalFee;
    private String status;
    private LocalDateTime createTime;
    private List<Bill> bills = new ArrayList<>();

    public InstallmentOrder() {
        this.createTime = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public BigDecimal getTotalRepayable() {
        return totalAmount.add(totalFee);
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Integer getInstallments() { return installments; }
    public void setInstallments(Integer installments) { this.installments = installments; }
    public BigDecimal getFeeRate() { return feeRate; }
    public void setFeeRate(BigDecimal feeRate) { this.feeRate = feeRate; }
    public BigDecimal getTotalFee() { return totalFee; }
    public void setTotalFee(BigDecimal totalFee) { this.totalFee = totalFee; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public List<Bill> getBills() { return bills; }
    public void setBills(List<Bill> bills) { this.bills = bills; }
}
