package com.bank.vyom.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bank.vyom.model.PrimaryTransaction;



public interface PrimaryTransactionDao extends CrudRepository<PrimaryTransaction, Long> {

    List<PrimaryTransaction> findAll();
}
