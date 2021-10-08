package uml.statemachine.camel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class SSMService {

    @Autowired
    @Qualifier(value="config_one")
    private StateMachineFactory<String, String> stateMachineFactoryOne;

    @Autowired
    @Qualifier(value="config_two")
    private StateMachineFactory<String, String> stateMachineFactoryTwo;

    public SSMService() {
    }

    public StateMachine<String, String> acquireMachine(String id, String version){
        if (version.equals("one")){
            return stateMachineFactoryOne.getStateMachine(id);
        }
        else{
            return stateMachineFactoryTwo.getStateMachine(id);
        }
    }

    public void triggerSSM(Exchange exchange) {

        Message camelMessage = exchange.getIn();
        //System.out.println(camelMessage.getBody());

        //Map<String, Object> payload = (Map<String, Object>) camelMessage.getBody();
        ObjectMapper mapObject = new ObjectMapper();
        Map<String, Object> mapObj = mapObject.convertValue(camelMessage.getBody(), Map.class);
        List<String> errors = new LinkedList<String>();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(mapObj);
            System.out.println(json);

            JSONObject jsons = new JSONObject(mapObj);

            JSONArray items = jsons.getJSONArray("test_list");
            for (int i = 0; i < items.length(); ++i) {
                JSONObject rec = items.getJSONObject(i);
                String test = rec.getString("test");
                System.out.println(test);

                if (test.equals("START")) {
                    final StateMachine<String, String> stateMachineOne = this.stateMachineFactoryOne.getStateMachine("machineone");
                    stateMachineOne.getExtendedState().getVariables().put("foo", "machine1");
                    stateMachineOne.start();
                }
            }

        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
	
	public void PingSSM(Exchange exchange) {

        Message camelMessage = exchange.getIn();

        ObjectMapper mapObject = new ObjectMapper();
        Map<String, Object> mapObj = mapObject.convertValue(camelMessage.getBody(), Map.class);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(mapObj);
            System.out.println(json);

            JSONObject jsons = new JSONObject(mapObj);
            String enter_ping = jsons.getString("request");
			
			Map<String, Object> out = new HashMap<>();
            out.put("response","pong");

            Map<String, Object> out2 = new HashMap<>();
            out2.put("error","please enter word `ping` in the request");

            if (enter_ping.toLowerCase().equals("ping")) {

                exchange.getOut().setBody(out);
                exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);

            }else{
                exchange.getOut().setBody(out2);
                exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }

    }

}