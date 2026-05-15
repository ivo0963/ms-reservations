package com.biblioteca.ms_reservations.repository;

import com.biblioteca.ms_reservations.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioId(Long usuarioId);

    List<Reserva> findByLibroIdAndEstado(Long libroId, String estado);

    boolean existsByUsuarioIdAndLibroIdAndEstado(Long usuarioId, Long libroId, String estado);
}