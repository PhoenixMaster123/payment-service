package springboot.paymentservice.controller;

import com.stripe.Stripe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentControllerUnitTest {

    @AfterEach
    void cleanup() {
        Stripe.apiKey = null;
    }

    @Test
    void init_setsStripeApiKey() {
        PaymentController controller = new PaymentController();
        ReflectionTestUtils.setField(controller, "stripeSecretKey", "sk_test_abc");

        controller.init();

        assertThat(Stripe.apiKey).isEqualTo("sk_test_abc");
    }
}