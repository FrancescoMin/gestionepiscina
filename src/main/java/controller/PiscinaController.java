package controller;

import model.SegreteriaDAO;
import exceptions.PiscinaException;

public class PiscinaController {
    private SegreteriaDAO dao;

    public PiscinaController() {
        this.dao = new SegreteriaDAO();
    }

    public void registraCliente(String cf, String nome, String cognome, java.sql.Date dataNascita, String via, String citta, String cap, String badge) throws PiscinaException {
        dao.registraCliente(cf, nome, cognome, dataNascita, via, citta, cap, badge);
    }

    public void aggiungiRecapito(String cf, String tipo, String valore) throws PiscinaException {
        dao.aggiungiRecapito(cf, tipo, valore);
    }

    public void aggiornaRecapito(String cf, String vecchioValore, String nuovoValore, String tipo) throws PiscinaException {
        dao.modificaRecapito(cf, vecchioValore, nuovoValore, tipo);
    }

    public void iscriviCliente(String cf, String nomeCorso, java.sql.Date dataInizio) throws PiscinaException {
        dao.iscriviCliente(cf, nomeCorso, dataInizio);
    }

    public void registraAccesso(String cf) throws PiscinaException {
        dao.registraAccesso(cf);
    }

    public void generaReport(java.sql.Date dataInizio, java.sql.Date dataFine) throws PiscinaException {
        dao.generaReportPresenze(dataInizio, dataFine);
    }

    public void aggiungiCorso(String nome, String desc, double costo, int min, int max) throws PiscinaException {
        dao.aggiungiCorso(nome, desc, costo, min, max);
    }

    public java.util.List<String[]> ottieniRecapitiCliente(String cf) throws PiscinaException {
        return dao.getRecapitiCliente(cf);
    }

}