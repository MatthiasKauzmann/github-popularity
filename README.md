# GitHub rate limit

## Unauthenticated

* GitHub allows only ten unauthenticated repository search API requests per minute
* one such a request responds with at max 100 results (which corresponds to one page)
* my GitHub client implementation will try to exhaust this limit and fetch as many results as possible.
  For example, given a search for Java repositories with earliest created-at-date May 1st 2025 will result in
  more than 1k results, my client will therefore fetch 10 pages to exhaust the limit (fetching a page costs one
  request). Hence, the result will have
  1k items

## Authenticated

* authenticated requests get 30 requests per minute. Therefore, I provided the opportunity to pass a personal GitHub
  access token at start-up. The token will then be available throughout the application runtime for authenticated
  requests
  providing more results.

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
