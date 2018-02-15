package com.unresyst;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.io.IOException;

import org.apache.commons.cli2.OptionException; 
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.AveragingPreferenceInferrer;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class UnresystBoolRecommend {
    
    public static void main(String... args) throws FileNotFoundException, TasteException, IOException, OptionException {
        
        // create data source (model) - from the csv file                                
        FileDataModel model = new FileDataModel(new File("datasets/intro.csv"));
        
       //Get a neighborhood of users
        //other similarities include: CityBlockSimilarity, EuclideanDistanceSimilarity, LogLikelihoodSimilarity, TanimotoCoefficientSimilarity, UncenteredCosineSimilarity
        UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model);
        //Optional: set default way of resolving empty values
        
       userSimilarity.setPreferenceInferrer(new AveragingPreferenceInferrer(model));
        
        //create k-NN algorithm 
        UserNeighborhood neighborhood =
                new NearestNUserNeighborhood(2, userSimilarity, model);
        
        //Create the recommender
        Recommender recommender =
                new GenericUserBasedRecommender(model, neighborhood, userSimilarity);
        //and a caching decorator
        Recommender cachingRecommender = new CachingRecommender(recommender);
        
      
   
        // for all users
        for (LongPrimitiveIterator it = model.getUserIDs(); it.hasNext();){
            long userId = it.nextLong();
            
            // get the recommendations for the user
            List<RecommendedItem> recommendations = cachingRecommender.recommend(userId, 3);
            
            // if empty write something
            if (recommendations.size() == 0){
                System.out.print("User ");
                System.out.print(userId);
                System.out.println(": no recommendations");
            }
                            
            // print the list of recommendations for each 
            for (RecommendedItem recommendedItem : recommendations) {
                System.out.print("User ");
                System.out.print(userId);
                System.out.print(": ");
                System.out.println(recommendedItem);
            }
        }
        
    }
}