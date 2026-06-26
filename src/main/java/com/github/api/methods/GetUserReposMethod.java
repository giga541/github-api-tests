package com.github.api.methods;

import com.zebrunner.carina.api.AbstractApiMethodV2;
import com.zebrunner.carina.api.annotation.Endpoint;
import com.zebrunner.carina.api.annotation.ResponseTemplatePath;
import com.zebrunner.carina.api.annotation.SuccessfulHttpStatus;
import com.zebrunner.carina.api.http.HttpMethodType;
import com.zebrunner.carina.api.http.HttpResponseStatusType;

@Endpoint(url = "${config.github.base_url}/users/${username}/repos", methodType = HttpMethodType.GET)
@ResponseTemplatePath(path = "api/github/users/repos/_get/rs.json")
@SuccessfulHttpStatus(status = HttpResponseStatusType.OK_200)
public class GetUserReposMethod extends AbstractApiMethodV2 {

    public GetUserReposMethod(String username) {
        replaceUrlPlaceholder("username", username);
    }
}
