package genericRecommender;

import java.io.*;
import java.util.*;

/**
 * @author CNelson A class that reads file and parses data for BookRecommender
 *         class.
 *
 */
public class BookFile implements FileInterface<String, Book, Integer, BookUser> {

	private HashMap<String, Book> findItem;
	private HashMap<Integer, BookUser> findUser;
	private static BookFile data;
	private double overallAvg;

	/**
	 * @param itemF
	 * @param userF
	 * @param rateF
	 * @return the singleton of BookFile instance
	 * @throws IOException
	 */
	public static BookFile getFile(BufferedReader itemF, BufferedReader userF, BufferedReader rateF)
			throws IOException {
		if (data == null) {
			data = new BookFile(itemF, userF, rateF);
		}
		return data;
	}

	/**
	 * Constructor for the BookFile that takes three file BufferedReader and
	 * parse them.
	 * 
	 * @param itemIDList
	 * @param userIDList
	 * @param rateList
	 * @throws IOException
	 */
	private BookFile(BufferedReader itemF, BufferedReader userF, BufferedReader rateF) throws IOException {
		findItem = new HashMap<String, Book>();
		findUser = new HashMap<Integer, BookUser>();
		parseItem(itemF);
		parseUser(userF);
		parseRate(rateF);
		System.out.printf("Total item: %d (with anonymous item)\n", findItem.size());
		System.out.printf("Total user: %d (with no info user)\n", findUser.size());
	}

	/**
	 * @return the book ID to object map
	 */
	public HashMap<String, Book> getItemMap() {
		return findItem;
	}

	/**
	 * @return the book user ID to object map
	 */
	public HashMap<Integer, BookUser> getUserMap() {
		return findUser;
	}

	/**
	 * @return total number of books
	 */
	public int getTotalI() {
		return findItem.size();
	}

	/**
	 * @return total number of book users
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
	 * Take an book BufferedReader and parse the ID and book name to Book
	 * object.
	 * 
	 * @param itemIDList
	 * @throws IOException
	 */
	private void parseItem(BufferedReader itemF) throws IOException {
		String one;
		itemF.readLine();
		while ((one = itemF.readLine()) != null) {
			String[] data = one.split("\"|;|\\\\");
			int idx = 0;
			while (idx < data.length && data[idx].length() == 0) {
				idx++;
			}
			String idI = data[idx++];
			while (idx < data.length && data[idx].length() == 0) {
				idx++;
			}
			String name = data[idx++];
			Book newItem = new Book(name, idI);
			for (; idx < data.length; idx++) {
				if (data[idx].length() == 0) {
					continue;
				}
				newItem.addOther(data[idx]);
			}
			findItem.put(idI, newItem);
		}
		itemF.close();
	}

	/**
	 * Take a book user BufferedReader and parse the ID to BookUser object.
	 * 
	 * @param userIDList
	 * @throws IOException
	 */
	private void parseUser(BufferedReader userF) throws IOException {
		String one;
		userF.readLine();
		while ((one = userF.readLine()) != null) {
			String[] data = one.split("\"|;|\\\\");
			int idx = 0;
			int idU;
			while (idx < data.length && data[idx].length() == 0) {
				idx++;
			}
			try {
				idU = Integer.parseInt(data[idx++]);
			} catch (NumberFormatException e) {
				continue;
			}
			if (!findUser.containsKey(idU)) {
				BookUser newUser = new BookUser(idU);
				findUser.put(idU, newUser);
			}
			BookUser u = findUser.get(idU);
			for (; idx < data.length; idx++) {
				if (data[idx].length() == 0) {
					continue;
				}
				u.addOther(data[idx]);
			}
		}
		userF.close();
	}

	/**
	 * Take a rate BufferedReader and parse the rate into Book and BookUser's
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
			String[] data = one.split("\"|;|\\\\");
			int idx = 0;
			int idU;
			double rate;
			while (idx < data.length && data[idx].length() == 0) {
				idx++;
			}
			try {
				idU = Integer.parseInt(data[idx++]);
			} catch (NumberFormatException e) {
				continue;
			}
			while (idx < data.length && data[idx].length() == 0) {
				idx++;
			}
			String idI = data[idx++];
			while (idx < data.length && data[idx].length() == 0) {
				idx++;
			}
			try {
				rate = Double.parseDouble(data[idx++]);
			} catch (NumberFormatException e) {
				continue;
			}
			while (!findUser.containsKey(idU) || !findItem.containsKey(idI)) {
				if (!findUser.containsKey(idU)) {
					BookUser newUser = new BookUser(idU);
					findUser.put(idU, newUser);
				} else if (!findItem.containsKey(idI)) {
					Book newItem = new Book("No name", idI);
					findItem.put(idI, newItem);
				}
			}
			BookUser u = findUser.get(idU);
			Book i = findItem.get(idI);
			u.addScore(idI, rate);
			i.addRate(idU, rate);
			total += rate;
			count++;
		}
		rateF.close();
		overallAvg = (count == 0) ? 0.0 : total / (double) count;

		Iterator<Map.Entry<String, Book>> iterI = findItem.entrySet().iterator();
		while (iterI.hasNext()) {
			Map.Entry<String, Book> entry = iterI.next();
			Book val = (Book) entry.getValue();
			val.setAvg();
		}
		Iterator<Map.Entry<Integer, BookUser>> iterU = findUser.entrySet().iterator();
		while (iterU.hasNext()) {
			Map.Entry<Integer, BookUser> entry = iterU.next();
			BookUser val = (BookUser) entry.getValue();
			val.setAvg();
		}

		// Iterator<Map.Entry<String, Book>> iter =
		// findItem.entrySet().iterator();
		// while (iter.hasNext()) {
		// Map.Entry<String, Book> entry = iter.next();
		// String key = (String) entry.getKey();
		// Book val = (Book) entry.getValue();
		// System.out.printf("Get item: ID: %s avg: %.2f\n", key, val.getAvg());
		// }
		// Iterator<Map.Entry<Integer, BookUser>> iter2 =
		// findUser.entrySet().iterator();
		// while (iter2.hasNext()) {
		// Map.Entry<Integer, BookUser> entry = iter2.next();
		// Integer key = (Integer) entry.getKey();
		// BookUser val = (BookUser) entry.getValue();
		// System.out.printf("Get user: ID: %d avg: %.2f\n", key, val.getAvg());
		// }
	}
}
