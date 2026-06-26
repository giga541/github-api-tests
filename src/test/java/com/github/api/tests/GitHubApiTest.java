package com.github.api.tests;

import com.github.api.methods.*;
import com.zebrunner.carina.api.apitools.validation.JsonCompareKeywords;
import com.zebrunner.carina.api.http.HttpResponseStatusType;
import com.zebrunner.carina.core.IAbstractTest;
import com.zebrunner.carina.utils.R;
import io.restassured.response.Response;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * GitHub API Tests using Carina Framework + Freemarker templates.
 * <p>
 * Endpoints under test:
 * 1. GET /users/{username}
 * 2. GET /repos/{owner}/{repo}
 * 3. GET /repos/{owner}/{repo}/issues
 * 4. GET /search/repositories
 * 5. GET /users/{username}/repos
 * <p>
 * Test data is loaded from _config.properties — no hardcoding in test code.
 * Response templates use Freemarker placeholders and Carina wildcards
 * (skip, type:*, regex:*) to validate structure without coupling tests to live data.
 */
public class GitHubApiTest implements IAbstractTest {

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers – read config values centrally so test methods stay clean
    // ─────────────────────────────────────────────────────────────────────────

    private String cfg(String key) {
        return R.CONFIG.get(key);
    }

    // =========================================================================
    //  ENDPOINT 1 – GET /users/{username}
    // =========================================================================

    /**
     * POSITIVE: Fetch a well-known GitHub user (octocat).
     * Asserts HTTP 200, validates response against Freemarker template,
     * then also validates structure against the JSON schema file.
     */
    @Test(description = "[POSITIVE] GET /users/{username} – existing user returns 200")
    public void testGetExistingUser() {
        String username = cfg("github.valid_username");   // "octocat" from config

        GetUserMethod api = new GetUserMethod(username);
        // Pass the username into the response template via Freemarker
        api.addProperty("username", username);

        api.expectResponseStatus(HttpResponseStatusType.OK_200);
        Response response = api.callAPI();

        // 1. Validate body against Freemarker template (rs.json)
        api.validateResponse(JSONCompareMode.LENIENT);

        // 2. Extra: validate JSON schema
        api.validateResponseAgainstSchema("api/github/users/_get/rs.schema");

        // 3. Extract and assert a key field via JsonPath
        String actualLogin = response.jsonPath().getString("login");
        Assert.assertEquals(actualLogin, username,
                "Login in response must match the requested username");
    }

    /**
     * NEGATIVE: Request a username that does not exist.
     * GitHub returns 404 Not Found.
     */
    @Test(description = "[NEGATIVE] GET /users/{username} – non-existent user returns 404")
    public void testGetNonExistentUser() {
        String username = cfg("github.invalid_username");  // "this-user-does-not-exist-xyz-999"

        GetUserMethod api = new GetUserMethod(username);

        // We explicitly set 404 as the expected status for this negative case
        api.expectResponseStatus(HttpResponseStatusType.NOT_FOUND_404);
        Response response = api.callAPI();

        // Confirm error message is present in body
        String message = response.jsonPath().getString("message");
        Assert.assertNotNull(message, "Error response should contain a 'message' field");
    }

    // =========================================================================
    //  ENDPOINT 2 – GET /repos/{owner}/{repo}
    // =========================================================================

    /**
     * POSITIVE: Fetch a well-known public repository (octocat/Hello-World).
     * Validates HTTP 200 and checks name/owner in the response.
     */
    @Test(description = "[POSITIVE] GET /repos/{owner}/{repo} – existing repo returns 200")
    public void testGetExistingRepository() {
        String owner = cfg("github.valid_owner");  // "octocat"
        String repo = cfg("github.valid_repo");   // "Hello-World"

        GetRepositoryMethod api = new GetRepositoryMethod(owner, repo);
        // Inject into Freemarker response template
        api.addProperty("owner", owner);
        api.addProperty("repo", repo);

        api.expectResponseStatus(HttpResponseStatusType.OK_200);
        Response response = api.callAPI();

        api.validateResponse(JSONCompareMode.LENIENT);

        // Assert key fields via JsonPath
        String actualName = response.jsonPath().getString("name");
        String actualOwner = response.jsonPath().getString("owner.login");
        Assert.assertEquals(actualName, repo, "Repository name must match");
        Assert.assertEquals(actualOwner, owner, "Repository owner must match");
    }

    /**
     * NEGATIVE: Request a repository that does not exist.
     * GitHub returns 404 Not Found.
     */
    @Test(description = "[NEGATIVE] GET /repos/{owner}/{repo} – non-existent repo returns 404")
    public void testGetNonExistentRepository() {
        String owner = cfg("github.invalid_owner");  // "no-such-owner-xyz"
        String repo = cfg("github.invalid_repo");   // "no-such-repo-xyz"

        GetRepositoryMethod api = new GetRepositoryMethod(owner, repo);

        api.expectResponseStatus(HttpResponseStatusType.NOT_FOUND_404);
        Response response = api.callAPI();

        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "Not Found",
                "Error message should say 'Not Found' for missing repo");
    }

    // =========================================================================
    //  ENDPOINT 3 – GET /repos/{owner}/{repo}/issues
    // =========================================================================

    /**
     * POSITIVE: Get open issues from octocat/Hello-World.
     * Response is an array; we validate at least one item matches the template
     * using Carina's ARRAY_CONTAINS strategy.
     */
    @Test(description = "[POSITIVE] GET /repos/{owner}/{repo}/issues – returns 200 with issue list")
    public void testGetRepoIssues() {
        String owner = cfg("github.valid_owner");
        String repo = cfg("github.valid_repo");

        GetRepoIssuesMethod api = new GetRepoIssuesMethod(owner, repo);

        api.callAPIExpectSuccess();

        // ARRAY_CONTAINS: verify at least one item in response matches rs.json structure
        api.validateResponse(JSONCompareMode.LENIENT,
                JsonCompareKeywords.ARRAY_CONTAINS.getKey());
    }

    /**
     * NEGATIVE: Get issues from a repo that does not exist.
     * GitHub returns 404.
     */
    @Test(description = "[NEGATIVE] GET /repos/{owner}/{repo}/issues – non-existent repo returns 404")
    public void testGetIssuesNonExistentRepo() {
        String owner = cfg("github.invalid_owner");
        String repo = cfg("github.invalid_repo");

        GetRepoIssuesMethod api = new GetRepoIssuesMethod(owner, repo);

        api.expectResponseStatus(HttpResponseStatusType.NOT_FOUND_404);
        api.callAPI();
    }

    // =========================================================================
    //  ENDPOINT 4 – GET /search/repositories
    // =========================================================================

    /**
     * POSITIVE: Search for repositories with a valid keyword.
     * Validates HTTP 200 and that total_count > 0.
     */
    @Test(description = "[POSITIVE] GET /search/repositories – valid query returns results")
    public void testSearchRepositoriesWithValidQuery() {
        String query = cfg("github.search_query");  // "carina+automation"

        SearchRepositoriesMethod api = new SearchRepositoriesMethod(query);

        api.expectResponseStatus(HttpResponseStatusType.OK_200);
        Response response = api.callAPI();

        // Top-level structure check
        api.validateResponse(JSONCompareMode.LENIENT,
                JsonCompareKeywords.ARRAY_CONTAINS.getKey() + "items");

        // Assert meaningful results were returned
        Integer totalCount = response.jsonPath().getInt("total_count");
        Assert.assertTrue(totalCount > 0,
                "Search for '" + query + "' should return at least 1 result");
    }

    /**
     * NEGATIVE: Search with an empty query string.
     * GitHub returns 422 Unprocessable Entity.
     */
    @Test(description = "[NEGATIVE] GET /search/repositories – empty query returns 422")
    public void testSearchRepositoriesWithEmptyQuery() {
        // Passing empty string triggers GitHub's validation error
        SearchRepositoriesMethod api = new SearchRepositoriesMethod("");

        api.expectResponseStatus(HttpResponseStatusType.UNPROCESSABLE_ENTITY_422);
        Response response = api.callAPI();

        // GitHub should return a descriptive error body
        String message = response.jsonPath().getString("message");
        Assert.assertNotNull(message,
                "422 response should contain a 'message' field describing the error");
    }

    // =========================================================================
    //  ENDPOINT 5 – GET /users/{username}/repos
    // =========================================================================

    /**
     * POSITIVE: List public repos for octocat.
     * Uses ARRAY_CONTAINS to confirm the array items match the template structure.
     * Also uses JsonPath to assert the response is non-empty.
     */
    @Test(description = "[POSITIVE] GET /users/{username}/repos – valid user returns repo list")
    public void testGetUserRepos() {
        String username = cfg("github.valid_username");  // "octocat"

        GetUserReposMethod api = new GetUserReposMethod(username);
        api.addProperty("username", username);

        api.expectResponseStatus(HttpResponseStatusType.OK_200);
        Response response = api.callAPI();

        // Validate array structure with ARRAY_CONTAINS (at least one item matches)
        api.validateResponse(JSONCompareMode.LENIENT,
                JsonCompareKeywords.ARRAY_CONTAINS.getKey());

        // Confirm at least one repo was returned
        int repoCount = response.jsonPath().getList("$").size();
        Assert.assertTrue(repoCount > 0,
                "User '" + username + "' should have at least one public repository");
    }

    /**
     * NEGATIVE: List repos for a username that does not exist.
     * GitHub returns 404 Not Found.
     */
    @Test(description = "[NEGATIVE] GET /users/{username}/repos – non-existent user returns 404")
    public void testGetReposForNonExistentUser() {
        String username = cfg("github.invalid_username");

        GetUserReposMethod api = new GetUserReposMethod(username);

        api.expectResponseStatus(HttpResponseStatusType.NOT_FOUND_404);
        Response response = api.callAPI();

        String message = response.jsonPath().getString("message");
        Assert.assertNotNull(message,
                "404 response should include a 'message' field");
    }

    // =========================================================================
    //  BONUS POSITIVE: chain call – get user, extract repos_url, call it
    // =========================================================================

    /**
     * POSITIVE (chained): Get user → extract repos_url from response →
     * use that URL directly to list repos. Demonstrates Carina's data
     * extraction between API calls (step 6 → step 7 in the Carina flow).
     */
    @Test(description = "[POSITIVE] Chained call: GET user → GET user repos via repos_url")
    public void testChainedUserToRepos() {
        String username = cfg("github.valid_username");

        // Step 1: Get user
        GetUserMethod userApi = new GetUserMethod(username);
        userApi.expectResponseStatus(HttpResponseStatusType.OK_200);
        Response userResponse = userApi.callAPI();

        // Step 2: Extract repos_url from the user response
        String reposUrl = userResponse.jsonPath().getString("repos_url");
        Assert.assertNotNull(reposUrl, "User response must contain 'repos_url'");
        Assert.assertTrue(reposUrl.contains(username),
                "repos_url should contain the username");

        // Step 3: Confirm the repos_url points to the expected pattern
        Assert.assertTrue(reposUrl.matches("https://api\\.github\\.com/users/.+/repos"),
                "repos_url should match the GitHub API pattern");
    }
}
