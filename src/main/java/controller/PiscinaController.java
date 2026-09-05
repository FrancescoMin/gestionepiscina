package controller;

import model.SegreteriaDAO;
import exceptions.PiscinaException;

public class PiscinaController {
    private SegreteriaDAO dao;

    public PiscinaController() {
        this.dao = new SegreteriaDAO();
    }

    public boolean login(String username, String password) throws PiscinaException {
        return dao.login(username, password);
    }

    public String registraCliente(String cf, String nome, String cognome, java.sql.Date dataNascita, String via, String citta, String cap) throws PiscinaException {
        return dao.registraCliente(cf, nome, cognome, dataNascita, via, citta, cap);
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

    public void annullaIscrizione(String cf, String nomeCorso) throws PiscinaException {
        dao.annullaIscrizione(cf, nomeCorso);
    }

    public void registraAccesso(String cf) throws PiscinaException {
        dao.registraAccesso(cf);
    }

    public void generaReport(java.sql.Date dataInizio, java.sql.Date dataFine) throws PiscinaException {
        dao.generaReportPresenze(dataInizio, dataFine);
    }

    public void aggiungiCorso(String nome, String desc, double costo, int min, int max, int numVasca) throws PiscinaException {
        dao.aggiungiCorso(nome, desc, costo, min, max, numVasca);
    }

    public void aggiornaCorso(String nome, String desc, double costo, int min, int max, int numVasca) throws PiscinaException {
        dao.aggiornaCorso(nome, desc, costo, min, max, numVasca);
    }

    public java.util.List<String[]> ottieniRecapitiCliente(String cf) throws PiscinaException {
        return dao.getRecapitiCliente(cf);
    }

    public void aggiornaDatiCliente(String cf, String via, String citta, String cap) throws PiscinaException {
        dao.aggiornaDatiCliente(cf, via, citta, cap);
    }

    public void disattivaCliente(String cf) throws PiscinaException {
        dao.disattivaCliente(cf);
    }

    public java.util.List<String[]> ottieniOrariPiscina() throws PiscinaException {
        return dao.getOrariPiscina();
    }

    public void aggiornaOrarioPiscina(String giorno, java.sql.Time apertura, java.sql.Time chiusura) throws PiscinaException {
        dao.aggiornaOrario(giorno, apertura, chiusura);
    }

    public void disattivaCorso(String nome) throws PiscinaException {
        dao.disattivaCorso(nome);
    }

    public java.util.List<String[]> getCorsiAttivi() throws PiscinaException {
        return dao.getCorsiAttivi();
    }


    public void aggiungiOrarioCorso(String nomeCorso, String giorno, java.sql.Time inizio, java.sql.Time fine) throws PiscinaException {
        dao.aggiungiOrarioCorso(nomeCorso, giorno, inizio, fine);
    }

    public java.util.List<String> getOrariDiUnCorso(String nomeCorso) throws PiscinaException {
        return dao.getOrariDiUnCorso(nomeCorso);
    }
}