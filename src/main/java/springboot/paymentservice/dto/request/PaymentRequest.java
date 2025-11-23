package springboot.paymentservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PaymentRequest {

    private Long amount;
    private String currency;
    private String description;
    private String paymentMethod;

}