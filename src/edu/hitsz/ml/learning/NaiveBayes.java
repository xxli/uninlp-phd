package edu.hitsz.ml.learning;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class NaiveBayes {
	Vector<String> category;
	Vector<Double> prior;
	HashMap<String,Vector<Double>> totalFeatures;
	
	public NaiveBayes(){
		category=new Vector<String>();
		prior=new Vector<Double> ();
		totalFeatures=new HashMap<String,Vector<Double>>();
	}
	
	/**
	 * add all classes and their numbers
	 * @param category Classes
	 * @param prior Prior probabilities
	 */
	public void addCategoryAndNumber(Vector<String> categories, Vector<Integer> numbers){
		int categoryNumber=categories.size();
		if(categoryNumber<0){
			System.out.println("The classes donot exist. Please check");
			System.exit(1);
		}
		if(categoryNumber!=numbers.size()){
			System.out.println("The number of classes and their probabilies is not equal. Please check");
			System.exit(1);
		}
		int totalNumber=0;
		for(int i=0;i<categoryNumber;i++){
			totalNumber+=numbers.get(i);
		}
		this.category.addAll(categories);
		for(int i=0;i<categoryNumber;i++){
			this.prior.add(numbers.get(i)/(double)totalNumber);
		}
		
	}
	
	
	/**
	 * add all classes and their corresponding prior probabilities
	 * @param category Classes
	 * @param prior Prior probabilities
	 */
	public void addCategoryAndProb(Vector<String> categories, Vector<Double> probabilities){
		int categoryNumber=categories.size();
		if(categoryNumber<0){
			System.out.println("The classes donot exist. Please check");
			System.exit(1);
		}
		if(categoryNumber!=probabilities.size()){
			System.out.println("The number of classes and their probabilies is not equal. Please check");
			System.exit(1);
		}
		this.category.addAll(categories);
		this.prior.addAll(probabilities);
	}
	
	/**
	 * add all feature (name)
	 * @param features
	 */
	public void addFeatures(Vector<String> features){
		int categoryNumber=category.size();
		if(categoryNumber<0){
			System.out.println("The classes donot exist. Please check");
			System.exit(1);
		}
		for(String feature : features){
			Vector<Double> newVec=new Vector<Double>();
			for(int i=0;i<categoryNumber;i++)
				newVec.add(0.0);
			totalFeatures.put(feature, newVec);
		}
	}
	
	
	/**
	 * Instance with its features, corresponding probabilities, and the category it belongs to.
	 * @param oneCategory
	 * @param features
	 * @param probabilities
	 */
	public void addInstanceAndProb(String oneCategory, Vector<String> features, Vector<Double> probabilities){
		if(!category.contains(oneCategory)){
			System.out.println("This Catogory \" "+oneCategory+"\" doesn't exists. Please check");
			System.exit(1);
		}
		int categoryNumber=category.size();
		int categoryPos=category.indexOf(oneCategory);
		int feaNumber=features.size();
		for(int i=0;i<feaNumber;i++){
			if(!totalFeatures.containsKey(features.get(i))){
				Vector<Double> newVec=new Vector<Double>();
				for(int j=0;j<categoryNumber;j++)
					newVec.add(0.0);
				newVec.set(categoryPos, probabilities.get(i));
				totalFeatures.put(features.get(i), newVec);
			}
			else{
				Vector<Double> newVec=totalFeatures.get(features.get(i));
				newVec.set(categoryPos, probabilities.get(i));
				totalFeatures.put(features.get(i), newVec);
			}
		}
	}
	
	/**
	 * one instance with its features, corresponding probabilities, and the category it belongs to.
	 * @param oneCategory
	 * @param features
	 * @param probabilities
	 * @param smoothMethod Method of smoothing: addOneSmooth
	 */
	public void addInstanceAndNumber(String oneCategory, Vector<String> features, Vector<Double> numbers,String smoothMethod){
		if(!category.contains(oneCategory)){
			System.out.println("This Catogory \" "+oneCategory+"\" doesn't exists. Please check");
			System.exit(1);
		}
		int featureLen=features.size();
		if(featureLen!=numbers.size()){
			System.out.println("The number of features and their probabilies is not equal. Please check");
			System.exit(1);
		}	
		try{
			Class c = Class.forName("NaiveBayes");
            Method[] m=c.getMethods();
            for(int i=0;i<m.length;i++)
            {
                if(m[i].getName().equals(smoothMethod))
                {        
                        m[i].invoke(oneCategory,features,numbers);
                }
            }
		}
		catch (Exception e) {
	            e.printStackTrace();
	        }  
	}
	
	/**
	 * using add-one smoothing to computer the probability
	 * @param oneCategory
	 * @param features
	 * @param numbers
	 */
	public void addOneSmooth(String oneCategory, Vector<String> features, Vector<Double> numbers){
		int totalFeaturesNumber=totalFeatures.size();
		int categoryPos=category.indexOf(oneCategory);
		int totalCount=0;
		//computer the posterior
		int featureLen=features.size();
		Iterator iter=totalFeatures.entrySet().iterator();
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    String oneFeature = entry.getKey().toString(); 
			if(features.contains(oneFeature)){
				int featurePos=features.indexOf(oneFeature);
				totalCount+=numbers.get(featurePos);
			}
			else
				totalCount+=1;				
		}
		int feaNumber=features.size();
		for(int i=0;i<feaNumber;i++){
			if(!totalFeatures.containsKey(features.get(i))){
				System.out.println("The features of one instance is not in all features. Please check");
				System.exit(1);
			}
			else{
				Vector<Double> newVec=totalFeatures.get(features.get(i));
				newVec.set(categoryPos, numbers.get(i)/(double)totalCount);
				totalFeatures.put(features.get(i),newVec);
			}
		}
	}
	
	
	/**
	 * given the features of an instance, return its class
	 * @param features
	 * @return
	 */
	public String findInstanceCategory(Vector<String> features){
		if(features.size()<0){
			System.out.println("The input features of the instance doesn't exist. Please check");
			System.exit(1);
		}
		int categoryNumber=category.size();
		if(categoryNumber<0){
			System.out.println("The classes donot exist. Please check");
			System.exit(1);
		}
		double bestPosteriorProb=0;
		int bestCategory=0;
		for(int i=0;i<categoryNumber;i++){
			double tmpProb=Math.log(prior.get(i))+compPosteriorProb(features,i);
			if(tmpProb>bestPosteriorProb){
				bestPosteriorProb=tmpProb;
				bestCategory=i;
			}
		}
		return category.get(bestCategory);
	}
	
	/**
	 * computer the posterior probability of an instance to a class, given the features of the instance 
	 * @param features
	 * @param i
	 * @return
	 */
	double compPosteriorProb(Vector<String> features, int i){
		double posteriorProb=0;
		for(String feature : features){
			if(totalFeatures.containsKey(feature))
				posteriorProb+=Math.log(totalFeatures.get(feature).get(i));
		}
		return posteriorProb;
	}
	
	
	
	static void textClassification(){
		Vector<String> cat=new Vector<String>();
		cat.add("China");
		cat.add("No-China");
		Vector<Integer> catNumber=new Vector<Integer>();
		catNumber.add(3);
		catNumber.add(1);
		Vector<String> fea=new Vector<String>();
		Vector<Integer> feaNumber=new Vector<Integer>();
		fea.add("Chinese");
		feaNumber.add(5);
		fea.add("Beijing");
		feaNumber.add(1);
		fea.add("Macao");
		feaNumber.add(1);
		fea.add("Shanghai");
		feaNumber.add(1);
		
		
		
		
	}
	
	
	public static void main(String[] args){
		textClassification();
	}
	

}
