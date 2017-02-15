package io.elastic.petstore.triggers;

import io.elastic.api.Component;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.petstore.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

public class TempWebhookTrigger implements Component{

    private static final Logger logger = LoggerFactory.getLogger(TempWebhookTrigger.class);
    private JsonArray pets;
    @Override
    public JsonObject startup(JsonObject configuration) {

        logger.info("Starting up TempWebhookTrigger");

        // access the value of the apiKey field defined in credentials section of component.json
        final JsonString apiKey = configuration.getJsonString("apiKey");
        if (apiKey == null) {
            throw new IllegalStateException("apiKey is required");
        }

        pets = ClientBuilder.newClient()
                .target(Constants.PETSTORE_API_BASE_URL)
                .path(Constants.FIND_PETS_BY_STATUS_PATH)
                .queryParam("status", "available")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(Constants.API_KEY_HEADER, apiKey.getString())
                .get(JsonArray.class);

        logger.info("Startup got {} pets", pets.size());

        return pets.getValuesAs(JsonObject.class).stream().findFirst().get();
    }

    @Override
    public void init(JsonObject configuration) {

        logger.info("Initializing TempWebhookTrigger");

        if (pets != null) {

            logger.info("Init got {} pets from startup", pets.size());
        }

        // access the value of the apiKey field defined in credentials section of component.json
        final JsonString apiKey = configuration.getJsonString("apiKey");
        if (apiKey == null) {
            throw new IllegalStateException("apiKey is required");
        }

        final JsonArray pets = ClientBuilder.newClient()
                .target(Constants.PETSTORE_API_BASE_URL)
                .path(Constants.FIND_PETS_BY_STATUS_PATH)
                .queryParam("status", "available")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(Constants.API_KEY_HEADER, apiKey.getString())
                .get(JsonArray.class);

        logger.info("Init got {} pets", pets.size());
    }

    @Override
    public void execute(ExecutionParameters parameters) {

        logger.info("Execute TempWebhookTrigger");

        if (pets != null) {

            logger.info("Execute got {} pets from startup", pets.size());
        }


        final Message data
                = new Message.Builder().body(Json.createObjectBuilder().add("message", "foo").build()).build();

        logger.info("Emitting data");

        // emitting the message to the platform
        parameters.getEventEmitter().emitData(data);
    }
}
