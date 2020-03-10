package com.bank.vyom.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bank.vyom.model.Appointment;



public interface AppointmentDao extends CrudRepository<Appointment, Long> {

    List<Appointment> findAll();
}
