package com.bank.vyom.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bank.vyom.model.SavingsTransaction;

public interface SavingsTransactionDao extends CrudRepository<SavingsTransaction, Long> {

    List<SavingsTransaction> findAll();
}

