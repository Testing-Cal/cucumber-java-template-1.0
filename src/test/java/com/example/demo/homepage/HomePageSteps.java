package com.example.demo.homepage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class HomePageSteps {


    private HomePage homePage;

    public HomePageSteps() {
        this.homePage = new HomePage();
    }

    @Given("^A user navigates to HomePage \"([^\"]*)\"$")
    public void aUserNavigatesToHomePage(String country) {
        this.homePage.goToHomePage();
    }

    @Then("^ Application Homepage is displayed$")
    public void platformpageisdisplayed() {
    	this.homePage.getTitle();
    	 System.out.println("page is loaded");
    }
  
    @And("^ user logs out of the project")
      public void logout() {	
    	this.homePage.logout();
    	 System.out.println("page title is displayed");
    	 
    }

 
   
   
    
}
