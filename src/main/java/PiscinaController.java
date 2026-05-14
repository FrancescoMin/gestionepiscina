import DAO.SegreteriaDAO;
import exceptions.PiscinaException;

import java.util.Date;
import java.sql.*;

public class PiscinaController {
    private SegreteriaDAO dao;

    public PiscinaController() {
        this.dao = new SegreteriaDAO();
    }

    public void gestisciNuovaIscrizione(String cf, String corso, Date dataInizio) {
        try {
            dao.iscriviCliente(cf, corso, (java.sql.Date) dataInizio);
            // Comunica alla View il successo
        } catch (PiscinaException e) {
            // Gestisce l'errore logico
        }
    }

    public void registraAccesso() {
    }

    public void iscrizioneCorso() {
    }

    public void generaReport() {
    }


    // Altri metodi per coordinare le operazioni...
}