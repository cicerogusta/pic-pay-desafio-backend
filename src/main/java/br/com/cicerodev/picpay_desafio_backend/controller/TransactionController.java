package br.com.cicerodev.picpay_desafio_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cicerodev.picpay_desafio_backend.domain.transaction.Transaction;
import br.com.cicerodev.picpay_desafio_backend.service.TransactionService;

@RestController
@RequestMapping("/transactions") // Mudança para plural, que é uma prática comum
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionService.create(transaction);
    }

    @GetMapping
    public List<Transaction> listTransactions() { // Nome do método mais descritivo
        return transactionService.list();
    }
}
