Feature: Home page


  @home_page @home_page_display
  Scenario Outline: Check page display
    Given A user navigates to HomePage "<url>"
    Then  logo is displayed
    And search bar is displayed

    Examples:
      |https://www.google.com/|
    |teena@mailinator.com|

