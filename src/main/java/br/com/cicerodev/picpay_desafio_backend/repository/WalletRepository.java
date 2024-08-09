package br.com.cicerodev.picpay_desafio_backend.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.cicerodev.picpay_desafio_backend.domain.wallet.Wallet;

public interface WalletRepository extends CrudRepository<Wallet, Long>{
    
}
