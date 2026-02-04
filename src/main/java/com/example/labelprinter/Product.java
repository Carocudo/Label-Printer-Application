package com.example.labelprinter;

import java.util.Objects;

public class Product {
    private String code;
    private String name;

    public Product(String code, String name) {
        this.code = code == null ? "" : code.trim();
        this.name = name == null ? "" : name.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? "" : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? "" : name.trim();
    }

    @Override
    public String toString() {
        if (name.isBlank()) {
            return code;
        }
        if (code.isBlank()) {
            return name;
        }
        return code + " - " + name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Product product = (Product) other;
        return Objects.equals(code, product.code) && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name);
    }
}
