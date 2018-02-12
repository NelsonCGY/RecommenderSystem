package genericRecommender;

import java.util.*;

/************************************************************************************************************************/
/****** Be sure to change "Run Configurations -> Common -> Encoding" to UTF-8 in Eclipse to ensure correct output. ******/
/************************************************************************************************************************/

/**
 * @author CNelson The main class for Recommender System. Asks user to choose
 *         whether to show the prediction for an item or to show the first n
 *         recommendations.
 */
public class Main {

	/**
	 * Main method of the recommender system. Takes at least five or nine
	 * arguments, first is to choose what item data (can be movie, book or
	 * both), second is the movie data, third is the movie user data, forth is
	 * movie rating data and fifth is the size of neighbors. If there is also
	 * book data then add the arguments follow like movie. If there is other
	 * data then as well. Choose 1 or 2 to make prediction or get
	 * recommendations. Choose s to switch data if has two or more data and q to
	 * quit. Input user id and item id and choose calculation method to get
	 * prediction or input user id and size to get recommendations.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		RecFactory recsys = RecFactory.getRecFactory(args);
		Scanner sc = new Scanner(System.in);

		System.out.println("\n\nWelcome to Recommender System!");
		while (true) {
			System.out.printf("\nUsing %s data.\n\n",
					(recsys.dataNow == 0) ? "movie" : (recsys.dataNow == 1) ? "book" : "Error");
			System.out.printf(
					"Please choose a function:\n1. Prediction      2. Recommendation%s      q. exit\nInput (1/2%s/q):\n",
					(recsys.canSwitch) ? "      s. switch data" : "", (recsys.canSwitch) ? "/s" : "");
			String c = sc.nextLine();
			if (c.equals("q")) {
				System.out.println("\nExit system.");
				break;
			}
			if (c.equals("s")) {
				recsys.switchMB();
				continue;
			}
			int f;
			try {
				f = Integer.parseInt(c);
			} catch (NumberFormatException e) {
				System.out.println("\nInvalid input!");
				continue;
			}
			if (f != 1 && f != 2) {
				System.out.println("\nInvalid input!");
				continue;
			}
			int choose = 1;
			while (true) {
				System.out.println(
						"\nPlease choose the prediction method:\n1. Pearson     2. Cosine      3. Baseline      b. back\nInput (1/2/3/b):");
				c = sc.nextLine();
				if (c.equals("b")) {
					break;
				}
				try {
					choose = Integer.parseInt(c);
				} catch (NumberFormatException e) {
					System.out.println("\nInvalid input!");
					continue;
				}
				if (choose != 1 && choose != 2 && choose != 3) {
					System.out.println("\nInvalid input!");
					continue;
				}
				break;
			}
			if (c.equals("b")) {
				continue;
			}
			if (f == 1) {
				String idU, idI;
				while (!recsys.predicted) {
					System.out.println("\nPlease input the user id and item id or b to go back (idU,idI/b): ");
					c = sc.nextLine();
					if (c.equals("b")) {
						break;
					}
					String[] cs = c.split(",| ");
					if (cs.length < 2) {
						System.out.println("\nInvalid input!");
						continue;
					}
					idU = cs[0];
					idI = cs[1];
					recsys.predict(idU, idI, choose);
				}
				recsys.predicted = false;
			} else if (f == 2) {
				String idU;
				int n;
				while (!recsys.recommmended) {
					System.out.println(
							"\nPlease input the user id and recommendation size or b to go back (idU,size/b): ");
					c = sc.nextLine();
					if (c.equals("b")) {
						break;
					}
					String[] cs = c.split(",| ");
					if (cs.length < 2) {
						System.out.println("\nInvalid input!");
						continue;
					}
					try {
						idU = cs[0];
						n = Integer.parseInt(cs[1]);
					} catch (NumberFormatException e) {
						System.out.println("\nInvalid input!");
						continue;
					}
					System.out.println("\nFinding recommendations...");
					recsys.recommend(idU, n, choose);
				}
				recsys.recommmended = false;
			}
		}

		sc.close();
	}

}
