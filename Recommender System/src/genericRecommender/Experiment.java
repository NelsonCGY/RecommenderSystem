package genericRecommender;

/**
 * @author CNelson A class that make some small experiments for three different
 *         methods of prediction. The accuracy is determined by the average
 *         percentage difference between the prediction of an item to a user and
 *         the user's actual rating to the item. The data is movie.
 */
public class Experiment {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		RecFactory recsys = RecFactory.getRecFactory(args);
		String idU[] = {"17", "306", "599", "48", "88", "460"}; // random samples of user
		String idM[] = {"7361", "1608", "6373", "6377", "8636", "2735"}; // random samples of item which is rated by user
		double rate[] = {4.5, 4.0, 3.5, 4.0, 3.0, 2.0}; // the actual rating of the item by the user
		
		/* make the prediction for the user */
		double[] pearson = new double[6], cosine = new double[6], baseline = new double[6];
		for(int i=0; i<6; i++){
			System.out.println("\n******Experiment #" + (i + 1));
			pearson[i] = recsys.predict(idU[i], idM[i], 1);
			cosine[i] = recsys.predict(idU[i], idM[i], 2);
			baseline[i] = recsys.predict(idU[i], idM[i], 3);
		}
		
		/* calculate the difference between prediction and actual rating */
		double pearsonR = 0.0, cosineR = 0.0, baselineR = 0.0;
		for(int i=0; i<6; i++){
			pearsonR += 1.0 - Math.abs(pearson[i] - rate[i]) / rate[i];
			cosineR += 1.0 - Math.abs(cosine[i] - rate[i]) / rate[i];
			baselineR += 1.0 - Math.abs(baseline[i] - rate[i]) / rate[i];
		}
		pearsonR /= 6.0 / 100.0;
		cosineR /= 6.0 / 100.0;
		baselineR /= 6.0 / 100.0;
		
		System.out.println("\n******Experiment result:");
		System.out.println("\nPearson accurary: " + pearsonR + "%");
		System.out.println("\nCosine accurary: " + cosineR + "%");
		System.out.println("\nBaseline accurary: " + baselineR + "%");
		System.out.println("\nSeems like Pearson works best.\nPearson focus more on calculating similarity"
				+ " by common items of two users while Cosine focus on calculating similarity by all the ratings"
				+ " of two users.\nThe result of Cosine similarity will be influenced by the amount of ratings a user gave.\n"
				+ "The accuracy of Baseline is a bit lower than Pearson and Cosine because it doesn't take similarity"
				+ " into consideration.\nThis may let some unsimilar user's rating affect the result.\nBut the running"
				+ " time of Baseline is much shorter if the average is calculated before.\nSo there should be a trade-off"
				+ " between accuracy and running time.");
	}

}
