package tran.allrecipes.presentation.model;

/**
 * A representation of an ingredient in a pantry list.
 * @author Todd
 */
public class PantryIngredient extends Ingredient {
	/** The threshold's whole number portion.*/
	private String thresholdWholeNumber;
	/** The threshold's numerator value.*/
	private String thresholdNumerator;
	/** The threshold's denominator value.*/
	private String thresholdDenominator;
	/** A flag to determine if the ingredient can still be moved to a shopping list after an update in quantity. */
	private boolean canIngredientBeAddedToShoppingList;
	/** A flag to indicate if the ingredient threshold has been hit. */
	private boolean hasIngredientThresholdBeenHit;
	
	public PantryIngredient() {
		// TODO Auto-generated constructor stub
	}

	public String getThresholdWholeNumber() {
		return thresholdWholeNumber;
	}

	public void setThresholdWholeNumber(String thresholdWholeNumber) {
		this.thresholdWholeNumber = thresholdWholeNumber;
	}

	public String getThresholdNumerator() {
		return thresholdNumerator;
	}

	public void setThresholdNumerator(String thresholdNumerator) {
		this.thresholdNumerator = thresholdNumerator;
	}

	public String getThresholdDenominator() {
		return thresholdDenominator;
	}

	public void setThresholdDenominator(String thresholdDenominator) {
		this.thresholdDenominator = thresholdDenominator;
	}
	
	public boolean canIngredientBeAddedToShoppingList() {
		return canIngredientBeAddedToShoppingList;
	}

	public void setCanIngredientBeAddedToShoppingList(boolean canIngredientBeAddedToShoppingList) {
		this.canIngredientBeAddedToShoppingList = canIngredientBeAddedToShoppingList;
	}
	
	public boolean isHasIngredientThresholdBeenHit() {
		return hasIngredientThresholdBeenHit;
	}

	public void setHasIngredientThresholdBeenHit(boolean hasIngredientThresholdBeenHit) {
		this.hasIngredientThresholdBeenHit = hasIngredientThresholdBeenHit;
	}
	
	public PantryIngredient(String wholeNumber, String numerator, String denominator) {
		super(wholeNumber, numerator, denominator);
	}
	
	public PantryIngredient(String ingredientName, String wholeNumber, String numerator, String denominator, String thresholdWholeNumber, String thresholdNumerator, String thresholdDenominator, 
			String ingredientUnit, Integer ingredientID, String ingredientType) {
		super(ingredientName, wholeNumber, numerator, denominator, ingredientUnit, ingredientID, ingredientType);
		this.thresholdWholeNumber = thresholdWholeNumber;
		this.thresholdNumerator = thresholdNumerator;
		this.thresholdDenominator = thresholdDenominator;
	}
	
	public PantryIngredient(String ingredientName, String wholeNumber, String numerator, String denominator, String thresholdWholeNumber, String thresholdNumerator, String thresholdDenominator, 
			String ingredientUnit, String ingredientType) {
		super(ingredientName, wholeNumber, numerator, denominator, ingredientUnit, ingredientType);
		this.thresholdWholeNumber = thresholdWholeNumber;
		this.thresholdNumerator = thresholdNumerator;
		this.thresholdDenominator = thresholdDenominator;
	}
	
	/**
	 * @return A string of the pantry ingredient's fields, this method is used for debugging purposes. 
	 */
	@Override
	public String toString() {
		return "ingredient information:\n" + "ingredient name: " + getIngredientName() + "\nwhole number: " + getWholeNumber() + "\nnumerator: " + getNumerator() + "\ndenominator: " + getDenominator() +
			"\nthreshold whole number: " + thresholdWholeNumber + "\nthreshold numerator: " + thresholdNumerator + "\nthreshold denominator: " + thresholdDenominator + "\ningredient unit: " + getIngredientUnit() + 
			"\ningredient id: " + getIngredientID() + "\ningredient type: " + getIngredientType();
	}
}
