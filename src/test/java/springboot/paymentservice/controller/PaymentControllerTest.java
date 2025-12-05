package springboot.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import springboot.paymentservice.dto.request.PaymentRequest;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@TestPropertySource(properties = {
        "stripe.secret.key=test_secret_key"
})
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private MockedStatic<PaymentIntent> paymentIntentStatic;

    @AfterEach
    void tearDown() {
        if (paymentIntentStatic != null) {
            paymentIntentStatic.close();
        }
    }

    @Test
    void createPaymentIntent_success_returnsClientSecret() throws Exception {

        PaymentRequest request = PaymentRequest.builder()
                .amount(1234L)
                .currency("usd")
                .description("Test payment")
                .paymentMethod("pm_card_visa")
                .build();

        PaymentIntent paymentIntent = mock(PaymentIntent.class);
        Mockito.when(paymentIntent.getClientSecret()).thenReturn("cs_test_123");

        paymentIntentStatic = Mockito.mockStatic(PaymentIntent.class);
        paymentIntentStatic.when(() -> PaymentIntent.create(Mockito.any(PaymentIntentCreateParams.class))).thenReturn(paymentIntent);

        mockMvc.perform(post("/api/payment/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientSecret").value("cs_test_123"));
    }

    @Test
    void createPaymentIntent_stripeThrows_returnsServerError() throws Exception {

        PaymentRequest request = PaymentRequest.builder()
                .amount(5000L)
                .currency("eur")
                .description("Should fail")
                .paymentMethod("pm_card_chargeDeclined")
                .build();

        paymentIntentStatic = Mockito.mockStatic(PaymentIntent.class);
        paymentIntentStatic.when(() -> PaymentIntent.create(Mockito.any(PaymentIntentCreateParams.class)))
                .thenThrow(new RuntimeException("Stripe error"));

        // Act & Assert
        mockMvc.perform(post("/api/payment/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }
}
