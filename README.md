#  Coding Exercise for the Fetch Rewards Android Team

- App renders a list of Items from the provided endpoint, filtering out Items without a name, sorting them by list ID followed by name,
and finally grouping them into individual lists based on list ID
- Written using Jetpack Compose and a MVVM Architecture, splitting various features, like the Item API Service, 
into their own files outside of the Model, View and ViewModel

## Features
- Makes requests to the provided endpoint by setting up a Retrofit instance that uses the given URL without any path as the Retrofit base URL 
  - This Retrofit instance is used to create ItemService, which defines a single GET request method using the provided endpoint's path
  - An ItemRestApiDataSource class is used to call the ItemService and return the HTTP response's parsed results
  - An ItemRepository checks the DataSource's HTTP response result, and, unless the response was bad, it provides
  any returned data to the ItemViewModel parsed into a list of Items
- The Item model is simply an ID integer, name String, and listId integer
  - Since some items from the endpoint have null or empty names, the name String is optional
- ItemListViewModel requests data from the Item Repository, accepts any data provided, and filters, sorts and groups it 
into a presentable list of Items for the ItemListView to use
  - While the ViewModel waits for the request to send a response, a loading message is displayed to the user 
  - If the endpoint isn't available or fails to provide any data, then an error message is displayed to the user
  - If the user would like to refresh the data after the first request, additional requests can be made by swiping
  down from the top of the screen to activate a pull-to-refresh composable
  - After scrolling down the list, the user may press a Floating Action Button so that they can return to the top of the list
- Material 3 Themed Design acts as a strong guide for styling and better conveys the purpose of each composable
- Testable by splitting each aspect of a given feature into its own file to handle ONLY its specific concern, allowing Hilt 
to inject dependencies ONLY where they are needed via constructor injection in app
  - By keeping dependency injection in mind, Mockito can be used to create mocks that are easily substituted into tests,
  so that unneeded requests to the endpoint and extra work can be prevented, keeping focus on the subject under test

## Underlying Technology Used
- Jetpack Compose with Material Design 3 for theming
- Android ViewModel
- Hilt Dependency Injection
- Retrofit HTTP Client
  - GSON for JSON Conversion
- Kotlin cold and hot Flows
- Mockito for testing