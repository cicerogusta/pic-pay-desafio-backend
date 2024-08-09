package br.com.cicerodev.picpay_desafio_backend.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cicerodev.picpay_desafio_backend.authorization.AuthorizerService;
import br.com.cicerodev.picpay_desafio_backend.domain.notification.NotificationService;
import br.com.cicerodev.picpay_desafio_backend.domain.transaction.Transaction;
import br.com.cicerodev.picpay_desafio_backend.domain.wallet.Wallet;
import br.com.cicerodev.picpay_desafio_backend.domain.wallet.WalletType;
import br.com.cicerodev.picpay_desafio_backend.exception.InvalidTransactionException;
import br.com.cicerodev.picpay_desafio_backend.repository.TransactionRepository;
import br.com.cicerodev.picpay_desafio_backend.repository.WalletRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AuthorizerService authorizerService;
    private final NotificationService notificationService;

    public TransactionService(
        TransactionRepository transactionRepository,
        WalletRepository walletRepository,
        AuthorizerService authorizerService,
        NotificationService notificationService

      ){
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.authorizerService = authorizerService;
        this.notificationService = notificationService;
    }

    // Rollback nas alterações do banco (retorna ao estado inicial)
    @Transactional
    public Transaction create(Transaction transaction) {
        // 1 - validar
        validate(transaction);

        // 2 - criar a transação
        var newTransaction = transactionRepository.save(transaction);

        // 3 - debitar da carteira
        var walletPayer = walletRepository.findById(transaction.payer()).get();
        var walletPayee = walletRepository.findById(transaction.payee()).get();
        walletRepository.save(walletPayer.debit(transaction.value()));
        walletRepository.save(walletPayee.credit(transaction.value()));

        // 4 - chamar serviços externos
        // authorize transaction
        authorizerService.authorize(transaction);

        // 5 - notificação
        // send notification
        notificationService.notify(transaction);

        return newTransaction;

    }

    private void validate(Transaction transaction) {
        // Busca quem recebe (payee)
        walletRepository.findById(transaction.payee())
        // Busca o pagador (payeer)
        .map(payee -> walletRepository.findById(transaction.payer())
        // Retorna transação apenas se for válida
            .map(payer -> isTransactionValid(transaction, payer) ? transaction : null)
            .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s".formatted(transaction))))
         .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s".formatted(transaction)));
       
    }

    private boolean isTransactionValid(Transaction transaction, Wallet payer) {
               // Verifica to tipo do pagador
        return payer.type() == WalletType.COMUM.getValue() &&
            // Verifica se o pagador tem o dinheiro para enviar
             payer.balance().compareTo(transaction.value()) >= 0 && 
             // Verifica se o id do pagador não é o mesmo do recebedor
             !payer.id().equals(transaction.payee());
    }

    public List<Transaction> list() {
        return transactionRepository.findAll();
    }
    
}
