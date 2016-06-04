package com.codefororlando.orlandowalkingtours.deserializer;

import com.codefororlando.orlandowalkingtours.models.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.models.Location;
import com.codefororlando.orlandowalkingtours.utilities.DevelopmentUtilities;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by MarkoPhillipMarkovic on 5/10/2016.
 */
public class HistoricLandmarkDeserializer implements JsonDeserializer<HistoricLandmark> {
    @Override
    public HistoricLandmark deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObject = json.getAsJsonObject();
        if(jObject !=null){
            int id = -1;
            String address = "";
            String name = "";
            String location_city = "";
            String location_state = "";
            String location_location = "";
            String loctype = "";
            Location location = null;
            String thumbnail = "";
            String fullsizeimage = "";
            String description = "";

            Date local = new Date();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);

            if(jObject.get("id") != null){
                try {
                    id = Integer.parseInt(jObject.get("id").getAsString());
                } catch(NumberFormatException nfe) {
                    DevelopmentUtilities.logE("Could not parse location id " + nfe.getMessage());
                }
            }

            if(jObject.get("address")!=null){
                address = jObject.get("address").getAsString();
            }

            if(jObject.get("downtown_walking_tour")!=null){
                description = jObject.get("downtown_walking_tour").getAsString();
            }

            if(jObject.get("local") != null){
                try{
                    local = formatter.parse(jObject.get("local").getAsString());
                }
                catch(Exception e){

                }
            }

            if(jObject.get("location")!=null){
                GsonBuilder gsonBldr = new GsonBuilder();
                gsonBldr.registerTypeAdapter(Location.class, new LocationDeserializer());
                location = gsonBldr.create().fromJson(jObject.get("location"), Location.class);
            }

            if(jObject.get("name")!=null){
                name = jObject.get("name").getAsString();
            }

            if(jObject.get("location_city")!=null){
                location_city = jObject.get("location_city").getAsString();
            }

            if(jObject.get("location_state")!=null){
                location_state = jObject.get("location_state").getAsString();
            }

            if(jObject.get("location_location")!=null){
                location_location = jObject.get("location_location").getAsString();
            }

            if(jObject.get("type")!=null){
                loctype = jObject.get("type").getAsString();
            }

            if(jObject.get("thumbnail")!=null){
                thumbnail = jObject.get("thumbnail").getAsString();
            }

            if(jObject.get("full_size_image")!=null)
            {
                fullsizeimage = jObject.get("full_size_image").getAsString();

            }

            HistoricLandmark result = new HistoricLandmark(id, address, local, location, location_location, location_city, location_state, name, description, loctype, thumbnail, fullsizeimage);
            return result;
        }

        return null;

    }
}
