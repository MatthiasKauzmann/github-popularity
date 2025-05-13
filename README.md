# GitHub rate limit

- a user can pass number of pages and page size for GitHub Search API requests
- page size can be between 1 and 100
- but the number of pages depends on authentication

## Unauthenticated

* GitHub allows only ten unauthenticated repository search API requests per minute
* one such a request (one page) responds with at max 100 results 
* as an unauthenticated user you can at max request 10 pages corresponding to 10x100=1000 results

## Authenticated

* authenticated requests get 30 requests per minute. Therefore, I provided the opportunity to pass a personal GitHub
  access token at start-up. The token will then be available throughout the application runtime for authenticated
  requests providing more results.
* as an authenticated user you can at max request 30 pages corresponding to 30x100=3000 results

If you want to pass a token you can start the app with Maven like

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--github.access-token=your_github_token_here"
```

or with the Java cli

```bash
java -jar your-application.jar --github.access-token=your_github_token_here
```

# Swagger

You can access the Swagger-UI for API docs at `localhost:8080/swagger-ui/index.html`

# PlantUML

You'll find a component and sequence diagram in `./plantuml`