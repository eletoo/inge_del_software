package it.unibs.ingsw;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class Scambio implements Serializable {

    //TODO: l'autore di una proposta in scambio può ritirarla o una volta che è in scambio non è più ritirabile?

    private Fruitore f1;
    private Offerta ownOffer;
    private Offerta selectedOffer;
    private String message;
    private LocalDateTime dateTime;

    public Scambio(Applicazione app, View view) {
        this.createExchange(app, view);
    }

    private void createExchange(@NotNull Applicazione app, @NotNull View view) {
        this.ownOffer = view.choose(app.getOfferte(this.f1)
                .stream()
                .filter(e -> e.isAvailableOffer())
                .collect(Collectors.toList()), null);

        this.selectedOffer = view.choose(app.getOfferte(this.ownOffer.getCategoria())
                .stream()
                .filter(e -> (this.ownOffer.getProprietario() != e.getProprietario()))
                .filter(e -> e.isAvailableOffer())
                .collect(Collectors.toList()), null);

        if (this.ownOffer != null && this.selectedOffer != null) {
            this.ownOffer.setStato(Offerta.StatoOfferta.ACCOPPIATA);
            this.selectedOffer.setStato(Offerta.StatoOfferta.SELEZIONATA);
        }
        this.message = "La tua offerta \"" + this.selectedOffer.getName() + "\" è stata selezionata per lo scambio con \"" + this.ownOffer.getName() + "\"";
        this.dateTime = LocalDateTime.now();
    }

    public boolean isValidExchange(@NotNull Applicazione app) {
        return LocalDateTime.now().isBefore(this.dateTime.plusDays(app.getInformazioni().getScadenza()));
    }

    /**
     * @param o offerta selezionata di cui si vuole ottenere il relativo scambio
     * @return lo scambio la cui offerta selezionata e' quella passata come parametro
     */
    public Scambio getScambio(Offerta o) {
        if (this.selectedOffer == o)
            return this;

        return null;
    }

    public void manageExchange(@NotNull View view, @NotNull Applicazione app) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nLuogo: " + view.choose(app.getInformazioni().getLuoghi(), null));
        sb.append("\nGiorno: " + view.choose(app.getInformazioni().getGiorni(), null));
        sb.append("\nOrario: "+ view.choose(app.getInformazioni().getIntervalliOrari(), null));
        //TODO: posso selezionare l'intervallo o il singolo orario al suo interno?
        this.message = sb.toString();

        //TODO: facendo così cambio lo stato dell'offerta in questo oggetto o dell'offerta salvata nell'applicazione?
        this.selectedOffer.setStato(Offerta.StatoOfferta.IN_SCAMBIO);
        this.ownOffer.setStato(Offerta.StatoOfferta.IN_SCAMBIO);
    }
}
