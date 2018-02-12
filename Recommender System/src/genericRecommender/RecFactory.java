package genericRecommender;

import java.io.*;
import java.util.ArrayList;

/**
 * @author CNelson A Factory class that deals with different kinds of data.
 *         Using the File and Recommender class implemented from interface.
 *
 */
public class RecFactory {

	public BufferedReader movieF, bookF;
	public BufferedReader movieUserF, bookUserF;
	public BufferedReader movieRateF, bookRateF;
	public MovieRecommender movieRec;
	public MovieFile movieData;
	public BookRecommender bookRec;
	public BookFile bookData;
	private static RecFactory recF;
	private int movieNeighbor, bookNeighbor;
	private boolean canMovie, canBook;
	private int dataNumber;
	public int dataNow; // 0 for movie data, 1 for book data
	public boolean canSwitch, predicted, recommmended;
	public long start, end;

	/**
	 * @param args
	 * @return the singleton of RecFactory instance
	 */
	public static RecFactory getRecFactory(String[] args) {
		if (recF == null) {
			recF = new RecFactory(args);
		}
		return recF;
	}

	/**
	 * A constructor that takes the file names and parse them to File instance.
	 * 
	 * @param args
	 */
	private RecFactory(String[] args) {
		if (!checkArgs(args)) {
			System.exit(1);
		}
		if (canMovie) {
			/* parse movie files */
			dataNumber++;
			try {
				System.out.println("\nLoading movie data to system...");
				start = System.currentTimeMillis();
				movieData = MovieFile.getFile(movieF, movieUserF, movieRateF);
				end = System.currentTimeMillis();
				System.out.printf("Parsing movie data takes %d ms.\n", end - start);
				movieRec = MovieRecommender.getRecommender(movieData.getItemMap(), movieData.getUserMap(),
						movieNeighbor, movieData.getAvg());
			} catch (IOException e) {
				System.out.println(e);
				System.exit(1);
			}
		}
		if (canBook) {
			/* parse book files */
			dataNumber++;
			try {
				System.out.println("\nLoading book data to system...");
				start = System.currentTimeMillis();
				bookData = BookFile.getFile(bookF, bookUserF, bookRateF);
				end = System.currentTimeMillis();
				System.out.printf("Parsing book data takes %d ms.\n", end - start);
				bookRec = BookRecommender.getRecommender(bookData.getItemMap(), bookData.getUserMap(), bookNeighbor,
						bookData.getAvg());
			} catch (IOException e) {
				System.out.println(e);
				System.exit(1);
			}
		}
		canSwitch = args[0].equals("all") ? true : false;
	}

	/**
	 * @param args
	 * @return if there are three arguments and all files exist
	 */
	public boolean checkArgs(String[] args) {
		if (!((args[0].equals("movie") || args[0].equals("book") && args.length >= 5)
				|| (args[0].equals("all") && args.length == 9))) {
			System.out.println("Invalid number of arguments! Please input valid arguments for " + args[0]);
			return false;
		}
		if (args[0].equals("movie") || args[0].equals("all")) {
			/* check movie files */
			try {
				FileInputStream fis = new FileInputStream(args[1]);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				movieF = new BufferedReader(isr);
			} catch (IOException e) {
				System.out.println("Invalid item data file!");
				return false;
			}
			try {
				FileInputStream fis = new FileInputStream(args[2]);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				movieUserF = new BufferedReader(isr);
			} catch (IOException e) {
				System.out.println("Invalid user data file!");
				return false;
			}
			try {
				FileInputStream fis = new FileInputStream(args[3]);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				movieRateF = new BufferedReader(isr);
			} catch (IOException e) {
				System.out.println("Invalid rate data file!");
				return false;
			}
			movieNeighbor = Integer.parseInt(args[4]);
			System.out.printf("Movie file loaded: %s   %s   %s\nNeighbor size: %d\n", args[1], args[2], args[3],
					movieNeighbor);
			canMovie = true;
			dataNow = 0;
		}
		if (args[0].equals("book") || args[0].equals("all")) {
			/* check book files */
			int idx = args[0].equals("all") ? 5 : 1;
			try {
				FileInputStream fis = new FileInputStream(args[idx]);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				bookF = new BufferedReader(isr);
			} catch (IOException e) {
				System.out.println("Invalid item data file!");
				return false;
			}
			try {
				FileInputStream fis = new FileInputStream(args[idx + 1]);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				bookUserF = new BufferedReader(isr);
			} catch (IOException e) {
				System.out.println("Invalid user data file!");
				return false;
			}
			try {
				FileInputStream fis = new FileInputStream(args[idx + 2]);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				bookRateF = new BufferedReader(isr);
			} catch (IOException e) {
				System.out.println("Invalid rate data file!");
				return false;
			}
			bookNeighbor = Integer.parseInt(args[idx + 3]);
			System.out.printf("Book file loaded: %s   %s   %s\nNeighbor size: %d\n", args[idx], args[idx + 1],
					args[idx + 2], bookNeighbor);
			canBook = true;
			dataNow = 1;
		}
		return true;
	}

	/**
	 * Used to switch between several data.
	 */
	public void switchMB() {
		if (canSwitch) {
			dataNow = (dataNow + 1) % dataNumber;
			System.out.printf("\nSwitch to %s data.\n", (dataNow == 0) ? "movie" : (dataNow == 1) ? "book" : "Error");
		} else {
			System.out.printf("\nCan't switch! Only %s data available.\n",
					(dataNow == 0) ? "movie" : (dataNow == 1) ? "book" : "Error");
		}
	}

	/**
	 * Calculate the prediction of an item to a user.
	 * 
	 * @param idU
	 * @param idI
	 */
	public double predict(String idU, String idI, int choose) {
		predicted = false;
		double P = 0.0;
		String kind = (choose == 1) ? "Pearson" : (choose == 2) ? "Cosine" : "Baseline";

		if (dataNow == 0) {
			/* prediction for movie data */
			int idUM, idIM;
			try {
				idUM = Integer.parseInt(idU);
				idIM = Integer.parseInt(idI);
			} catch (NumberFormatException e) {
				System.out.println("\nInvalid input!\n");
				return P;
			}
			if (!movieData.getItemMap().containsKey(idIM)) {
				System.out.println("\nInvalid item id input!\n");
				return P;
			}
			if (!movieData.getUserMap().containsKey(idUM)) {
				System.out.println("\nInvalid user id input!\n");
				return P;
			}
			start = System.currentTimeMillis();
			if (choose == 3) {
				P = movieRec.predictionB(idUM, idIM);
			} else {
				P = movieRec.predictionRMSE(idUM, idIM, choose);
			}
			end = System.currentTimeMillis();
			System.out.printf("\nThe %s prediction of movie id %s for user id %s is: %f\n", kind, idI, idU, P);
		} else if (dataNow == 1) {
			/* prediction for book data */
			int idUB;
			try {
				idUB = Integer.parseInt(idU);
			} catch (NumberFormatException e) {
				System.out.println("\nInvalid input!\n");
				return P;
			}
			if (!bookData.getItemMap().containsKey(idI)) {
				System.out.println("\nInvalid item id input!\n");
				return P;
			}
			if (!bookData.getUserMap().containsKey(idUB)) {
				System.out.println("\nInvalid user id input!\n");
				return P;
			}
			start = System.currentTimeMillis();
			if (choose == 3) {
				P = bookRec.predictionB(idUB, idI);
			} else {
				P = bookRec.predictionRMSE(idUB, idI, choose);
			}
			end = System.currentTimeMillis();
			System.out.printf("\nThe %s prediction of book id %s for user id %s is: %f\n", kind, idI, idU, P);
		}

		System.out.printf("\nCalculating recommendation takes %d ms.\n", end - start);
		predicted = true;
		return P;
	}

	/**
	 * Get n recommendations for a user.
	 * 
	 * @param idU
	 * @param n
	 */
	public ArrayList<String> recommend(String idU, int n, int choose) {
		recommmended = false;
		String kind = (choose == 1) ? "Pearson" : (choose == 2) ? "Cosine" : "Baseline";
		int idUMB;
		try {
			idUMB = Integer.parseInt(idU);
		} catch (NumberFormatException e) {
			System.out.println("\nInvalid input!\n");
			return null;
		}
		ArrayList<String> res = new ArrayList<String>();

		if (dataNow == 0) {
			/* recommendation for movie data */
			if (!movieData.getUserMap().containsKey(idUMB)) {
				System.out.println("\nInvalid user id input!\n");
				return null;
			}
			if (n > movieData.getItemMap().size() || n < 0) {
				System.out.println("\nInvalid size input!\n");
				return null;
			}
			start = System.currentTimeMillis();
			res = movieRec.recommendation(idUMB, n, choose);
			end = System.currentTimeMillis();
			System.out.printf("\nRecommendations for movie user id %s by %s:\n", idU, kind);
		} else if (dataNow == 1) {
			/* prediction for book data */
			if (!bookData.getUserMap().containsKey(idUMB)) {
				System.out.println("\nInvalid user id input!\n");
				return null;
			}
			if (n > bookData.getItemMap().size() || n < 0) {
				System.out.println("\nInvalid size input!\n");
				return null;
			}
			start = System.currentTimeMillis();
			res = bookRec.recommendation(idUMB, n, choose);
			end = System.currentTimeMillis();
			System.out.printf("\nRecommendations for book user id %s by %s:\n", idU, kind);
		}

		for (int i = 0; i < res.size(); i++) {
			System.out.printf("%d. ", i + 1);
			System.out.println(res.get(i));
		}
		System.out.printf("\nCalculating recommendation takes %d ms.\n", end - start);
		System.out.println();
		recommmended = true;
		return res;
	}
}
