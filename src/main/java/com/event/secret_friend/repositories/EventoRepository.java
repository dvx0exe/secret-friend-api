package com.event.secret_friend.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.event.secret_friend.entities.Event_Class;

public interface EventoRepository extends JpaRepository <Event_Class, Long> {
}
