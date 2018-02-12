package genericRecommender;

import java.util.*;

/**
 * @author CNelson A generic interface for the user.
 *
 * @param <U>
 * @param <I>
 */
public interface UserInterface<U, I> {

	/**
	 * @return the user's ID
	 */
	public U getID();

	/**
	 * @return the user's average score gave
	 */
	public double getAvg();

	/**
	 * @return a copy of the map of the item and score
	 */
	public HashMap<I, Double> getMap();

	/**
	 * @return the size of the map
	 */
	public int getMapSize();

	/**
	 * Add an item and its score to map.
	 * 
	 * @param id
	 * @param score
	 */
	public void addScore(I id, double score);

	/**
	 * Remove an item and score from map.
	 * 
	 * @param id
	 */
	public void removeScore(I id);

	/**
	 * @param id
	 * @return if the map contains the item
	 */
	public boolean contains(I id);

	/**
	 * @param id
	 * @return the item's score if exists
	 */
	public double getScore(I id);

	/**
	 * Calculate the average score to all items.
	 */
	public void setAvg();

}
