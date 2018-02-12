package genericRecommender;

import java.io.*;
import java.util.*;

/**
 * @author CNelson A class that reads file and parses data for MovieRecommender
 *         class.
 *
 */
public class MovieFile implements FileInterface<Integer, Movie, Integer, MovieUser> {

	private HashMap<Integer, Movie> findItem;
	private HashMap<Integer, MovieUser> findUser;
	private static MovieFile data;
	private double overallAvg;

	/**
	 * @param itemF
	 * @param userF
	 * @param rateF
	 * @return the singleton of MovieFile instance
	 * @throws IOException
	 */
	public static MovieFile getFile(BufferedReader itemF, BufferedReader userF, BufferedReader rateF)
			throws IOException {
		if (data == null) {
			data = new MovieFile(itemF, userF, rateF);
		}
		return data;
	}

	/**
	 * Constructor for the MovieFile that takes three file BufferedReader and
	 * parse them.
	 * 
	 * @param itemIDList
	 * @param userIDList
	 * @param rateList
	 * @throws IOException
	 */
	private MovieFile(BufferedReader itemF, BufferedReader userF, BufferedReader rateF) throws IOException {
		findItem = new HashMap<Integer, Movie>();
		findUser = new HashMap<Integer, MovieUser>();
		parseItem(itemF);
		parseUser(userF);
		parseRate(rateF);
		System.out.printf("Total item: %d (with anonymous item)\n", findItem.size());
		System.out.printf("Total user: %d (with no info user)\n", findUser.size());
	}

	/**
	 * @return the movie ID to object map
	 */
	public HashMap<Integer, Movie> getItemMap() {
		return findItem;
	}

	/**
	 * @return the movie user ID to object map
	 */
	public HashMap<Integer, MovieUser> getUserMap() {
		return findUser;
	}

	/**
	 * @return total number of movies
	 */
	public int getTotalI() {
		return findItem.size();
	}

	/**
	 * @return total number of movie users
	 */
	public int getTotalU() {
		return findUser.size();
	}

	/**
	 * @return the overall average score
	 */
	public double getAvg() {
		return overallAvg;
	}

	/**
	 * Take an movie BufferedReader and parse the ID and movie name to Movie
	 * object.
	 * 
	 * @param itemIDList
	 * @throws IOException
	 */
	private void parseItem(BufferedReader itemF) throws IOException {
		String one;
		itemF.readLine();
		while ((one = itemF.readLine()) != null) {
			String[] data = one.split("::|,");
			int idI;
			try {
				idI = Integer.parseInt(data[0]);
			} catch (NumberFormatException e) {
				continue;
			}
			String name = data[1];
			Movie newItem = new Movie(name, idI);
			int idx = 2;
			while (idx < data.length) {
				String[] others = data[idx++].split("\\|");
				for (int i = 0; i < others.length; i++) {
					newItem.addOther(others[i]);
				}
			}
			findItem.put(idI, newItem);
		}
		itemF.close();
	}

	/**
	 * Take a movie user BufferedReader and parse the ID to MovieUser object.
	 * 
	 * @param userIDList
	 * @throws IOException
	 */
	private void parseUser(BufferedReader userF) throws IOException {
		String one;
		userF.readLine();
		while ((one = userF.readLine()) != null) {
			String[] data = one.split("::|,");
			int idU, idI;
			try {
				idU = Integer.parseInt(data[0]);
				idI = Integer.parseInt(data[1]);
			} catch (NumberFormatException e) {
				continue;
			}
			if (!findUser.containsKey(idU)) {
				MovieUser newUser = new MovieUser(idU);
				findUser.put(idU, newUser);
			}
			MovieUser u = findUser.get(idU);
			u.addOther(idI, data[2]);
		}
		userF.close();
	}

	/**
	 * Take a rate BufferedReader and parse the rate into Movie and MovieUser's
	 * HashMap, then calculate all the average data.
	 * 
	 * @param rateList
	 * @throws IOException
	 */
	private void parseRate(BufferedReader rateF) throws IOException {
		String one;
		double total = 0.0;
		int count = 0;
		rateF.readLine();
		while ((one = rateF.readLine()) != null) {
			String[] data = one.split("::|,");
			int idU, idI;
			double rate;
			try {
				idU = Integer.parseInt(data[0]);
				idI = Integer.parseInt(data[1]);
			} catch (NumberFormatException e) {
				continue;
			}
			try {
				rate = Double.parseDouble(data[2]);
			} catch (NumberFormatException e) {
				continue;
			}
			while (!findUser.containsKey(idU) || !findItem.containsKey(idI)) {
				if (!findUser.containsKey(idU)) {
					MovieUser newUser = new MovieUser(idU);
					findUser.put(idU, newUser);
				} else if (!findItem.containsKey(idI)) {
					Movie newItem = new Movie("No name", idI);
					findItem.put(idI, newItem);
				}
			}
			MovieUser u = findUser.get(idU);
			Movie i = findItem.get(idI);
			u.addScore(idI, rate);
			i.addRate(idU, rate);
			total += rate;
			count++;
		}
		rateF.close();
		overallAvg = (count == 0) ? 0.0 : total / (double) count;

		Iterator<Map.Entry<Integer, Movie>> iterI = findItem.entrySet().iterator();
		while (iterI.hasNext()) {
			Map.Entry<Integer, Movie> entry = iterI.next();
			Movie val = (Movie) entry.getValue();
			val.setAvg();
		}
		Iterator<Map.Entry<Integer, MovieUser>> iterU = findUser.entrySet().iterator();
		while (iterU.hasNext()) {
			Map.Entry<Integer, MovieUser> entry = iterU.next();
			MovieUser val = (MovieUser) entry.getValue();
			val.setAvg();
		}
	}
}
