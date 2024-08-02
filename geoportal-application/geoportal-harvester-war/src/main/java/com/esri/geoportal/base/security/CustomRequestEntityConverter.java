/*
 * Copyright 2024 cont_anki.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.esri.geoportal.base.security;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author cont_anki
 */
public class CustomRequestEntityConverter implements Converter<OAuth2UserRequest, RequestEntity<?>> {

	private static final MediaType DEFAULT_CONTENT_TYPE = MediaType
		.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

	/**
	 * Returns the {@link RequestEntity} used for the UserInfo Request.
	 * @param userRequest the user request
	 * @return the {@link RequestEntity} used for the UserInfo Request
	 */
	@Override
	public RequestEntity<?> convert(OAuth2UserRequest userRequest) {
//                String name = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
//                System.out.println("Pricipal name "+name);
                
		ClientRegistration clientRegistration = userRequest.getClientRegistration();
		HttpMethod httpMethod = getHttpMethod(clientRegistration);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                
                String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
                
                Map<String, Object> reqParamMap = userRequest.getAdditionalParameters();
                String userName="";
                if(reqParamMap.get(userNameAttributeName) != null)
                {
                    userName = (String)reqParamMap.get(userNameAttributeName);
                }
                String userInfoUri = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri();
                userInfoUri =userInfoUri+"/"+userName+"?f=json";
                
		URI uri = UriComponentsBuilder
			.fromUriString(userInfoUri)
			.build()
			.toUri();
                


		RequestEntity<?> request;
		if (HttpMethod.POST.equals(httpMethod)) {
			headers.setContentType(DEFAULT_CONTENT_TYPE);
			MultiValueMap<String, String> formParameters = new LinkedMultiValueMap<>();
			formParameters.add(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue());
			request = new RequestEntity<>(formParameters, headers, httpMethod, uri);
		}
		else {
			headers.setBearerAuth(userRequest.getAccessToken().getTokenValue());
			request = new RequestEntity<>(headers, httpMethod, uri);
		}

		return request;
	}

	private HttpMethod getHttpMethod(ClientRegistration clientRegistration) {
		if (AuthenticationMethod.FORM
			.equals(clientRegistration.getProviderDetails().getUserInfoEndpoint().getAuthenticationMethod())) {
			return HttpMethod.POST;
		}
		return HttpMethod.GET;
	}

}
