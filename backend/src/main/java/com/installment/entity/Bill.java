package com.installment.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Bill {
    private String billId;
    private String orderId;
    private String userId;
    private Integer period;
    private BigDecimal principal;
    private BigDecimal fee;
    private BigDecimal penalty;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    private LocalDateTime paidTime;
    private String status;
    private String remark;

    public Bill() {
        this.paidAmount = BigDecimal.ZERO;
        this.penalty = BigDecimal.ZERO;
        this.status = "PENDING";
    }

    public BigDecimal getRemainingAmount() {
        return totalAmount.add(penalty).subtract(paidAmount);
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && !"PAID".equals(status);
    }

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Integer getPeriod() { return period; }
    public void setPeriod(Integer period) { this.period = period; }
    public BigDecimal getPrincipal() { return principal; }
    public void setPrincipal(BigDecimal principal) { this.principal = principal; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
    public BigDecimal getPenalty() { return penalty; }
    public void setPenalty(BigDecimal penalty) { this.penalty = penalty; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getPaidTime() { return paidTime; }
    public void setPaidTime(LocalDateTime paidTime) { this.paidTime = paidTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
