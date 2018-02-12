package genericRecommender;

import java.util.*;

/**
 * @author CNelson A generic interface for files.
 *
 * @param <I>
 * @param <M>
 * @param <U>
 * @param <N>
 */
public interface FileInterface<I, M, U, N> {

	/**
	 * @return the item ID to object map
	 */
	public HashMap<I, M> getItemMap();

	/**
	 * @return the user ID to object map
	 */
	public HashMap<U, N> getUserMap();

	/**
	 * @return total number of items
	 */
	public int getTotalI();

	/**
	 * @return total number of users
	 */
	public int getTotalU();

	/**
	 * @return the overall average score
	 */
	public double getAvg();

}
