package genericRecommender;

import java.util.*;

/**
 * @author CNelson A generic interface for the item.
 *
 * @param <I>
 * @param <U>
 */
public interface ItemInterface<I, U> {

	/**
	 * @return the item's name
	 */
	public String getName();

	/**
	 * @return the item's ID
	 */
	public I getID();

	/**
	 * @return the item's average rate received
	 */
	public double getAvg();

	/**
	 * @return a copy of the map of the user and rate
	 */
	public HashMap<U, Double> getMap();

	/**
	 * @return the size of the map
	 */
	public int getMapSize();

	/**
	 * Add a user and rate to map.
	 * 
	 * @param id
	 * @param rate
	 */
	public void addRate(U id, double rate);

	/**
	 * Remove a user and rate from map.
	 * 
	 * @param id
	 */
	public void removeRate(U id);

	/**
	 * @param id
	 * @return if the map contains the user
	 */
	public boolean contains(U id);

	/**
	 * @param id
	 * @return the user's rate if exists
	 */
	public double getRate(U id);

	/**
	 * Calculate the average rate from all users.
	 */
	public void setAvg();

}
