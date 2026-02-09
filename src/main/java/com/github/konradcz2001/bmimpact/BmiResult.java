package com.github.konradcz2001.bmimpact;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Document representing a single BMI calculation result stored in MongoDB.
 * Mapped to the "results" collection.
 */
@Document(collection = "results")
public class BmiResult {

    @Id
    private String id;

    private String username; // Owner of the result (null if anonymous/legacy)
    private String name;     // Label/Name of the measurement given by user
    private int height;
    private int weight;
    private double bmi;
    private BmiCategory category;
    private Date timestamp;

    public BmiResult() {
    }

    public BmiResult(String username, String name, int height, int weight, double bmi, BmiCategory category, Date timestamp) {
        this.username = username;
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.bmi = bmi;
        this.category = category;
        this.timestamp = timestamp;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public BmiCategory getCategory() {
        return category;
    }

    public void setCategory(BmiCategory category) {
        this.category = category;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}