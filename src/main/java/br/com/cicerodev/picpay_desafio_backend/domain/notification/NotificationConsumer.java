package br.com.cicerodev.picpay_desafio_backend.domain.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.com.cicerodev.picpay_desafio_backend.authorization.AuthorizerService;
import br.com.cicerodev.picpay_desafio_backend.domain.transaction.Transaction;

@Service
public class NotificationConsumer {
    private RestClient restClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizerService.class);

    public NotificationConsumer(RestClient.Builder builder){
        this.restClient = builder
        .baseUrl("https://util.devi.tools/api/v2/authorize")
        .build();
    }

    @KafkaListener(topics = "transaction-notification", groupId = "picpay-desafio-backend" )
    public void receiveNotification(Transaction transaction) {
        LOGGER.info("notifying transiction {}", transaction);

        var response = restClient.get()
        .retrieve()
        .toEntity(Notification.class);

        if (response.getStatusCode().isError()) 
            throw new NotificationException("Error sending notification!");
        LOGGER.info("notification has been sent {}", transaction);
            
        

    }

    
}
