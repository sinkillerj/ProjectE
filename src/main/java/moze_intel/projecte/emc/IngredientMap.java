package moze_intel.projecte.emc;

import java.util.HashMap;
import java.util.Map;

public class IngredientMap<T> {
    protected HashMap<T, Integer> ingredientsWithAmount = new HashMap<T, Integer>();
    public void addIngredient(T stackNorm, int amount) {
        int count = amount;
        if (ingredientsWithAmount.containsKey(stackNorm)) {
            count += ingredientsWithAmount.get(stackNorm);
        }
        ingredientsWithAmount.put(stackNorm, count);
    }

    public Map<T, Integer> getMap() {
        return new HashMap<T,Integer>(ingredientsWithAmount);
    }

    public String toString() {
        return ingredientsWithAmount.toString();
    }
}
