package com.example.demo.homepage;

import com.example.demo.basepage.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


public class HomePage extends BasePage {

    private static final String HOME_PAGE_URL = "https://www.facebook.com/";

    @FindBy(css = "input[name=email]")
    private WebElement email;

    

    HomePage() {
        PageFactory.initElements(driver, this);
    }

    void goToHomePage(){
        driver.get(HOME_PAGE_URL);
        wait.forLoading(2000);
    }

    void logout() {
       driver.quit();
    }

    String getTitle() {
        return driver.getTitle();
     
    }

	/*
	 * void checkSearchBarDisplay() { wait.forElementToBeDisplayed(5000,
	 * this.searchInput, "Search Bar"); }
	 * 
	 * void searchFor(String searchValue) { this.searchInput.sendKeys(searchValue);
	 * this.searchInput.sendKeys(Keys.ENTER); }
	 */

	
	}

