package com.busticket.controller;

import com.busticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PayOS payOS;

    @Autowired
    private TicketService ticketService;

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> handleWebhook(@RequestBody Webhook webhookBody) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Kiểm tra xem có phải là test webhook / confirm webhook từ PayOS không
            if (webhookBody != null && (
                    "confirm-webhook".equals(webhookBody.getDesc()) ||
                    (webhookBody.getData() != null && "confirm-webhook".equals(webhookBody.getData().getDesc()))
               )) {
                response.put("success", true);
                response.put("message", "Webhook confirmed successfully");
                return ResponseEntity.ok(response);
            }

            // Xác thực dữ liệu webhook sử dụng PayOS SDK v2
            WebhookData data = payOS.webhooks().verify(webhookBody);

            // Kiểm tra mã thành công (Mã 00)
            if ("00".equals(data.getCode())) {
                Long orderCode = data.getOrderCode();
                
                // Ủy quyền cập nhật trạng thái vé và ghế cho TicketService
                ticketService.completePayment(orderCode);
            }

            response.put("success", true);
            response.put("message", "Webhook received and verified");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
