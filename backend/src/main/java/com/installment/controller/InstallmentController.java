package com.installment.controller;

import com.installment.entity.Bill;
import com.installment.entity.InstallmentOrder;
import com.installment.entity.User;
import com.installment.service.InstallmentService;
import com.installment.service.ScheduledTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class InstallmentController {

    @Autowired
    private InstallmentService installmentService;

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(installmentService.getUser(userId));
    }

    @GetMapping("/installments/available")
    public ResponseEntity<List<Integer>> getAvailableInstallments(@RequestParam String userId) {
        return ResponseEntity.ok(installmentService.getAvailableInstallments(userId));
    }

    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            if (userId == null || userId.trim().isEmpty()) {
                throw new RuntimeException("用户ID不能为空");
            }
            Object totalAmountObj = request.get("totalAmount");
            if (totalAmountObj == null) {
                throw new RuntimeException("分期金额不能为空");
            }
            BigDecimal totalAmount = new BigDecimal(totalAmountObj.toString());
            Object installmentsObj = request.get("installments");
            if (installmentsObj == null) {
                throw new RuntimeException("分期期数不能为空");
            }
            int installments = ((Number) installmentsObj).intValue();

            InstallmentOrder order = installmentService.createOrder(userId, totalAmount, installments);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", order);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/bill/{billId}/pay")
    public ResponseEntity<Map<String, Object>> payBill(@PathVariable String billId, @RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        try {
            Bill bill = installmentService.payBill(billId, amount);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", bill);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/order/{orderId}/early-repay")
    public ResponseEntity<Map<String, Object>> earlyRepay(@PathVariable String orderId) {
        try {
            installmentService.earlyRepay(orderId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "提前还款成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/order/{orderId}/refund")
    public ResponseEntity<Map<String, Object>> refundOrder(@PathVariable String orderId, @RequestBody Map<String, Object> request) {
        BigDecimal refundAmount = new BigDecimal(request.get("refundAmount").toString());
        try {
            installmentService.refundOrder(orderId, refundAmount);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "退款成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/user/{userId}/orders")
    public ResponseEntity<List<InstallmentOrder>> getUserOrders(@PathVariable String userId) {
        return ResponseEntity.ok(installmentService.getUserOrders(userId));
    }

    @GetMapping("/user/{userId}/bills")
    public ResponseEntity<List<Bill>> getUserBills(@PathVariable String userId) {
        return ResponseEntity.ok(installmentService.getUserBills(userId));
    }

    @GetMapping("/user/{userId}/reminders")
    public ResponseEntity<List<String>> getReminders(@PathVariable String userId) {
        return ResponseEntity.ok(scheduledTaskService.getReminders(userId));
    }
}
