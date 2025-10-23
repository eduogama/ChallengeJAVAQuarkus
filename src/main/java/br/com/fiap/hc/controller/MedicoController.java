package br.com.fiap.hc.controller;

import br.com.fiap.hc.dao.MedicoDao;
import br.com.fiap.hc.model.Medico;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/medico")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MedicoController {

    private final MedicoDao medicoDao;

    public MedicoController(MedicoDao medicoDao) {
        this.medicoDao = medicoDao;
    }

    @GET
    @Path("/list")
    public Response list() {

        try {
            List<Medico> medico = medicoDao.listarMedicos();
            return Response.status(200).entity(medico).build();
        } catch (Exception ex) {
            return Response.status(422).build();
        }
    }

    @GET
    @Path("/getById/{idMedico}")
    public Response getById(@PathParam("idMedico") Integer idMedico) {

        try {
            Medico medico = medicoDao.buscar(idMedico);
            return Response.status(200).entity(medico).build();
        } catch (Exception ex) {
            return Response.status(422).build();
        }
    }

    @PUT
    @Path("/insert")
    public Response insert(Medico medico) {

        try {
            medicoDao.cadastrarMedico(medico);
            return Response.status(201).build();
        } catch (Exception ex) {
            return Response.status(422).build();
        }
    }

    @PUT
    @Path("/update")
    public Response update(Medico medico) {

        try {
            medicoDao.atualizarMedico(medico);
            return Response.status(200).build();
        } catch (Exception ex) {
            return Response.status(422).build();
        }
    }

    @DELETE
    @Path("/delete")
    public Response delete(Medico medico) {

        try {
            medicoDao.removerMedico(medico.getIdMedico());
            return Response.status(200).build();
        } catch (Exception ex) {
            return Response.status(422).build();
        }
    }
}
