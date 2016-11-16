package com.boot;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.boot.model.Shipwreck;
import com.boot.repository.ShipwreckRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(App.class)
@WebIntegrationTest("server.port:0") // use random port
public class ShipwreckControllerWebIntegrationTest {

	@Autowired
	ShipwreckRepository repository;
	
	@Value("${local.server.port}")
    private int port;
		
	private Shipwreck sw;

	private String baseUrl;
	
	
	
	@Before
	public void setUp() {
		repository.deleteAll();
		
		sw = new Shipwreck();
		sw.setName("Titanic");
		repository.saveAndFlush(sw);
		
		Shipwreck sw2 = new Shipwreck();
		sw2.setName("Lusitania");
		repository.saveAndFlush(sw2);

	    baseUrl = "http://localhost:"+ port +"/api/v1/shipwrecks";
	}
	
	
	@Test
	public void testListEmpty() throws JsonProcessingException, IOException {
		repository.deleteAll();
		RestTemplate restTemplate = new TestRestTemplate();

		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);
		
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode responseJsonNode = objectMapper.readTree(response.getBody());
		
		assertThat(responseJsonNode.isMissingNode(), is(false));
		assertThat(responseJsonNode.toString(), equalTo("[]"));
		
	}
	
	@Test
	public void testListAll() throws JsonProcessingException, IOException {
		RestTemplate restTemplate = new TestRestTemplate();
		
		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);
		
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode responseJsonNode = objectMapper.readTree(response.getBody());

		assertThat(responseJsonNode.isMissingNode(), is(false));
		assertThat(responseJsonNode.findValue("name").asText(), is("Titanic"));
		
	}
	

	@Test
	public void testDelete() throws JsonProcessingException, IOException {
		RestTemplate restTemplate = new TestRestTemplate();
		String url = baseUrl + "/{id}";


	    Map<String, String> params = new HashMap<String, String>();
	    params.put("id", repository.findOne(sw.getId()).getId().toString());
		restTemplate.delete(url, params);
		Shipwreck result = repository.findOne(sw.getId());
		assertThat(result, is(nullValue()));
	}
	

	
	@Test
	public void testCreateNewShipwreck() {
		repository.deleteAll();
		Shipwreck wreck = new Shipwreck();
		final String wreckName = "Atlantis";
		wreck.setName(wreckName);
		
		RestTemplate restTemplate = new TestRestTemplate();
		ResponseEntity<Shipwreck> response =
				restTemplate.postForEntity(baseUrl, wreck, Shipwreck.class);
		
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(repository.count(), is(greaterThan(0L)));
		List<Shipwreck> resultList = repository.findAll();
		Shipwreck result = resultList.get(0);
		assertThat(result.getName(), is(equalTo("Atlantis")));

	}
	
	
}
