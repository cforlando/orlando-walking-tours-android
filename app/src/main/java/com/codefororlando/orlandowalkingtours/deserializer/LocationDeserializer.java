package com.codefororlando.orlandowalkingtours.deserializer;
import com.codefororlando.orlandowalkingtours.models.Location;
import com.codefororlando.orlandowalkingtours.utilities.DevelopmentUtilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by MarkoPhillipMarkovic on 5/10/2016.
 */
public class LocationDeserializer implements JsonDeserializer<Location> {
    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObject = json.getAsJsonObject();
        if(jObject!=null){
            String loctype = "";
            double latitude = 0.0;
            double longitude = 0.0;

            if(jObject.get("type")!=null){
                loctype = jObject.get("type").toString();
            }

            if(jObject.get("coordinates")!=null){
                JsonArray coords = jObject.get("coordinates").getAsJsonArray();

                if(coords.size() > 1){
                    /* we receive coordinates as an array and have to get the longitude and latitude respectively
                        ex: "coordinates": [28.53608693900003, -81.37636357399998]
                    */

                    latitude = coords.get(1).getAsDouble();
                    longitude = coords.get(0).getAsDouble();

                    DevelopmentUtilities.logE("Adding lat" + String.valueOf(latitude) + "-" + String.valueOf(longitude));
                }
            }

            Location result = new Location(loctype, latitude, longitude);
            return result;
        }
        return null;
    }
}
