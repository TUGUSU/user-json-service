package com.example.userjsonservice.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TokenValidationService {

	private static final String SOAP_URL = "http://soap-service:8080/ws";

	public boolean validateToken(String token) {
		try {
			RestTemplate restTemplate = new RestTemplate();

			String soapRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
					+ "xmlns:aut=\"http://example.com/auth\">" + "<soapenv:Header/>" + "<soapenv:Body>"
					+ "<aut:ValidateTokenRequest>" + "<aut:token>" + token + "</aut:token>"
					+ "</aut:ValidateTokenRequest>" + "</soapenv:Body>" + "</soapenv:Envelope>";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.TEXT_XML);

			HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);

			ResponseEntity<String> response = restTemplate.exchange(SOAP_URL, HttpMethod.POST, requestEntity,
					String.class);

			String responseBody = response.getBody();

			System.out.println("SOAP ValidateToken response: " + responseBody);

			return responseBody != null && (responseBody.contains("<ns2:valid>true</ns2:valid>")
					|| responseBody.contains("<ns3:valid>true</ns3:valid>")
					|| responseBody.contains("<valid>true</valid>"));

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
