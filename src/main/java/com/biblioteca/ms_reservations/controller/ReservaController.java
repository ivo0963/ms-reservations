package com.biblioteca.ms_reservations.controller;

import com.biblioteca.ms_reservations.model.Reserva;
import com.biblioteca.ms_reservations.model.dto.ReservaDTO;
import com.biblioteca.ms_reservations.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Endpoints para la gestión de reservas de libros por parte de los usuarios")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    @Operation(summary = "Listar todas las reservas", description = "Obtiene el historial completo de todas las reservas registradas en el sistema")
    public CollectionModel<EntityModel<Reserva>> listar() {
        List<EntityModel<Reserva>> reservas = reservaService.obtenerTodas()
                .stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(
                reservas,
                linkTo(methodOn(ReservaController.class).listar()).withSelfRel()
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar reservas por usuario", description = "Busca y devuelve todas las reservas asociadas a un ID de usuario específico")
    public ResponseEntity<CollectionModel<EntityModel<Reserva>>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<EntityModel<Reserva>> reservas = reservaService.obtenerPorUsuario(usuarioId)
                .stream()
                .map(this::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(
                reservas,
                linkTo(methodOn(ReservaController.class).listarPorUsuario(usuarioId)).withSelfRel(),
                linkTo(methodOn(ReservaController.class).listar()).withRel("todas-las-reservas")
        ));
    }

    @PostMapping
    @Operation(summary = "Crear nueva reserva", description = "Registra una nueva reserva de un libro validando los datos de entrada")
    public ResponseEntity<EntityModel<Reserva>> crear(@Valid @RequestBody ReservaDTO dto) {

        // Mapeo del DTO a la Entidad
        Reserva reserva = new Reserva();
        reserva.setUsuarioId(dto.getUsuarioId());
        reserva.setLibroId(dto.getLibroId());
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setEstado(dto.getEstado());

        Reserva creada = reservaService.crearReserva(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(creada));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de reserva", description = "Modifica el estado actual de una reserva (ej. marcar como completada o cancelada)")
    public ResponseEntity<EntityModel<Reserva>> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        Reserva actualizada = reservaService.cambiarEstado(id, estado);
        return ResponseEntity.ok(toModel(actualizada));
    }

    private EntityModel<Reserva> toModel(Reserva reserva) {
        return EntityModel.of(
                reserva,
                linkTo(methodOn(ReservaController.class).listarPorUsuario(reserva.getUsuarioId())).withRel("reservas-del-usuario"),
                linkTo(methodOn(ReservaController.class).actualizarEstado(reserva.getId(), reserva.getEstado())).withRel("actualizar-estado"),
                linkTo(methodOn(ReservaController.class).listar()).withRel("todas-las-reservas")
        );
    }
}