## Todd's Implementation of a miniature AllRecipes website using Java Spring and JSP (soon to be Thymeleaf).
The below sections will be updated!


## Gmail API 

- https://developers.google.com/gmail/api/quickstart/java
- The above will help you generate contents for the client_secret.json and StoredCredential files that are used to send an email to the email specified in the adminEmailList.properties file.

## JUnit Tests Information:

- The testAccountCredentials.properties file must have a password filled out to run the JUnit tests.
- The idea of my JUnit tests was to use multiple test accounts to test for functionality of the application such as: viewing a shopping/pantry list when logged in, viewing these lists when not logged in, and viewing the the shopping/pantry list of another user.
- These tests were able to test the requestmapping annotation used in the controller classes by using a MockMvc object to simulate get and post requests.

# todo: (currently what I'm working on replacing)

- Replace jsp files using thymeleaf.



# A running demo can be seen: https://ar-app.herokuapp.com/