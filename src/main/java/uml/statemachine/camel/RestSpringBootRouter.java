package uml.statemachine.camel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;

import static org.apache.camel.model.rest.RestParamType.body;

import uml.statemachine.swagger_examples.ping.Ping;
import uml.statemachine.swagger_examples.test.Test;

/**
 * @author SP.
 * @date JUN 2020
 * @description Camel REST DSL and routing
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */

@Component
public class RestSpringBootRouter extends RouteBuilder {

    @Value("${camel.component.servlet.mapping.context-path}")
    private String contextPath;

    public class ReadJsonAsString {

        String file;
        String json;

        public String readFileAsString(String file)throws Exception
        {
            return new String(Files.readAllBytes(Paths.get(file)));
        }

        public String getJson(String s) {
            file = "src/main/resources/" + s;
            {
                try {
                    json = readFileAsString(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return json;
        }

    }

    // Redirects for Swagger UI

    @Controller
    public class SwaggerConfig {
        @RequestMapping("/api-doc")
        public String redirectToUi() {
            return "redirect:/webjars/swagger-ui/index.html?url=/api/swagger&validatorUrl=";
        }
    }


    @Controller
    public class SwaggerConfig2 {
        @RequestMapping("/swagger")
        public String redirectToUi() {
            return "redirect:/webjars/swagger-ui/index.html?url=/api/swagger&validatorUrl=";
        }
    }
	

    @Override
    public void configure() throws Exception {

        ReadJsonAsString readJson = new ReadJsonAsString();
        ObjectMapper mapper = new ObjectMapper();

        // REST DSL CONFIG =======================================================================================

        String listenAddress = "0.0.0.0";
        int listenPort = 8080;

        restConfiguration()
                .component("servlet")
                //.component("netty-http")
                //.component("jetty")
                .scheme("http")
                .host(listenAddress)
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)
                .port(listenPort)
                .apiContextPath("/swagger") //swagger endpoint path
                .apiContextRouteId("swagger") //id of route providing the swagger endpoint
                .contextPath("/api") //base.path swagger property; use the mapping path set for CamelServlet
                .apiProperty("api.title", "DEV API")
                .apiProperty("api.version", "1.0.0-DEV")
                .apiProperty("api.description", "Development OpenAPI Documentation");

        onException(JsonParseException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                .setBody().constant("Invalid json data. Body must not be empty.");

        // REST DSL (SWAGGER) ===============================================================================================

        rest("/SSM").description("Spring State Machine functions").consumes("application/json").produces("application/json")
				// setup security definitions
				//.securityDefinitions()
				//	.apiKey("api_key").withHeader("myHeader").end()
				//.end()
                .post("/trigger").id("trigger")//.security("api_key")
                .description("Function to trigger SSM")
                .type(Test.class).param().name("body").type(body)
				//.description(readJson.getJson("SSM_trigger_json_example.txt"))
				.required(true).endParam()
                .route().bean(new SSMService(), "triggerSSM");
				
		rest("/SSM").description("Spring State Machine functions").consumes("application/json").produces("application/json")
				// setup security definitions
				//.securityDefinitions()
				//	.apiKey("api_key").withHeader("myHeader").end()
				//.end()
                .post("/ping").id("ping")//.security("api_key")
                .description("Function to test HTTP call")
                .type(Ping.class).param().name("body").type(body)
				//.description(readJson.getJson("SSM_trigger_json_example.txt"))
				.required(true).endParam()
                .route().bean(new SSMService(), "PingSSM");

        // CAMEL COMPONENTS (KAFKA)

        from("kafka:test_topic?brokers=10.10.1.10:9092")
                .log("Message received from Kafka : ${body}")
                //.log("    on the topic ${headers[kafka.TOPIC]}")
                //.log("    on the partition ${headers[kafka.PARTITION]}")
                //.log("    with the offset ${headers[kafka.OFFSET]}")
                //.log("    with the key ${headers[kafka.KEY]}")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        String payload = exchange.getIn().getBody(String.class);
                        Map<String, String> map = mapper.readValue(payload, Map.class);
						exchange.getIn().setBody(map);
                    }
                })
                .bean(new SSMService(), "triggerSSM");


    }

}
