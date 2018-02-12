This Recommend System can take either the book or the movie data or both of them. Please input the arguments like the form below:

To inport both data: all MovieFile MovieuserFile MovieRatingFile MovieNeighbor# BookFile BookUserFile BookRatingFile BookNeighbor#
To import only movie: movie MovieFile MovieuserFile MovieRatingFile MovieNeighbor#
To import only book: book BookFile BookUserFile BookRatingFile BookNeighbor#

Here the XXXFile is the corresponding file name like movies.csv.

One example for both data is: all movies.csv tags.csv ratings.csv 20 BX-Books.csv BX-Users.csv BX-Book-Ratings.csv 20

The experinment only uses the small movie data. Please input the arguments as: movie movies.csv tags.csv ratings.csv 20



The experiment just runs some samples and print out the result. There is no input needed.

To use the Recommend System, run the Main class with arguments and follow the instruction printed to make choice.

Input 1 or 2 to get prediction or recommendation. Input s to switch data if more than one data are imported. Input q to exit.

Before input the user id or item id, first the user should also choose which method to calculate the result. Input 1 as Pearson, 2 as Cosine and 3 as Baseline. Input b to go back.

In prediction function, input ids as "user id,item id" to get result. Input b to go back.
In recommendation function, input as "user id,number of items" to get result. Input b to go back.



In milestone 3, there are several changes I made:

To make the system more generic and easy to support different files, I used generic Interfaces for Item, User, File and Recommender so that the hashmap and id can support different types. The Interfaces are designed with Facade pattern so that for different data, different prediction and recommendation methods can be implemented. I also designed a factory class called RecFatcory to process different datas and make possible to switch between datas. If there is a new kind of data, we just need to implement the Interfaces to create certain objects and add some case determining codes to the factory class. The important classes like RecFactory, Recommender and File are made with Singleton.

To make system faster, the first improvement I made is that I used a size-fixed priority queue for storing neighbors rather than storing all the neighbors then find the first N neighbors. This is realized with a min heap instead of the original max heap. Once the queue size is larger than neighbor size, the one with least similarity will be polled. Eventually we can got a queue of neighbors with highest similarity. The running time will become a little smaller as when the heap size become larger, the time for fixing heap will be larger. This is also made for saving space, which is important for multi threads.
The second improvement I made is to use multi threads when calculating the recommendation. With a PriorityBlockingQueue we can assure thread safe and let multiple threads to push results into the queue. The running time is significantly reduced for 4 or 5 times.
There are also several improvements I made in milestone 2 for performance, like using hashmap to access in O(1). Also calculating average rating for each item and user when loading file will have a significant improvement when we are using Baseline to calculate the prediction. With pre-calculated average we can get Baseline recommendations much faster than Pearson and Cosine even if they use multi thread.







