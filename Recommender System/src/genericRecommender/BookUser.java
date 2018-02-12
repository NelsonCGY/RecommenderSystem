package genericRecommender;

import java.util.*;

/**
 * @author CNelson A class for the book user, contains the user's ID, his
 *         average score and a map of the book and score he gave and a list of
 *         other tags.
 *
 */
public class BookUser implements UserInterface<Integer, String> {

	private int ID;
	private double avgScore;
	private HashMap<String, Double> map;
	private ArrayList<String> other;

	/**
	 * Constructor for a book user.
	 * 
	 * @param ID
	 */
	public BookUser(int ID) {
		this.ID = ID;
		map = new HashMap<String, Double>();
		other = new ArrayList<String>();
	}

	/**
	 * @return the book user's ID
	 */
	public Integer getID() {
		return ID;
	}

	/**
	 * @return the book user's average score gave
	 */
	public double getAvg() {
		return avgScore;
	}

	/**
	 * @return a copy of the map of the book and score
	 */
	public HashMap<String, Double> getMap() {
		HashMap<String, Double> res = new HashMap<String, Double>();
		Iterator<Map.Entry<String, Double>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Double> entry = iter.next();
			String key = (String) entry.getKey();
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
	 * Add a book and its score to map.
	 * 
	 * @param id
	 * @param score
	 */
	public void addScore(String id, double score) {
		map.put(id, score);
	}

	/**
	 * Remove a book and score from map.
	 * 
	 * @param id
	 */
	public void removeScore(String id) {
		if (map.containsKey(id)) {
			map.remove(id);
		}
	}

	/**
	 * @param id
	 * @return if the map contains the book
	 */
	public boolean contains(String id) {
		if (map.containsKey(id)) {
			return true;
		}
		return false;
	}

	/**
	 * @param id
	 * @return the book's score if exists
	 */
	public double getScore(String id) {
		if (map.containsKey(id)) {
			return map.get(id);
		}
		return 0;
	}

	/**
	 * Calculate the average score to all books.
	 */
	public void setAvg() {
		double total = 0.0;
		int count = 0;
		Iterator<Map.Entry<String, Double>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Double> entry = iter.next();
			Double val = (Double) entry.getValue();
			total += val;
		}
		count = map.size() == 0 ? 1 : map.size();
		this.avgScore = total / ((double) count);
	}

	/**
	 * @return a copy of the other list
	 */
	public ArrayList<String> getOther() {
		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0; i < other.size(); i++) {
			res.add(other.get(i));
		}
		return res;
	}

	/**
	 * @return the size of the other list
	 */
	public int getOtherSize() {
		return other.size();
	}

	/**
	 * Add a string to the other list.
	 * 
	 * @param str
	 */
	public void addOther(String str) {
		other.add(str);
	}

	/**
	 * Remove a string from other.
	 * 
	 * @param str
	 */
	public void removeOther(String str) {
		if (other.contains(str)) {
			other.remove(str);
		}
	}

	/**
	 * @param index
	 * @return the string of the book user if exists
	 */
	public String getStr(int index) {
		if (other.contains(index)) {
			return other.get(index);
		}
		return null;
	}
}
