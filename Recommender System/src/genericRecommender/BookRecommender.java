package genericRecommender;

import java.util.*;
import java.util.concurrent.*;

/**
 * Modified Comparator that compares scores for priority queue.
 */
class CompI implements Comparator<IntPair> {

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

class CompStrP implements Comparator<StrPair> {

	@Override
	public int compare(StrPair a, StrPair b) {
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
 * @author CNelson A class that uses the rates of books with book users to
 *         calculate the prediction of an item with CF.
 *
 */
public class BookRecommender implements RecommenderInterface<Integer, String> {

	private HashMap<String, Book> findItem;
	private HashMap<Integer, BookUser> findUser;
	private int neighbor;
	private PriorityBlockingQueue<StrPair> predictionR;
	private CompI rateCompI;
	private CompStrP rateCompSP;
	private static BookRecommender recommender;
	private double overallAvg;
	private Book[] bookList;

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
			for (int i = start; i < (start + length) && i < bookList.length; i++) {
				if (bookList[i].getName().equals("No name")) {
					continue;
				}
				double P;
				String idI = bookList[i].getID();
				if (findUser.get(idU).contains(idI)) {
					continue;
				}
				if (choose == 3) {
					P = predictionB(idU, idI);
				} else {
					P = predictionRMSE(idU, idI, choose);
				}
				StrPair newP = new StrPair(idI, P);
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
	public static BookRecommender getRecommender(HashMap<String, Book> findItem, HashMap<Integer, BookUser> findUser,
			int neighbor, double overallAvg) {
		if (recommender == null) {
			recommender = new BookRecommender(findItem, findUser, neighbor, overallAvg);
		}
		return recommender;
	}

	/**
	 * Constructor of the Recommender that takes two lists and two maps from
	 * File.
	 * 
	 * @param Books
	 * @param BookUsers
	 * @param findItem
	 * @param findUser
	 */
	private BookRecommender(HashMap<String, Book> findItem, HashMap<Integer, BookUser> findUser, int neighbor,
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
	private double rMr(String idI, int idU) {
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
		rateCompI = new CompI();
		PriorityQueue<IntPair> similarity = new PriorityQueue<IntPair>(rateCompI);
		for (int i = 0; i < idUs.size(); i++) {
			int idU2 = idUs.get(i);
			if (idU1 == idU2) {
				continue;
			}
			ArrayList<String> idIs = getCommonBook(idU1, idU2);
			double up = 0.0, downL = 0.0, downR = 0.0;
			for (int j = 0; j < idIs.size(); j++) {
				String idI = idIs.get(j);
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
		rateCompI = new CompI();
		PriorityQueue<IntPair> similarity = new PriorityQueue<IntPair>(rateCompI);
		BookUser Ua = findUser.get(idU1);
		for (int i = 0; i < idUs.size(); i++) {
			int idU2 = idUs.get(i);
			if (idU1 == idU2) {
				continue;
			}
			ArrayList<String> idIs = getCommonBook(idU1, idU2);
			BookUser Ub = findUser.get(idU2);
			double up = 0.0, downL = 0.0, downR = 0.0;
			for (int j = 0; j < idIs.size(); j++) {
				String idI = idIs.get(j);
				up += Ua.getScore(idI) * Ub.getScore(idI);
			}
			Iterator<Map.Entry<String, Double>> iter = Ua.getMap().entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Double> entry = iter.next();
				double val = (double) entry.getValue();
				downL += Math.pow(val, 2);
			}
			iter = Ub.getMap().entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Double> entry = iter.next();
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
	 * @return a list of books both book user1 and book user2 rated
	 */
	private ArrayList<String> getCommonBook(int idU1, int idU2) {
		ArrayList<String> idIs = new ArrayList<String>();
		HashMap<String, Double> map = findUser.get(idU1).getMap();
		BookUser u = findUser.get(idU2);
		Iterator<Map.Entry<String, Double>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Double> entry = iter.next();
			String key = (String) entry.getKey();
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
	 * @return the prediction P(u,i) of a book to a book user
	 */
	public double predictionRMSE(Integer idU, String idI, int choose) {
		ArrayList<Integer> idUs = getBookUsers(idI);
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
	public double predictionB(Integer idU, String idI) {
		BookUser U = findUser.get(idU);
		Book I = findItem.get(idI);
		ArrayList<Integer> idUs = getBookUsers(idI);
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
	 * @return a list of the book users who have rated this book
	 */
	private ArrayList<Integer> getBookUsers(String idI) {
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
		rateCompSP = new CompStrP();
		predictionR = new PriorityBlockingQueue<StrPair>(n, rateCompSP);
		bookList = new Book[findItem.size()];
		int idx = 0;
		Iterator<Map.Entry<String, Book>> iter = findItem.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Book> entry = iter.next();
			Book val = (Book) entry.getValue();
			bookList[idx] = val;
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
			StrPair hi = predictionR.poll();
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
