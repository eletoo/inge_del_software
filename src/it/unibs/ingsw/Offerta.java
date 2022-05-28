package it.unibs.ingsw;

import it.unibs.ingsw.exceptions.RequiredConstraintFailureException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Offerta implements Serializable {

    private String name;
    private Foglia categoria;
    private Fruitore proprietario;
    private StatoOfferta stato;
    private Map<String, Object> valoreCampi = new HashMap<>();

    /**
     * Costruttore
     *
     * @param name         nome offerta
     * @param categoria    categoria a cui appartiene l'offerta
     * @param proprietario utente creatore dell'offerta
     * @param stato        stato
     */
    public Offerta(String name, Foglia categoria, Fruitore proprietario, StatoOfferta stato) {
        this.name = name;
        this.categoria = categoria;
        this.proprietario = proprietario;
        this.stato = stato;
    }

    /**
     * @return stato dell'offerta
     */
    public StatoOfferta getStato() {
        return stato;
    }

    /**
     * @param stato stato offerta
     */
    public void setStato(StatoOfferta stato) {
        this.stato = stato;
    }

    /**
     * @return nome offerta
     */
    public String getName() {
        return name;
    }

    /**
     * @return categoria foglia a cui appartiene l'offerta
     */
    public Foglia getCategoria() {
        return categoria;
    }

    /**
     *
     * @return proprietario dell'offerta
     */
    public Fruitore getProprietario() {
        return proprietario;
    }

    /**
     * @return valore dei campi dell'offerta
     */
    public Map<String, Object> getValoreCampi() {
        return valoreCampi;
    }

    /**
     * @return stringa contenente le informazioni relative a un'offerta
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Offerta " + name);
        sb.append("\n\tCategoria > " + categoria.toShortString());
        sb.append("\n\tProprietario > " + proprietario.getUsername());
        sb.append("\n\tStato > " + stato);
        sb.append("\n\tCampi > ");
        for (var valCampo : valoreCampi.entrySet()) {
            sb.append("\n\t\t" + valCampo.getKey() + "> " + valCampo.getValue());
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * StatoOfferta: enum che tiene traccia dello stato dell'offerta
     */
    public static enum StatoOfferta {
        APERTA,
        RITIRATA
    }

    /**
     * Crea una lista di offerte presenti nella categoria selezionata dall'utente da mostrare attraverso la View
     * @param app applicazione
     * @param view view
     */
    public static void viewOffersByCategory(@NotNull Applicazione app, View view) {
        if (app.getHierarchies().size() == 0)
            view.errorMessage(View.ErrorMessage.E_NO_CATEGORIES);
        else
            view.showList(
                    app.getOfferte(chooseLeaf("Scegliere una categoria della quale visualizzare le offerte disponibili", view, app))
                            .stream()
                            .filter(e -> e.getStato() == Offerta.StatoOfferta.APERTA)
                            .collect(Collectors.toList())
            );
    }

    /**
     * Mostra la lista di offerte personali relative a un utente
     * @param fruitore utente di cui visualizzare le offerte
     * @param app applicazione
     * @param view view
     */
    public static void viewPersonalOffers(Fruitore fruitore, @NotNull Applicazione app, @NotNull View view) {
        view.showList(app.getOfferte(fruitore));
    }

    /**
     * Permette all'utente di scegliere una categoria foglia da una gerarchia
     * @param prompt prompt da fornire all'utente
     * @param view view
     * @param app applicazione
     * @return categoria foglia selezionata dall'utente
     */
    private static Foglia chooseLeaf(String prompt, @NotNull View view, @NotNull Applicazione app) {
        view.message(prompt);
        List<Foglia> choices = new LinkedList<>();
        var stack = new Stack<Categoria>();
        for (var gerarchia : app.getHierarchies().entrySet()) {
            stack.clear();
            stack.push(gerarchia.getValue().getRoot());
            while (!stack.empty()) {
                var c = stack.pop();
                if (c instanceof Foglia)
                    choices.add((Foglia) c);
                else
                    stack.addAll(((Nodo) c).getCategorieFiglie());
            }
        }
        return (Foglia) view.choose(choices, Categoria::toShortString);
    }

    /**
     * Permette di creare un'offerta indicando la categoria foglia di appartenenza, il nome dell'offerta e il valore dei
     * campi nativi (obbligatori e facoltativi)
     * @param app applicazione
     * @param view view
     * @param fruitore utente fruitore
     * @throws IOException eccezione I/O
     */
    public static void createOffer(@NotNull Applicazione app, View view, Fruitore fruitore) throws IOException {
        if (app.getHierarchies().isEmpty())
            view.errorMessage(View.ErrorMessage.E_NO_CATEGORIES);
        else {
            var cat = chooseLeaf("Selezionare una categoria dove pubblicare l'offerta", view, app);
            var offer = new Offerta(view.inLine("Nome: "), cat, fruitore, Offerta.StatoOfferta.APERTA);
            for (var field : cat.getCampiNativi().entrySet()) {
                if (field.getValue().isObbligatorio())
                    inputField(offer, field, view);
                else if (view.yesOrNoQuestion("Configurare un valore per " + field.getKey() + "? [Y/N]"))
                    inputField(offer, field, view);
            }
            try {
                app.addOfferta(offer);
            } catch (RequiredConstraintFailureException e) {
                e.printStackTrace(); //should not happen
            }
            app.saveOfferte();
        }
    }

    /**
     * Permette l'inserimento del valore di un campo
     * @param offer offerta
     * @param field campo da compilare
     * @param view view
     */
    private static void inputField(@NotNull Offerta offer, Map.@NotNull Entry<String, CampoNativo> field, @NotNull View view) {
        offer.getValoreCampi()
                .put(
                        field.getKey(),
                        field.getValue().getType().deserialize(
                                view.inLine("Valore per " + field.getKey() + (field.getValue().isObbligatorio() ? " (Obbligatorio) " : ""))
                        )
                );
    }

    /**
     * Permette di selezionare un'offerta da ritirare e modificarne lo stato opportunamente
     * @param app applicazione
     * @param view view
     * @param fruitore fruitore
     * @throws IOException eccezione I/O
     */
    public static void undoOffer(@NotNull Applicazione app, View view, Fruitore fruitore) throws IOException {
        var user_offers = app.getOfferte(fruitore).stream().filter(e -> e.getStato() == Offerta.StatoOfferta.APERTA).collect(Collectors.toList());
        if (user_offers.isEmpty()) {
            view.errorMessage(View.ErrorMessage.E_NO_OFFERS);
        } else {
            var to_edit = (Offerta) view.choose(user_offers, null);
            to_edit.setStato(Offerta.StatoOfferta.RITIRATA);
            app.saveOfferte();
        }
    }
}
