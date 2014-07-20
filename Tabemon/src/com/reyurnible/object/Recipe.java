package com.reyurnible.object;

import java.util.ArrayList;
import java.util.List;

import com.reyurnible.network.CustomImageLoader.ImageContainer;

public class Recipe {
	public long recipeId;
    public String recipeTitle;
    public String recipeUrl;
    public String foodImageUrl;
    public int pickup;
    public int shop;
    public String nickname;
    public String recipeDescription;
    public ArrayList<String> recipeMaterial = new ArrayList<String>();
    public String recipeIndication;
    public String recipeCost;
    public String categoryUrl;
    public String categoryName;
    public String recipePublishday;
    
    public ImageContainer imageContainer;
	
    public Recipe(
    		long recipeId,
    		String recipeTitle,
    		String recipeUrl,
    		String foodImageUrl,
    		int pickup,
    		int shop,
    		String nickname,
    		String recipeDescription,
    		ArrayList<String> recipeMaterial,
    		String recipeIndication,
    		String recipeCost,
    		String categoryUrl,
    		String categoryName,
    		String recipePublishday){
    	this.recipeId = recipeId;
		this.recipeTitle = recipeTitle;
		this.recipeUrl = recipeUrl; 
		this.foodImageUrl = foodImageUrl;
		this.pickup = pickup;
		this.shop = shop;
		this.nickname = nickname;
		this.recipeDescription = recipeDescription;
		this.recipeMaterial = recipeMaterial;
		this.recipeIndication = recipeIndication;
		this.recipeCost = recipeCost;
		this.categoryUrl = categoryUrl;
		this.categoryName = categoryName;
		this.recipePublishday = recipePublishday;
    }
}