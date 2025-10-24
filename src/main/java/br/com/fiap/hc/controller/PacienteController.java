package br.com.fiap.hc.controller;

import br.com.fiap.hc.dao.PacienteDao;
import br.com.fiap.hc.model.Paciente;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/paciente")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacienteController {

    private final PacienteDao pacienteDao;

    public PacienteController(PacienteDao pacienteDao) {
        this.pacienteDao = pacienteDao;
    }

    @GET
    @Path("/list")
    public Response list() {

        try {
            List<Paciente> paciente = pacienteDao.listar();
            return Response.status(200).entity(paciente).build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }
    }
}
