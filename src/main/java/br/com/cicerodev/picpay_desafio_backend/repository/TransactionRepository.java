package br.com.cicerodev.picpay_desafio_backend.repository;

import org.springframework.data.repository.ListCrudRepository;

import br.com.cicerodev.picpay_desafio_backend.domain.transaction.Transaction;

public interface TransactionRepository extends ListCrudRepository<Transaction, Long>{
    
}
