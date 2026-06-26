package com.github.api.methods;

import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.ResponseTemplatePath;
import com.zebrunner.carina.api.annotation.SuccessfulHttpStatus;
import com.zebrunner.carina.api.http.HttpMethodType;
import com.zebrunner.carina.api.http.HttpResponseStatusType;

@Endpoint(url = "${config.env.api_url}/search/repositories?q=${query}", methodType = HttpMethodType.GET)
@ResponseTemplatePath(path = "api/github/search/repositories/_get/rs.json")
@SuccessfulHttpStatus(status = HttpResponseStatusType.OK_200)
public class SearchRepositoriesMethod extends AbstractApiMethodV2 {

    public SearchRepositoriesMethod(String query) {
        replaceUrlPlaceholder("query", query);
    }
}