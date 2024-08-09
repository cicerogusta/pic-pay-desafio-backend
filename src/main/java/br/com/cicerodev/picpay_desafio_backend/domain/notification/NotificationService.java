package br.com.cicerodev.picpay_desafio_backend.domain.notification;

import org.springframework.stereotype.Service;

import br.com.cicerodev.picpay_desafio_backend.domain.transaction.Transaction;

@Service
public class NotificationService {
    private final NotificationProducer notificationProducer;

    public NotificationService(NotificationProducer notificationProducer){
        this.notificationProducer = notificationProducer;
    }
    public void notify(Transaction transaction){
        notificationProducer.sendNotification(transaction);

    }
    
}
