package com.biblioteca.ms_reservations.controller;

import com.biblioteca.ms_reservations.model.Reserva;
import com.biblioteca.ms_reservations.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
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
    public ResponseEntity<EntityModel<Reserva>> crear(@RequestBody Reserva reserva) {
        Reserva creada = reservaService.crearReserva(reserva);
        return ResponseEntity.ok(toModel(creada));
    }

    @PutMapping("/{id}/estado")
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