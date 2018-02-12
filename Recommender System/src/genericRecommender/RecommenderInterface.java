package genericRecommender;

import java.util.*;

/**
 * @author CNelson A generic interface for the recommender.
 *
 * @param <U>
 * @param <I>
 */
public interface RecommenderInterface<U, I> {

	/**
	 * @param idU
	 * @param idI
	 * @param choose
	 * @return the prediction P(u,i) of an item to a user by CF
	 */
	public double predictionRMSE(U idU, I idI, int choose);

	/**
	 * @param idU
	 * @param idI
	 * @return the prediction P(u,i) of an item to a user by Baseline
	 */
	public double predictionB(U idU, I idI);

	/**
	 * @param idU
	 * @param n
	 * @param choose
	 * @return a list of n recommendations
	 */
	public ArrayList<String> recommendation(U idU, int n, int choose);
}
