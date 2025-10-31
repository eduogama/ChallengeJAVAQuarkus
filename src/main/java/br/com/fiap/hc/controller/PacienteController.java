package br.com.fiap.hc.controller;

import br.com.fiap.hc.dao.PacienteDao;
import br.com.fiap.hc.model.Medico;
import br.com.fiap.hc.model.Paciente;
import br.com.fiap.hc.model.request.MedicoRequest;
import br.com.fiap.hc.model.request.PacienteRequest;
import jakarta.ws.rs.*;
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

    @GET
    @Path("/getById/{idPaciente}")
    public Response getById(@PathParam("idPaciente") Integer idPaciente) {

        try {
            Paciente paciente = pacienteDao.buscar(idPaciente);
            return Response.status(200).entity(paciente).build();
        } catch (Exception ex) {
            return Response.status(404).build();
        }
    }

    @POST
    @Path("/insert")
    public Response insert(Paciente paciente) {

        try {
            pacienteDao.cadastrar(paciente);
            return Response.status(201).build();
        } catch (Exception ex) {
            return Response.status(404).build();
        }
    }

    @PUT
    @Path("/update")
    public Response update(Paciente paciente) {

        try {
            pacienteDao.atualizar(paciente);
            return Response.status(200).build();
        } catch (Exception ex) {
            return Response.status(404).build();
        }
    }

    @DELETE
    @Path("/delete")
    public Response delete(PacienteRequest paciente) {

        try {
            pacienteDao.remover(paciente.getIdPaciente());
            return Response.status(200).build();
        } catch (Exception ex) {
            return Response.status(404).build();
        }
    }
}
