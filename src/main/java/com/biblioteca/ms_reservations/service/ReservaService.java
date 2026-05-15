package com.biblioteca.ms_reservations.service;

import com.biblioteca.ms_reservations.model.Reserva;
import com.biblioteca.ms_reservations.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public Reserva crearReserva(Reserva reserva) {
        if (reservaRepository.existsByUsuarioIdAndLibroIdAndEstado(reserva.getUsuarioId(), reserva.getLibroId(), "PENDIENTE")) {
            throw new RuntimeException("El usuario ya tiene una reserva pendiente para este libro.");
        }

        reserva.setFechaReserva(LocalDate.now());
        reserva.setEstado("PENDIENTE");
        return reservaRepository.save(reserva);
    }

    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }

    public List<Reserva> obtenerPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    public Reserva cambiarEstado(Long id, String nuevoEstado) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La reserva con ID " + id + " no existe."));

        reserva.setEstado(nuevoEstado);
        return reservaRepository.save(reserva);
    }
}