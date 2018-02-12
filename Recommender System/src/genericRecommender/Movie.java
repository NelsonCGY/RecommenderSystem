package genericRecommender;

import java.util.*;

/**
 * @author CNelson A class for the movie, contains the movie's name, ID, its
 *         average rate, a map of the movie user and rate it received and a list
 *         of other genres.
 *
 */
public class Movie implements ItemInterface<Integer, Integer> {

	private String name;
	private int ID;
	private double avgRate;
	private HashMap<Integer, Double> map;
	private ArrayList<String> other;

	/**
	 * Constructor for a movie.
	 * 
	 * @param name
	 * @param ID
	 */
	public Movie(String name, int ID) {
		this.name = name;
		this.ID = ID;
		map = new HashMap<Integer, Double>();
		other = new ArrayList<String>();
	}

	/**
	 * @return the movie's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the movie's ID
	 */
	public Integer getID() {
		return ID;
	}

	/**
	 * @return the movie's average rate received
	 */
	public double getAvg() {
		return avgRate;
	}

	/**
	 * @return a copy of the map of the movie user and rate
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
	 * Add a movie user and rate to map.
	 * 
	 * @param id
	 * @param rate
	 */
	public void addRate(Integer id, double rate) {
		map.put(id, rate);
	}

	/**
	 * Remove a movie user and rate from map.
	 * 
	 * @param id
	 */
	public void removeRate(Integer id) {
		if (map.containsKey(id)) {
			map.remove(id);
		}
	}

	/**
	 * @param id
	 * @return if the map contains the movie user
	 */
	public boolean contains(Integer id) {
		if (map.containsKey(id)) {
			return true;
		}
		return false;
	}

	/**
	 * @param id
	 * @return the movie user's rate if exists
	 */
	public double getRate(Integer id) {
		if (map.containsKey(id)) {
			return map.get(id);
		}
		return 0;
	}

	/**
	 * Calculate the average rate from all movie users.
	 */
	public void setAvg() {
		double total = 0.0;
		int count = 0;
		Iterator<Map.Entry<Integer, Double>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Double> entry = iter.next();
			Double val = (Double) entry.getValue();
			total += val;
			count++;
		}
		count = map.size() == 0 ? 1 : map.size();
		this.avgRate = total / ((double) count);
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
	 * @return the string of the movie if exists
	 */
	public String getStr(int index) {
		if (other.contains(index)) {
			return other.get(index);
		}
		return null;
	}
}
