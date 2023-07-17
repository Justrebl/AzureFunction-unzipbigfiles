package tlhd.dev;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.EventGridTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;

/**
 * Azure Functions with Event Grid Trigger.
 */
public class Function {

    @FunctionName("EventGridExample")
    public void run(
            @EventGridTrigger(
                name = "event"
            ) 
            EventSchema content,
            final ExecutionContext context) {
        context.getLogger().info("Event Grid Triggered processed a request.");
        context.getLogger().info("Event Subject : "+ content.subject); 
        
        // // Parse query parameter
        // final String query = request.getQueryParameters().get("name");
        // final String name = request.getBody().orElse(query);
    }
}