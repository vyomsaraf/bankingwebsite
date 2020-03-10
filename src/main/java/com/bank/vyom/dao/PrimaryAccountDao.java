package com.bank.vyom.dao;

import org.springframework.data.repository.CrudRepository;

import com.bank.vyom.model.PrimaryAccount;

/**
 * Created by z00382545 on 10/21/16.
 */
public interface PrimaryAccountDao extends CrudRepository<PrimaryAccount,Long> {

    PrimaryAccount findByAccountNumber (long accountNumber);
}
