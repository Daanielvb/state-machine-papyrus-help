package uml.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.statemachine.StateMachine;
import org.springframework.context.ConfigurableApplicationContext;
import uml.statemachine.camel.SSMService;


@SpringBootApplication
@ComponentScan(basePackages = {"uml.statemachine.*"})
public class UmlSpringStateMachineApplication implements CommandLineRunner {
	
	private static ConfigurableApplicationContext context;

    @Autowired
    private SSMService stateMachineService;

    public static void main(String[] args) {
         context = SpringApplication.run(UmlSpringStateMachineApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        final StateMachine machineOne = stateMachineService.acquireMachine("1", "one");
        final StateMachine machineTwo = stateMachineService.acquireMachine("1", "two");
    	synchronized (machineOne) {
            machineOne.getExtendedState().getVariables().put("foo", "machine1");
            machineOne.start();
			 //stateMachineOne.wait();
		}
       
        synchronized (machineTwo) {
            if(machineOne.isComplete()) {
                machineTwo.getExtendedState().getVariables().put("foo", (String) machineOne.getExtendedState().getVariables().get("foo"));
                machineTwo.start();
            }
		}
        
    }

   
}
