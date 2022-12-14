package com.kbeauty.gbt.oauth;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;



public class KakaoExample {
	private static final String NETWORK_NAME = "Kakao";
	private static final String PROTECTED_RESOURCE_URL = "https://kapi.kakao.com/v2/user/me";

	private KakaoExample() {
	}

	@SuppressWarnings("PMD.SystemPrintln")
	public static void main(String... args) throws IOException, InterruptedException, ExecutionException {
		// Replace this with your client id
		
//		custom.oauth2.kakao.client-id=5568486fc48295f2f1cba3496bb20b4b
//		custom.oauth2.kakao.client-secret=PjTVfArLDLYTX59y91dT5V5G03od0yRd
				
//		@Value("${custom.oauth2.kakao.client-id}") 
		String clientId = "5568486fc48295f2f1cba3496bb20b4b";
		String clientSecret = "PjTVfArLDLYTX59y91dT5V5G03od0yRd";

		final OAuth20Service service = new ServiceBuilder(clientId).apiSecret(clientSecret).callback("http://localhost:8080/login/oauth2/code/kakao")
				.build(KakaoApi.instance());

		final Scanner in = new Scanner(System.in, "UTF-8");

		System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
		System.out.println();

		// Obtain the Authorization URL
		System.out.println("Fetching the Authorization URL...");
		final String authorizationUrl = service.getAuthorizationUrl();
		System.out.println("Got the Authorization URL!");
		System.out.println("Now go and authorize ScribeJava here:");
		System.out.println(authorizationUrl);
		System.out.println("And paste the authorization code here");
		System.out.print(">>");
		final String code = in.nextLine();
		System.out.println();

		System.out.println("Trading the Authorization Code for an Access Token...");
		final OAuth2AccessToken accessToken = service.getAccessToken(code);

		System.out.println("Got the Access Token!");
		System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
		System.out.println();

		// Now let's go and ask for a protected resource!
		System.out.println("Now we're going to access a protected resource...");
		final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
		service.signRequest(accessToken, request);
		try (Response response = service.execute(request)) {
			System.out.println("Got it! Lets see what we found...");
			System.out.println();
			System.out.println(response.getCode());
			System.out.println(response.getBody());
		}

		System.out.println();
		System.out.println("Thats it man! Go and build something awesome with ScribeJava! :)");

	}
}
