package genericRecommender;

import java.util.*;
import java.util.concurrent.*;

/**
 * Modified Comparator that compares scores for priority queue.
 */
class CompInt implements Comparator<IntPair> {

	@Override
	public int compare(IntPair a, IntPair b) {
		Double sa = a.score;
		Double sb = b.score;
		if (sa.compareTo(sb) > 0) {
			return 1;
		} else {
			return -1;
		}
	}
}

class CompIntP implements Comparator<IntPair> {

	@Override
	public int compare(IntPair a, IntPair b) {
		Double sa = a.score;
		Double sb = b.score;
		if (sa.compareTo(sb) < 0) {
			return 1;
		} else {
			return -1;
		}
	}
}

/**
 * @author CNelson A class that uses the rates of movies with movie users to
 *         calculate the prediction of an item with CF.
 *
 */
public class MovieRecommender implements RecommenderInterface<Integer, Integer> {

	private HashMap<Integer, Movie> findItem;
	private HashMap<Integer, MovieUser> findUser;
	private int neighbor;
	private PriorityBlockingQueue<IntPair> predictionR;
	private CompInt rateComp;
	private CompIntP rateCompP;
	private static MovieRecommender recommender;
	private double overallAvg;
	private Movie[] movieList;

	/**
	 * A private class for multiple threads
	 */
	private class RecThread extends Thread {
		private int idU;
		private int choose;
		private int start;
		private int length;

		public RecThread(int idU, int start, int length, int choose) {
			this.idU = idU;
			this.start = start;
			this.length = length;
			this.choose = choose;
		}

		@Override
		public void run() {
			for (int i = start; i < (start + length) && i < movieList.length; i++) {
				if (movieList[i].getName().equals("No name")) {
					continue;
				}
				double P;
				int idI = movieList[i].getID();
				if (findUser.get(idU).contains(idI)) {
					continue;
				}
				if (choose == 3) {
					P = predictionB(idU, idI);
				} else {
					P = predictionRMSE(idU, idI, choose);
				}
				IntPair newP = new IntPair(idI, P);
				predictionR.add(newP);
			}
		}
	}

	/**
	 * @param findItem
	 * @param findUser
	 * @param neighbor
	 * @return the singleton of Recommender instance
	 */
	public static MovieRecommender getRecommender(HashMap<Integer, Movie> findItem,
			HashMap<Integer, MovieUser> findUser, int neighbor, double overallAvg) {
		if (recommender == null) {
			recommender = new MovieRecommender(findItem, findUser, neighbor, overallAvg);
		}
		return recommender;
	}

	/**
	 * Constructor of the Recommender that takes two lists and two maps from
	 * File.
	 * 
	 * @param Movies
	 * @param MovieUsers
	 * @param findItem
	 * @param findUser
	 */
	private MovieRecommender(HashMap<Integer, Movie> findItem, HashMap<Integer, MovieUser> findUser, int neighbor,
			double overallAvg) {
		this.findItem = findItem;
		this.findUser = findUser;
		this.neighbor = neighbor;
		this.overallAvg = overallAvg;
	}

	/**
	 * @param idI
	 * @param idU
	 * @return r(u,i) - r(avgS)
	 */
	private double rMr(int idI, int idU) {
		return findItem.get(idI).getRate(idU) - findUser.get(idU).getAvg();
	}

	/**
	 * Calculate the similarity S(u,v) of the neighbors using Pearson and put
	 * into the priority queue.
	 * 
	 * @param idU1
	 * @param idUs
	 * @return a list of neighbors
	 */
	private PriorityQueue<IntPair> calculateSP(int idU1, ArrayList<Integer> idUs) {
		rateComp = new CompInt();
		PriorityQueue<IntPair> similarity = new PriorityQueue<IntPair>(rateComp);
		for (int i = 0; i < idUs.size(); i++) {
			int idU2 = idUs.get(i);
			if (idU1 == idU2) {
				continue;
			}
			ArrayList<Integer> idIs = getCommonMovie(idU1, idU2);
			double up = 0.0, downL = 0.0, downR = 0.0;
			for (int j = 0; j < idIs.size(); j++) {
				int idI = idIs.get(j);
				up += rMr(idI, idU1) * rMr(idI, idU2);
				downL += Math.pow(rMr(idI, idU1), 2);
				downR += Math.pow(rMr(idI, idU2), 2);
			}
			double S = (((Double) downL).compareTo(0.0) == 0 || ((Double) downR).compareTo(0.0) == 0) ? 0.0
					: up / (Math.sqrt(downL) * Math.sqrt(downR));
			IntPair newP = new IntPair(idU2, S);
			similarity.add(newP);
			while (similarity.size() > neighbor) {
				similarity.poll();
			}
		}
		return similarity;
	}

	/**
	 * Calculate the similarity S(u,v) of the neighbors using Cosine and put
	 * into the priority queue.
	 * 
	 * @param idU1
	 * @param idUs
	 * @return a list of neighbors
	 */
	private PriorityQueue<IntPair> calculateSC(int idU1, ArrayList<Integer> idUs) {
		rateComp = new CompInt();
		PriorityQueue<IntPair> similarity = new PriorityQueue<IntPair>(rateComp);
		MovieUser Ua = findUser.get(idU1);
		for (int i = 0; i < idUs.size(); i++) {
			int idU2 = idUs.get(i);
			if (idU1 == idU2) {
				continue;
			}
			ArrayList<Integer> idIs = getCommonMovie(idU1, idU2);
			MovieUser Ub = findUser.get(idU2);
			double up = 0.0, downL = 0.0, downR = 0.0;
			for (int j = 0; j < idIs.size(); j++) {
				int idI = idIs.get(j);
				up += Ua.getScore(idI) * Ub.getScore(idI);
			}
			Iterator<Map.Entry<Integer, Double>> iter = Ua.getMap().entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<Integer, Double> entry = iter.next();
				double val = (double) entry.getValue();
				downL += Math.pow(val, 2);
			}
			iter = Ub.getMap().entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<Integer, Double> entry = iter.next();
				double val = (double) entry.getValue();
				downR += Math.pow(val, 2);
			}
			double S = (((Double) downL).compareTo(0.0) == 0 || ((Double) downR).compareTo(0.0) == 0) ? 0.0
					: up / (Math.sqrt(downL) * Math.sqrt(downR));
			IntPair newP = new IntPair(idU2, S);
			similarity.add(newP);
			while (similarity.size() > neighbor) {
				similarity.poll();
			}
		}
		return similarity;
	}

	/**
	 * @param idU1
	 * @param idU2
	 * @return a list of movies both movie user1 and movie user2 rated
	 */
	private ArrayList<Integer> getCommonMovie(int idU1, int idU2) {
		ArrayList<Integer> idIs = new ArrayList<Integer>();
		HashMap<Integer, Double> map = findUser.get(idU1).getMap();
		MovieUser u = findUser.get(idU2);
		Iterator<Map.Entry<Integer, Double>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Double> entry = iter.next();
			Integer key = (Integer) entry.getKey();
			if (u.contains(key)) {
				idIs.add(key);
			}
		}
		return idIs;
	}

	/**
	 * @param idU
	 * @param idI
	 * @param choose
	 * @return the prediction P(u,i) of a movie to a movie user
	 */
	public double predictionRMSE(Integer idU, Integer idI, int choose) {
		ArrayList<Integer> idUs = getMovieUsers(idI);
		PriorityQueue<IntPair> similarity;
		if (choose == 1) {
			similarity = calculateSP(idU, idUs);
		} else {
			similarity = calculateSC(idU, idUs);
		}
		double up = 0.0, down = 0.0;
		for (int i = 0; i < neighbor && !similarity.isEmpty(); i++) {
			IntPair hi = similarity.poll();
			up += hi.score * rMr(idI, hi.id);
			down += Math.abs(hi.score);
		}
		double P = ((Double) down).compareTo(0.0) == 0 ? 0.0 : findUser.get(idU).getAvg() + up / down;
		return P;
	}

	/**
	 * @param idU
	 * @param idI
	 * @return the prediction P(u,i) of an item to a user by Baseline
	 */
	public double predictionB(Integer idU, Integer idI) {
		MovieUser U = findUser.get(idU);
		Movie I = findItem.get(idI);
		ArrayList<Integer> idUs = getMovieUsers(idI);
		double bu = U.getAvg() - overallAvg;
		double biuTot = 0.0;
		for (int i = 0; i < idUs.size(); i++) {
			biuTot += findUser.get(idUs.get(i)).getAvg() - overallAvg;
		}
		double biu = (idUs.size() == 0) ? 0.0 : biuTot / (double) idUs.size();
		double bi = I.getAvg() - biu - overallAvg;
		return bu + bi + overallAvg;
	}

	/**
	 * @param idI
	 * @return a list of the movie users who have rated this movie
	 */
	private ArrayList<Integer> getMovieUsers(int idI) {
		ArrayList<Integer> idUs = new ArrayList<Integer>();
		HashMap<Integer, Double> map = findItem.get(idI).getMap();
		Iterator<Map.Entry<Integer, Double>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Double> entry = iter.next();
			Integer key = (Integer) entry.getKey();
			idUs.add(key);
		}
		return idUs;
	}

	/**
	 * @param idU
	 * @param n
	 * @param choose
	 * @return a list of n recommendations
	 */
	public ArrayList<String> recommendation(Integer idU, int n, int choose) {
		rateCompP = new CompIntP();
		predictionR = new PriorityBlockingQueue<IntPair>(n, rateCompP);
		movieList = new Movie[findItem.size()];
		int idx = 0;
		Iterator<Map.Entry<Integer, Movie>> iter = findItem.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Movie> entry = iter.next();
			Movie val = (Movie) entry.getValue();
			movieList[idx] = val;
			idx++;
		}

		int threadN = 20; // number of threads, can be changed
		System.out.println("Running with " + threadN + " threads...");
		RecThread[] threads = new RecThread[threadN];
		int length = findItem.size() / threadN;
		int st, j;
		for (st = 0, j = 0; st < findItem.size() && j < threadN - 1; st += length, j++) {
			threads[j] = new RecThread(idU, st, length, choose);
			threads[j].start();
		}
		threads[threadN - 1] = new RecThread(idU, st, findItem.size() - st, choose);
		threads[threadN - 1].start();
		for (j = 0; j < threadN; j++) {
			try {
				threads[j].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0; i < n && !predictionR.isEmpty();) {
			IntPair hi = predictionR.poll();
			if (findUser.get(idU).contains(hi.id)) {
				continue;
			}
			String s = "id: " + hi.id + "   " + findItem.get(hi.id).getName() + "   "
					+ findItem.get(hi.id).getOther().toString();
			res.add(s);
			++i;
		}
		return res;
	}
}
