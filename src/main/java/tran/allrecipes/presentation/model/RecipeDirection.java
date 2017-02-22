package tran.allrecipes.presentation.model;

/**
 * @author Todd
 * This class represents the directions for a recipe, defined by a data member.
 */
public class RecipeDirection {
	/** Detailed directions */
	private String direction; 
	/** stored to account for quotations. */
	private String directionsDelimited;
	/** The number of the direction (first, second, etc). */
	private int directionNumber;
	/** A unique identifier for each direction object. */
	private Integer directionId;
	/** For form binding, specifies what recipe this direction is a part of. */
	private String recipeName;

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	public String getDirectionsDelimited() {
		return directionsDelimited;
	}

	public void setDirectionsDelimited(String directionsDelimited) {
		this.directionsDelimited = directionsDelimited;
	}

	public int getDirectionNumber() {
		return directionNumber;
	}

	public void setDirectionNumber(int directionNumber) {
		this.directionNumber = directionNumber;
	}
	
	public Integer getDirectionId() {
		return directionId;
	}

	public void setDirectionId(Integer directionId) {
		this.directionId = directionId;
	}

	public RecipeDirection(String direction, int directionNumber) {
		super();
		this.direction = direction;
		this.directionNumber = directionNumber;
	}
	
	public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}
	
	public RecipeDirection() {
		// TODO Auto-generated constructor stub
	}
	
}
