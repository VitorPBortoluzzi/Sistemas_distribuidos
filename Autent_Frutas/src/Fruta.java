public class Fruta {
    private static final String[] frutas = {
        "Maçã", "Banana", "Laranja", "Manga", "Uva", "Morango", "Abacaxi", "Melancia"
    };

    public String sorteiaFruta() {
        // Sorteia uma fruta aleatória da lista
        int index = (int) (Math.random() * frutas.length);
        return frutas[index];
    }

    @Override
    public String toString() {
        return sorteiaFruta();
    }
}