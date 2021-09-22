package test.umlspringstatemachine;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class Actions extends StateMachineConfigurerAdapter<String, String> {

	Logger log;
	java.util.logging.Logger logs;

    @Bean
    public Action<String, String> sysoName() {
        return context -> {
			System.out.println(context.getStateMachine().getId());
			System.out.println((String)context.getExtendedState().getVariables().get("foo"));
			//log.info(context.getStateMachine().getId());
		    //log.info((String)context.getExtendedState().getVariables().get("foo"));
		    
		    if(("machineone").equalsIgnoreCase(context.getStateMachine().getId())) {
		        context.getExtendedState().getVariables().put("foo", "machine one finished, start machine 2");
		    }
		};
    }

	@Bean
	public Action<String, String> sysoName2() {
		return context -> {
			
			Map<String, Object> json = new HashMap<>();
			json.put("request","ping");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<?> entity = new HttpEntity<>(json,headers);

			RestTemplate restTemplate = new RestTemplate();
			String authUri = "http://10.10.1.20:8080/api/SSM/ping";
			ResponseEntity<Object> response =
					restTemplate.exchange(authUri, HttpMethod.POST, entity, Object.class);

			//System.out.println("Create Resource: " + response2);

			ObjectMapper objectMapper = new ObjectMapper();

			try {
				String jsons = objectMapper.writeValueAsString(response.getBody());
				System.out.println(jsons);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

		};
	}
}
