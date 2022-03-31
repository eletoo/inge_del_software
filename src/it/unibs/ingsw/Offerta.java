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

    public Offerta(String name, Foglia categoria, Fruitore proprietario, StatoOfferta stato) {
        this.name = name;
        this.categoria = categoria;
        this.proprietario = proprietario;
        this.stato = stato;
    }

    public StatoOfferta getStato() {
        return stato;
    }

    public void setStato(StatoOfferta stato) {
        this.stato = stato;
    }

    public String getName() {
        return name;
    }

    public Foglia getCategoria() {
        return categoria;
    }

    public Fruitore getProprietario() {
        return proprietario;
    }

    public Map<String, Object> getValoreCampi() {
        return valoreCampi;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Offerta "+ name);
        sb.append("\n\tCategoria > " + categoria.toShortString());
        sb.append("\n\tProprietario > " + proprietario.getUsername());
        sb.append("\n\tStato > " + stato);
        sb.append("\n\tCampi > " );
        for (var valCampo: valoreCampi.entrySet()) {
            sb.append("\n\t\t"+valCampo.getKey() + "> "+ valCampo.getValue());
        }
        sb.append("\n");
        return sb.toString();
    }

    public static enum StatoOfferta {
        APERTA,
        RITIRATA
    }

    public static void viewOffersByCategory(@NotNull Applicazione app, View view){
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

    public static void viewPersonalOffers(Fruitore fruitore, @NotNull Applicazione app, @NotNull View view){
        view.showList(app.getOfferte(fruitore));
    }

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

    public static void createOffer(@NotNull Applicazione app, View view, Fruitore fruitore) throws IOException {
        if (app.getHierarchies().isEmpty())
            view.errorMessage(View.ErrorMessage.E_NO_CATEGORIES);
        else {
            var cat = chooseLeaf("Selezionare una categoria dove pubblicare l'offerta", view, app);
            var offer = new Offerta(view.inLine("Nome: "), cat, fruitore, Offerta.StatoOfferta.APERTA);
            for (var field : cat.getCampiNativi().entrySet()) {
                if (field.getValue().isObbligatorio())
                    inputField(offer, field, view);
                else if (view.yesOrNoQuestion("Configurare un valore per " + field.getKey() + "? [Y/N]").equalsIgnoreCase("y"))
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

    private static void inputField(@NotNull Offerta offer, Map.@NotNull Entry<String, CampoNativo> field, @NotNull View view) {
        offer.getValoreCampi()
                .put(
                        field.getKey(),
                        field.getValue().getType().deserialize(
                                view.inLine("Valore per " + field.getKey() + (field.getValue().isObbligatorio() ? " (Obbligatorio) " : ""))
                        )
                );
    }

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
