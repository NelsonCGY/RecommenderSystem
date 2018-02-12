package genericRecommender;

import java.util.*;

/**
 * @author CNelson A class for the movie user, contains the user's ID, his
 *         average score and a map of the movie and score he gave and a map of
 *         other tags.
 *
 */
public class MovieUser implements UserInterface<Integer, Integer> {

	private int ID;
	private double avgScore;
	private HashMap<Integer, Double> map;
	private HashMap<Integer, String> other;

	/**
	 * Constructor for a movie user.
	 * 
	 * @param ID
	 */
	public MovieUser(int ID) {
		this.ID = ID;
		map = new HashMap<Integer, Double>();
		other = new HashMap<Integer, String>();
	}

	/**
	 * @return the movie user's ID
	 */
	public Integer getID() {
		return ID;
	}

	/**
	 * @return the movie user's average score gave
	 */
	public double getAvg() {
		return avgScore;
	}

	/**
	 * @return a copy of the map of the movie and score
	 */
	public HashMap<Integer, Double> getMap() {
		HashMap<Integer, Double> res = new HashMap<Integer, Double>();
		Iterator<Map.Entry<Integer, Double>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Double> entry = iter.next();
			Integer key = (Integer) entry.getKey();
			Double val = (Double) entry.getValue();
			res.put(key, val);
		}
		return res;
	}

	/**
	 * @return the size of the map
	 */
	public int getMapSize() {
		return map.size();
	}

	/**
	 * Add a movie and its score to map.
	 * 
	 * @param id
	 * @param score
	 */
	public void addScore(Integer id, double score) {
		map.put(id, score);
	}

	/**
	 * Remove a movie and score from map.
	 * 
	 * @param id
	 */
	public void removeScore(Integer id) {
		if (map.containsKey(id)) {
			map.remove(id);
		}
	}

	/**
	 * @param id
	 * @return if the map contains the movie
	 */
	public boolean contains(Integer id) {
		if (map.containsKey(id)) {
			return true;
		}
		return false;
	}

	/**
	 * @param id
	 * @return the movie's score if exists
	 */
	public double getScore(Integer id) {
		if (map.containsKey(id)) {
			return map.get(id);
		}
		return 0;
	}

	/**
	 * Calculate the average score to all movies.
	 */
	public void setAvg() {
		double total = 0.0;
		int count = 0;
		Iterator<Map.Entry<Integer, Double>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Double> entry = iter.next();
			Double val = (Double) entry.getValue();
			total += val;
		}
		count = map.size() == 0 ? 1 : map.size();
		this.avgScore = total / ((double) count);
	}

	/**
	 * @return a copy of the other map
	 */
	public HashMap<Integer, String> getOther() {
		HashMap<Integer, String> res = new HashMap<Integer, String>();
		Iterator<Map.Entry<Integer, String>> iter = other.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, String> entry = iter.next();
			Integer key = (Integer) entry.getKey();
			String val = (String) entry.getValue();
			res.put(key, val);
		}
		return res;
	}

	/**
	 * @return the size of the other map
	 */
	public int getOtherSize() {
		return other.size();
	}

	/**
	 * Add a movie and its tag to the other map.
	 * 
	 * @param tag
	 */
	public void addOther(Integer id, String tag) {
		other.put(id, tag);
	}

	/**
	 * Remove a movie and its tag from other map by id.
	 * 
	 * @param id
	 */
	public void removeOther(Integer id) {
		if (other.containsKey(id)) {
			other.remove(id);
		}
	}

	/**
	 * @param id
	 * @return the tag of the movie if exists
	 */
	public String getTag(Integer id) {
		if (other.containsKey(id)) {
			return other.get(id);
		}
		return null;
	}
}
