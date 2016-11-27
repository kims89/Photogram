package com.photogram.Storage;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Her er upload-klassen. De sørger for at det er mulig å laste opp filer til en mappe kalt upload-dir.
 */

@SpringBootApplication
@ComponentScan({"com.photogram.Storage"})
@EntityScan("com.photogram.domain")
@EnableMongoRepositories({"com.photogram.Repository"})

@ConfigurationProperties("storage")
public class StorageProperties {

    //Her settes lokasjonen hvor bildene skal lagres.
    private String location = "upload-dir";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}